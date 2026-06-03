/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.pricing.rest.internal.resource.v1_0;

import com.liferay.ai.hub.pricing.rest.dto.v1_0.QuotaBlock;
import com.liferay.ai.hub.pricing.rest.resource.v1_0.QuotaBlockResource;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.object.rest.manager.v1_0.DefaultObjectEntryManager;
import com.liferay.object.rest.manager.v1_0.DefaultObjectEntryManagerProvider;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManagerRegistry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;

import java.io.Serializable;

import java.math.BigDecimal;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Carolina Barbosa
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/quota-block.properties",
	scope = ServiceScope.PROTOTYPE, service = QuotaBlockResource.class
)
public class QuotaBlockResourceImpl extends BaseQuotaBlockResourceImpl {

	@Override
	public QuotaBlock postAccountQuotaBlockPurchase(
			Long accountId, QuotaBlock quotaBlock)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled(
				contextCompany.getCompanyId(), "LPD-62272")) {

			throw new UnsupportedOperationException();
		}

		Map<String, Serializable> conversionTableValues =
			_getConversionTableValues();

		ObjectDefinition quotaObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_AI_HUB_QUOTA", contextCompany.getCompanyId());

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					contextCompany.getCompanyId(),
					quotaObjectDefinition.getStorageType()));

		Date purchaseDate = new Date();

		ObjectEntry quotaBlockObjectEntry =
			defaultObjectEntryManager.addRelatedObjectEntry(
				new DefaultDTOConverterContext(
					contextAcceptLanguage.isAcceptAllLanguages(), null, null,
					contextHttpServletRequest, null,
					contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
					contextUser),
				"quota-" + accountId,
				new ObjectEntry() {
					{
						setExternalReferenceCode(quotaBlock::getTransactionId);
						setProperties(
							() -> HashMapBuilder.<String, Object>put(
								"purchaseDate", purchaseDate
							).put(
								"purchaseExpirationDate",
								() -> {
									Calendar calendar = Calendar.getInstance();

									calendar.setTime(purchaseDate);

									calendar.add(Calendar.MONTH, 12);

									return calendar.getTime();
								}
							).put(
								"r_aiHubQuotaCTToAIHubQuotaBlocks_l_" +
									"aiHubQuotaConversionTableId",
								MapUtil.getLong(
									conversionTableValues,
									"l_aiHubQuotaConversionTableId")
							).put(
								"remainingBalance", quotaBlock.getSize()
							).put(
								"size", quotaBlock.getSize()
							).put(
								"transactionId", quotaBlock.getTransactionId()
							).build());
					}
				},
				_objectRelationshipLocalService.getObjectRelationship(
					quotaObjectDefinition.getObjectDefinitionId(),
					"aiHubQuotaToAIHubQuotaBlocks"),
				null);

		Map<String, Object> properties = quotaBlockObjectEntry.getProperties();

		return new QuotaBlock() {
			{
				setExternalReferenceCode(
					quotaBlockObjectEntry::getExternalReferenceCode);
				setId(quotaBlockObjectEntry::getId);
				setPurchaseDate(
					() -> _parseDate(
						MapUtil.getString(properties, "purchaseDate")));
				setPurchaseExpirationDate(
					() -> _parseDate(
						MapUtil.getString(
							properties, "purchaseExpirationDate")));
				setRemainingBalance(() -> (BigDecimal)properties.get("size"));
				setSize(() -> (BigDecimal)properties.get("size"));
				setTransactionId(
					() -> MapUtil.getString(properties, "transactionId"));
				setVersion(
					() -> MapUtil.getDouble(conversionTableValues, "version"));
			}
		};
	}

	private Map<String, Serializable> _getConversionTableValues()
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_AI_HUB_QUOTA_CONVERSION_TABLE",
					contextCompany.getCompanyId());

		List<Map<String, Serializable>> valuesList =
			_objectEntryLocalService.getValuesList(
				0, contextCompany.getCompanyId(), contextUser.getUserId(),
				objectDefinition.getObjectDefinitionId(), null, null, 0, 1,
				new Sort[] {new Sort("version", Sort.DOUBLE_TYPE, true)});

		return valuesList.get(0);
	}

	private Date _parseDate(String dateString) throws Exception {
		return DateUtil.parseDate(
			"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", dateString,
			contextAcceptLanguage.getPreferredLocale());
	}

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private ObjectEntryManagerRegistry _objectEntryManagerRegistry;

	@Reference
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

}