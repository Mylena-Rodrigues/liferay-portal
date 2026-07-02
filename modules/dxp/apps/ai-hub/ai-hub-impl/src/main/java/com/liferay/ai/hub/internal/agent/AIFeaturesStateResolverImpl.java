/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.internal.agent;

import com.liferay.ai.hub.agent.AIFeaturesStateResolver;
import com.liferay.ai.hub.constants.AIHubFeatureConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.util.MapUtil;

import java.io.Serializable;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mylena Monte
 */
@Component(service = AIFeaturesStateResolver.class)
public class AIFeaturesStateResolverImpl implements AIFeaturesStateResolver {

	@Override
	public boolean isEnabled(long companyId, long accountEntryId)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinition(
				companyId, AIHubFeatureConstants.OBJECT_DEFINITION_NAME);

		if (objectDefinition == null) {
			return true;
		}

		ObjectEntry latestObjectEntry = null;

		for (ObjectEntry objectEntry :
				_objectEntryLocalService.getObjectEntries(
					0, objectDefinition.getObjectDefinitionId(),
					QueryUtil.ALL_POS, QueryUtil.ALL_POS)) {

			Map<String, Serializable> values = objectEntry.getValues();

			if (!AIHubFeatureConstants.FEATURE_NAME_AI_FEATURES.equals(
					MapUtil.getString(values, "featureName")) ||
				(accountEntryId != MapUtil.getLong(
					values,
					AIHubFeatureConstants.
						OBJECT_FIELD_NAME_ACCOUNT_ENTRY_ID))) {

				continue;
			}

			if ((latestObjectEntry == null) ||
				objectEntry.getCreateDate(
				).after(
					latestObjectEntry.getCreateDate()
				)) {

				latestObjectEntry = objectEntry;
			}
		}

		if (latestObjectEntry == null) {
			return true;
		}

		return MapUtil.getBoolean(latestObjectEntry.getValues(), "enable");
	}

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

}