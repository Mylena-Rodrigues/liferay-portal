/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.pricing.internal.quota;

import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.CountTokensResponse;
import com.google.cloud.vertexai.generativeai.GenerativeModel;

import com.liferay.account.model.AccountEntry;
import com.liferay.ai.hub.configuration.VertexAIConfiguration;
import com.liferay.ai.hub.pricing.internal.converter.util.TokenConverterUtil;
import com.liferay.ai.hub.pricing.rest.util.ObjectEntryUtil;
import com.liferay.ai.hub.quota.QuotaManager;
import com.liferay.ai.hub.quota.Usage;
import com.liferay.ai.hub.util.AccountEntryUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.object.rest.manager.v1_0.DefaultObjectEntryManager;
import com.liferay.object.rest.manager.v1_0.DefaultObjectEntryManagerProvider;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManagerRegistry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.concurrent.DCLSingleton;
import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.lock.DuplicateLockException;
import com.liferay.portal.kernel.lock.Lock;
import com.liferay.portal.kernel.lock.LockManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.BigDecimalUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portal.lock.service.LockLocalService;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;

import jakarta.persistence.PersistenceException;

import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;

import java.math.BigDecimal;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeoutException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carolina Barbosa
 */
@Component(service = QuotaManager.class)
public class DefaultQuotaManager implements QuotaManager {

	@Override
	public void addQuotas(long accountEntryId, long companyId, long userId)
		throws PortalException {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_AI_HUB_QUOTA", companyId);

		DefaultObjectEntryManager defaultObjectEntryManager =
			_getDefaultObjectEntryManager(objectDefinition);

		try {
			defaultObjectEntryManager.updateObjectEntry(
				companyId,
				_getDefaultDTOConverterContext(
					_userLocalService.getUser(userId)),
				"quota-" + accountEntryId, objectDefinition,
				new ObjectEntry() {
					{
						setProperties(
							() -> HashMapBuilder.<String, Object>put(
								"r_accountToAIHubQuotas_accountEntryId",
								accountEntryId
							).build());
					}
				},
				null);
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	@Override
	public Closeable checkConcurrentRequests(long userId)
		throws PortalException {

		long currentTimeMillis = System.currentTimeMillis();

		Semaphore semaphore = _semaphoreDCLSingleton.getSingleton(
			() -> new Semaphore(10 - _expirationTimes.size()));

		for (Map.Entry<String, Long> entry : _expirationTimes.entrySet()) {
			if ((entry.getValue() < currentTimeMillis) &&
				_expirationTimes.remove(entry.getKey(), entry.getValue())) {

				semaphore.release();
			}
		}

		if (!semaphore.tryAcquire()) {
			throw new UnsupportedOperationException(
				"You have exceeded your concurrent request limit");
		}

		AccountEntry accountEntry = AccountEntryUtil.getUserAccountEntry(
			userId);

		boolean acquired = false;

		try {
			String prefix = accountEntry.getAccountEntryId() + ":";

			Set<String> keys = _getKeys(prefix);

			String owner = PortalUUIDUtil.generate();

			for (int i = 0; i < 10; i++) {
				String key = prefix + Math.floorMod(owner.hashCode() + i, 10);

				if (keys.contains(key)) {
					continue;
				}

				try {
					Lock lock = LockManagerUtil.lock(
						userId, QuotaManager.class.getName(), key, owner, false,
						Time.MINUTE, false);

					if (Objects.equals(lock.getOwner(), owner)) {
						_expirationTimes.put(
							owner, System.currentTimeMillis() + Time.MINUTE);

						acquired = true;

						return () -> _release(lock);
					}
				}
				catch (DuplicateLockException | PersistenceException
							exception) {

					if (_log.isDebugEnabled()) {
						_log.debug(exception);
					}
				}
			}

			throw new UnsupportedOperationException(
				"You have exceeded your concurrent request limit");
		}
		finally {
			if (!acquired) {
				semaphore.release();
			}
		}
	}

	@Override
	public void checkTokensUsage(long companyId, long userId)
		throws PortalException {

		if (ListUtil.isEmpty(
				ObjectEntryUtil.getQuotaBlockObjectEntries(
					companyId, _dtoConverterRegistry,
					_objectEntryManagerRegistry, userId))) {

			throw new UnsupportedOperationException(
				"You have exceeded your token quota");
		}
	}

	@Override
	public void updateUsage(long companyId, Usage usage, long userId)
		throws PortalException {

		BigDecimal tokenCount = _getTokenCount(companyId, usage);

		if (tokenCount.signum() == 0) {
			return;
		}

		for (ObjectEntry quotaBlockObjectEntry :
				ObjectEntryUtil.getQuotaBlockObjectEntries(
					companyId, _dtoConverterRegistry,
					_objectEntryManagerRegistry, userId)) {

			try (Closeable closeable = _lock(quotaBlockObjectEntry.getId())) {
				com.liferay.object.model.ObjectEntry
					serviceBuilderQuotaBlockObjectEntry =
						_objectEntryLocalService.getObjectEntry(
							quotaBlockObjectEntry.getId());

				Map<String, Serializable> values =
					serviceBuilderQuotaBlockObjectEntry.getValues();

				com.liferay.object.model.ObjectEntry
					serviceBuilderConversionTableObjectEntry =
						_objectEntryLocalService.getObjectEntry(
							MapUtil.getLong(
								values,
								"r_aiHubQuotaCTToAIHubQuotaBlocks_l_" +
									"aiHubQuotaConversionTableId"));

				BigDecimal lrtCount = TokenConverterUtil.convertTokenToLRT(
					serviceBuilderConversionTableObjectEntry.getValues(),
					usage.getSource(), tokenCount);

				BigDecimal remainingBalance = (BigDecimal)values.get(
					"remainingBalance");

				BigDecimal consumedLRTCount = lrtCount.min(remainingBalance);

				_objectEntryLocalService.partialUpdateObjectEntry(
					userId,
					serviceBuilderQuotaBlockObjectEntry.getObjectEntryId(), 0,
					HashMapBuilder.<String, Serializable>put(
						"remainingBalance",
						remainingBalance.subtract(consumedLRTCount)
					).build(),
					new ServiceContext() {
						{
							setCompanyId(companyId);
							setUserId(userId);
						}
					});

				if (BigDecimalUtil.eq(consumedLRTCount, lrtCount)) {
					break;
				}

				BigDecimal consumedTokenCount =
					TokenConverterUtil.convertLRTToToken(
						serviceBuilderConversionTableObjectEntry.getValues(),
						consumedLRTCount, usage.getSource());

				tokenCount = tokenCount.subtract(consumedTokenCount);

				if (tokenCount.signum() <= 0) {
					break;
				}
			}
			catch (IOException ioException) {
				throw new RuntimeException(ioException);
			}
		}
	}

	private DefaultDTOConverterContext _getDefaultDTOConverterContext(
		User user) {

		return new DefaultDTOConverterContext(
			_dtoConverterRegistry, null, user.getLocale(), null, user);
	}

	private DefaultObjectEntryManager _getDefaultObjectEntryManager(
		ObjectDefinition objectDefinition) {

		return DefaultObjectEntryManagerProvider.provide(
			_objectEntryManagerRegistry.getObjectEntryManager(
				objectDefinition.getCompanyId(),
				objectDefinition.getStorageType()));
	}

	private Set<String> _getKeys(String keyPrefix) {
		return new HashSet<>(
			_lockLocalService.dynamicQuery(
				_lockLocalService.dynamicQuery(
				).add(
					PropertyFactoryUtil.forName(
						"className"
					).eq(
						QuotaManager.class.getName()
					)
				).add(
					PropertyFactoryUtil.forName(
						"expirationDate"
					).gt(
						new Date()
					)
				).add(
					PropertyFactoryUtil.forName(
						"key"
					).like(
						keyPrefix + "%"
					)
				).setProjection(
					ProjectionFactoryUtil.property("key")
				)));
	}

	private BigDecimal _getTokenCount(long companyId, Usage usage)
		throws PortalException {

		if (Validator.isNull(usage.getText())) {
			return BigDecimal.valueOf(usage.getTokenCount());
		}

		VertexAIConfiguration vertexAIConfiguration =
			ConfigurationProviderUtil.getCompanyConfiguration(
				VertexAIConfiguration.class, companyId);

		String location = vertexAIConfiguration.location();
		String modelName = vertexAIConfiguration.modelName();

		if (Objects.equals(location, "global")) {
			location = "europe-central2";
			modelName = "gemini-2.5-flash";
		}

		try (VertexAI vertexAI = new VertexAI(
				vertexAIConfiguration.projectId(), location)) {

			GenerativeModel generativeModel = new GenerativeModel(
				modelName, vertexAI);

			CountTokensResponse countTokensResponse =
				generativeModel.countTokens(usage.getText());

			return BigDecimal.valueOf(countTokensResponse.getTotalTokens());
		}
		catch (IOException ioException) {
			throw new PortalException(ioException);
		}
	}

	private Closeable _lock(long objectEntryId) throws PortalException {
		String updatedOwner = PortalUUIDUtil.generate();

		long deadline = System.currentTimeMillis() + (10 * Time.SECOND);

		while (true) {
			Lock lock = LockManagerUtil.lock(
				DefaultQuotaManager.class.getName(),
				String.valueOf(objectEntryId), null, updatedOwner);

			if (Objects.equals(lock.getOwner(), updatedOwner)) {
				break;
			}

			if (System.currentTimeMillis() >= deadline) {
				throw new PortalException(new TimeoutException());
			}

			try {
				Thread.sleep(50);
			}
			catch (InterruptedException interruptedException) {
				Thread thread = Thread.currentThread();

				thread.interrupt();

				throw new PortalException(interruptedException);
			}
		}

		return () -> LockManagerUtil.unlock(
			DefaultQuotaManager.class.getName(), String.valueOf(objectEntryId),
			updatedOwner);
	}

	private void _release(Lock lock) {
		LockManagerUtil.unlock(
			QuotaManager.class.getName(), lock.getKey(), lock.getOwner());

		Semaphore semaphore = _semaphoreDCLSingleton.getSingleton(() -> null);

		if ((semaphore != null) &&
			(_expirationTimes.remove(lock.getOwner()) != null)) {

			semaphore.release();
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DefaultQuotaManager.class);

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	private final ConcurrentMap<String, Long> _expirationTimes =
		new ConcurrentHashMap<>();

	@Reference
	private LockLocalService _lockLocalService;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private ObjectEntryManagerRegistry _objectEntryManagerRegistry;

	private final DCLSingleton<Semaphore> _semaphoreDCLSingleton =
		new DCLSingleton<>();

	@Reference
	private UserLocalService _userLocalService;

}