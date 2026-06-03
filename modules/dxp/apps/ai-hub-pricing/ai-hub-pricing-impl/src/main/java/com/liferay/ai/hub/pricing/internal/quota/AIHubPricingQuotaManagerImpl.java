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
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.lock.Lock;
import com.liferay.portal.kernel.lock.LockManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.BigDecimalUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;

import java.math.BigDecimal;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carolina Barbosa
 */
@Component(service = QuotaManager.class)
public class AIHubPricingQuotaManagerImpl implements QuotaManager {

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
	public void checkUsage(long companyId, long userId) throws PortalException {
		if (ListUtil.isEmpty(_getQuotaBlockObjectEntries(companyId, userId))) {
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
				_getQuotaBlockObjectEntries(companyId, userId)) {

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

	private List<ObjectEntry> _getQuotaBlockObjectEntries(
			long companyId, long userId)
		throws PortalException {

		AccountEntry accountEntry = AccountEntryUtil.getUserAccountEntry(
			userId);

		if (accountEntry == null) {
			return Collections.emptyList();
		}

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();
		String name = PrincipalThreadLocal.getName();

		try {
			User user = _userLocalService.getUser(userId);

			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(user));
			PrincipalThreadLocal.setName(user.getUserId());

			ObjectDefinition quotaObjectDefinition =
				_objectDefinitionLocalService.
					getObjectDefinitionByExternalReferenceCode(
						"L_AI_HUB_QUOTA", companyId);

			DefaultObjectEntryManager defaultObjectEntryManager =
				_getDefaultObjectEntryManager(quotaObjectDefinition);

			Page<ObjectEntry> quotaBlockObjectEntriesPage =
				defaultObjectEntryManager.getRelatedObjectEntries(
					null, _getDefaultDTOConverterContext(user),
					"quota-" + accountEntry.getAccountEntryId(),
					StringBundler.concat(
						"purchaseExpirationDate ge ",
						Instant.now(
						).truncatedTo(
							ChronoUnit.SECONDS
						),
						" and remainingBalance gt 0"),
					_objectRelationshipLocalService.getObjectRelationship(
						quotaObjectDefinition.getObjectDefinitionId(),
						"aiHubQuotaToAIHubQuotaBlocks"),
					Pagination.of(QueryUtil.ALL_POS, QueryUtil.ALL_POS), null,
					null,
					new Sort[] {
						new Sort("purchaseDate", Sort.LONG_TYPE, false)
					});

			return ListUtil.fromCollection(
				quotaBlockObjectEntriesPage.getItems());
		}
		catch (Exception exception) {
			_log.error(exception);
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(permissionChecker);
			PrincipalThreadLocal.setName(name);
		}

		return Collections.emptyList();
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
				AIHubPricingQuotaManagerImpl.class.getName(),
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
			AIHubPricingQuotaManagerImpl.class.getName(),
			String.valueOf(objectEntryId), updatedOwner);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AIHubPricingQuotaManagerImpl.class);

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private ObjectEntryManagerRegistry _objectEntryManagerRegistry;

	@Reference
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@Reference
	private UserLocalService _userLocalService;

}