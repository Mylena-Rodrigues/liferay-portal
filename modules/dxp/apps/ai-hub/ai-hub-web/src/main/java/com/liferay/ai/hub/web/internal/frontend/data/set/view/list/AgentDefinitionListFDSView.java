/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.web.internal.frontend.data.set.view.list;

import com.liferay.ai.hub.web.internal.constants.AIHubFDSNames;
import com.liferay.frontend.data.set.view.FDSView;
import com.liferay.frontend.data.set.view.list.BaseListFDSView;

import org.osgi.service.component.annotations.Component;

/**
 * @author João Victor Alves
 */
@Component(
	property = "frontend.data.set.name=" + AIHubFDSNames.AGENT_DEFINITIONS,
	service = FDSView.class
)
public class AgentDefinitionListFDSView extends BaseListFDSView {

	@Override
	public String getDescription() {
		return "description";
	}

	@Override
	public FDSListSchema getFDSListSchema(Locale locale) {
		FDSListSchemaBuilderFactory fdsListSchemaBuilderFactory =
			_fdsListSchemaBuilderFactorySnapshot.get();

		if (fdsListSchemaBuilderFactory == null) {
			return null;
		}

		FDSListSchemaBuilder fdsListSchemaBuilder =
			fdsListSchemaBuilderFactory.create();

		return fdsListSchemaBuilder.add(
			"labels.agentDefinitionStatus.value",
			HashMapBuilder.put(
				"active", "success"
			).put(
				"inactive", "danger"
			).build(),
			"labels.agentDefinitionStatus.value_i18n"
		).add(
			"labels.agentDefinitionType.value",
			HashMapBuilder.put(
				"custom", "warning"
			).put(
				"system", "info"
			).build(),
			"labels.agentDefinitionType.value_i18n"
		).build();
	}

	@Override
	public String getTitle() {
		return "title";
	}

}