/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.rest.internal.manager.v1_0;

import com.liferay.account.model.AccountEntry;
import com.liferay.ai.hub.constants.AIHubFeatureConstants;
import com.liferay.ai.hub.rest.dto.v1_0.AIFeatures;
import com.liferay.ai.hub.rest.dto.v1_0.AgentDefinition;
import com.liferay.ai.hub.rest.manager.v1_0.AIFeaturesManager;
import com.liferay.ai.hub.rest.manager.v1_0.AgentDefinitionManager;
import com.liferay.ai.hub.util.AccountEntryUtil;
import com.liferay.headless.delivery.dto.v1_0.Creator;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManager;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouterUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import jakarta.ws.rs.BadRequestException;

import java.util.Date;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mylena Monte
 */
@Component(service = AIFeaturesManager.class)
public class AIFeaturesManagerImpl implements AIFeaturesManager {

	@Override
	public AIFeatures getAIFeatures(
			long companyId, DTOConverterContext dtoConverterContext)
		throws Exception {

		AccountEntry accountEntry = AccountEntryUtil.getUserAccountEntry(
			dtoConverterContext.getUserId());

		if (accountEntry == null) {
			return _toAIFeatures(true, null, null);
		}

		ObjectEntry objectEntry = _getLatestFeatureObjectEntry(
			accountEntry, companyId, dtoConverterContext);

		if (objectEntry == null) {
			return _toAIFeatures(true, null, null);
		}

		return _toAIFeatures(
			GetterUtil.getBoolean(objectEntry.getPropertyValue("enable")),
			_getModifiedByName(objectEntry), objectEntry.getDateModified());
	}

	@Override
	public AIFeatures patchAIFeatures(
			boolean enable, long companyId,
			DTOConverterContext dtoConverterContext, String reason,
			String comment)
		throws Exception {

		if (!enable && Validator.isNull(reason)) {
			throw new BadRequestException(
				"A reason is required to disable AI features");
		}

		AccountEntry accountEntry = AccountEntryUtil.getUserAccountEntry(
			dtoConverterContext.getUserId());

		if (accountEntry == null) {
			throw new BadRequestException(
				"The user is not associated with an account");
		}

		Page<AgentDefinition> page =
			_agentDefinitionManager.getAgentDefinitionsPage(
				companyId, dtoConverterContext, null, Pagination.of(1, 10000),
				null, null);

		for (AgentDefinition agentDefinition : page.getItems()) {
			if (!Boolean.TRUE.equals(agentDefinition.getSystem())) {
				continue;
			}

			_agentDefinitionManager.patchAgentDefinitionUpdateActive(
				enable, companyId, dtoConverterContext,
				agentDefinition.getExternalReferenceCode());
		}

		_addFeatureObjectEntry(
			accountEntry, companyId, dtoConverterContext, enable, reason,
			comment);

		_routeAuditMessage(
			accountEntry, dtoConverterContext.getUserId(), enable, reason,
			comment);

		return getAIFeatures(companyId, dtoConverterContext);
	}

	private void _addFeatureObjectEntry(
			AccountEntry accountEntry, long companyId,
			DTOConverterContext dtoConverterContext, boolean enable,
			String reason, String comment)
		throws Exception {

		_objectEntryManager.addObjectEntry(
			dtoConverterContext, _getFeatureObjectDefinition(companyId),
			new ObjectEntry() {
				{
					setProperties(
						() -> HashMapBuilder.<String, Object>put(
							AIHubFeatureConstants.
								OBJECT_FIELD_NAME_ACCOUNT_ENTRY_ID,
							accountEntry.getAccountEntryId()
						).put(
							"comment", _truncate(GetterUtil.getString(comment))
						).put(
							"enable", enable
						).put(
							"featureName",
							AIHubFeatureConstants.FEATURE_NAME_AI_FEATURES
						).put(
							"reason", _truncate(GetterUtil.getString(reason))
						).build());
				}
			},
			null);
	}

	private ObjectDefinition _getFeatureObjectDefinition(long companyId)
		throws Exception {

		return _objectDefinitionLocalService.getObjectDefinition(
			companyId, AIHubFeatureConstants.OBJECT_DEFINITION_NAME);
	}

	private ObjectEntry _getLatestFeatureObjectEntry(
			AccountEntry accountEntry, long companyId,
			DTOConverterContext dtoConverterContext)
		throws Exception {

		Page<ObjectEntry> page = _objectEntryManager.getObjectEntries(
			companyId, _getFeatureObjectDefinition(companyId), null, null,
			dtoConverterContext,
			StringBundler.concat(
				"featureName eq '",
				AIHubFeatureConstants.FEATURE_NAME_AI_FEATURES, "' and ",
				AIHubFeatureConstants.OBJECT_FIELD_NAME_ACCOUNT_ENTRY_ID,
				" eq '", accountEntry.getAccountEntryId(), "'"),
			Pagination.of(1, 1), null,
			new Sort[] {new Sort("modifiedDate", Sort.LONG_TYPE, true)});

		for (ObjectEntry objectEntry : page.getItems()) {
			return objectEntry;
		}

		return null;
	}

	private String _getModifiedByName(ObjectEntry objectEntry) {
		Creator creator = objectEntry.getModifiedBy();

		if (creator == null) {
			return null;
		}

		return creator.getName();
	}

	private void _routeAuditMessage(
			AccountEntry accountEntry, long userId, boolean enable,
			String reason, String comment)
		throws Exception {

		JSONObject jsonObject = JSONUtil.put(
			"action", enable ? "enable" : "disable"
		).put(
			"comment", _truncate(GetterUtil.getString(comment))
		).put(
			"reason", _truncate(GetterUtil.getString(reason))
		);

		AuditRouterUtil.route(
			new AuditMessage(
				0, accountEntry.getCompanyId(), userId,
				PortalUtil.getUserName(userId, StringPool.BLANK), new Date(),
				accountEntry.getAccountEntryId(), jsonObject,
				AccountEntry.class.getName(),
				String.valueOf(accountEntry.getAccountEntryId()), null,
				_EVENT_TYPE, null));
	}

	private AIFeatures _toAIFeatures(
		boolean enable, String lastModifiedBy, Date lastModifiedDate) {

		AIFeatures aiFeatures = new AIFeatures();

		aiFeatures.setEnable(() -> enable);
		aiFeatures.setLastModifiedBy(() -> lastModifiedBy);
		aiFeatures.setLastModifiedDate(() -> lastModifiedDate);

		return aiFeatures;
	}

	private String _truncate(String string) {
		if (string.length() > 200) {
			return string.substring(0, 200);
		}

		return string;
	}

	private static final String _EVENT_TYPE = "AI_HUB_AI_FEATURES_UPDATE";

	@Reference
	private AgentDefinitionManager _agentDefinitionManager;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference(target = "(object.entry.manager.storage.type=default)")
	private ObjectEntryManager _objectEntryManager;

}