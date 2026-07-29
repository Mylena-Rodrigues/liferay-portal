/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.internal.workflow.node.delegate;

import com.liferay.ai.hub.workflow.node.ServiceNodeDelegate;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowNodeManager;
import com.liferay.portal.workflow.kaleo.runtime.ExecutionContext;

import java.io.Serializable;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alberto Sousa
 */
@Component(service = ServiceNodeDelegate.class)
public class CategorizeContentEntriesServiceNodeDelegate
	implements ServiceNodeDelegate {

	@Override
	public String execute(
			ExecutionContext executionContext,
			Map<String, String> inputVariables,
			Map<String, Serializable> workflowContext)
		throws Exception {

		String contentEntriesPayload = inputVariables.get(
			"contentEntriesPayload");

		JSONArray taxonomyCategoryIdsJSONArray = _jsonFactory.createJSONArray();

		long personaId = GetterUtil.getLong(inputVariables.get("personaId"));

		if (personaId > 0) {
			taxonomyCategoryIdsJSONArray.put(personaId);
		}

		long funnelStageId = GetterUtil.getLong(
			inputVariables.get("funnelStageId"));

		if (funnelStageId > 0) {
			taxonomyCategoryIdsJSONArray.put(funnelStageId);
		}

		if (Validator.isNotNull(contentEntriesPayload) &&
			(taxonomyCategoryIdsJSONArray.length() > 0)) {

			JSONArray contentEntriesPayloadJSONArray =
				_jsonFactory.createJSONArray(contentEntriesPayload);

			for (int i = 0; i < contentEntriesPayloadJSONArray.length(); i++) {
				JSONObject jsonObject =
					contentEntriesPayloadJSONArray.getJSONObject(i);

				jsonObject.put(
					"taxonomyCategoryIds", taxonomyCategoryIdsJSONArray);
			}

			contentEntriesPayload = contentEntriesPayloadJSONArray.toString();
		}

		workflowContext.put("contentEntriesPayload", contentEntriesPayload);
		workflowContext.put("output", contentEntriesPayload);

		WorkflowNodeUtil.completeWorkflowNode(
			executionContext, workflowContext, _workflowNodeManager);

		return contentEntriesPayload;
	}

	@Override
	public String getKey() {
		return "javaDelegate#categorizeContentEntries";
	}

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private WorkflowNodeManager _workflowNodeManager;

}