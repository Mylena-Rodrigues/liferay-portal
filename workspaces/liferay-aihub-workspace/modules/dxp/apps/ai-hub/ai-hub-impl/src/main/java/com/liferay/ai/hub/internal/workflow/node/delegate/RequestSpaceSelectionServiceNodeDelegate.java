/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.internal.workflow.node.delegate;

import com.liferay.ai.hub.rest.resource.v1_0.util.SseUtil;
import com.liferay.ai.hub.workflow.node.ServiceNodeDelegate;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowInstanceManager;
import com.liferay.portal.workflow.kaleo.model.KaleoInstanceToken;
import com.liferay.portal.workflow.kaleo.runtime.ExecutionContext;

import java.io.Serializable;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alberto Sousa
 */
@Component(service = ServiceNodeDelegate.class)
public class RequestSpaceSelectionServiceNodeDelegate
	implements ServiceNodeDelegate {

	@Override
	public String execute(
			ExecutionContext executionContext,
			Map<String, String> inputVariables,
			Map<String, Serializable> workflowContext)
		throws Exception {

		KaleoInstanceToken kaleoInstanceToken =
			executionContext.getKaleoInstanceToken();

		_updateWorkflowContext(
			inputVariables, kaleoInstanceToken, workflowContext);

		JSONArray optionsJSONArray = _jsonFactory.createJSONArray();

		JSONArray spacesJSONArray = _jsonFactory.createJSONArray(
			GetterUtil.getString(inputVariables.get("spacesJSONArray")));

		for (int i = 0; i < spacesJSONArray.length(); i++) {
			JSONObject spaceJSONObject = spacesJSONArray.getJSONObject(i);

			optionsJSONArray.put(
				JSONUtil.put(
					"action",
					JSONUtil.put(
						"http-request",
						JSONUtil.put(
							"body",
							JSONUtil.put(
								"context",
								JSONUtil.put(
									"spaceExternalReferenceCode",
									spaceJSONObject.getString(
										"externalReferenceCode")
								).put(
									"spaceId", spaceJSONObject.getLong("id")
								))
						).put(
							"href",
							StringBundler.concat(
								GetterUtil.getString(
									workflowContext.get(
										"aiHubCellLiferayDXPURL")),
								"/o/ai-hub/v1.0/agent-instances/",
								String.valueOf(
									kaleoInstanceToken.getKaleoInstanceId()),
								"/resume")
						).put(
							"method", "PUT"
						))
				).put(
					"label", spaceJSONObject.getString("label")
				));
		}

		SseUtil.send(
			_getAgentDefinitionExternalReferenceCodes(workflowContext), null,
			"Chat Message Sent", kaleoInstanceToken.getCurrentKaleoNodeName(),
			JSONUtil.put(
				"component",
				JSONUtil.put(
					"options", optionsJSONArray
				).put(
					"title", "Which space?"
				).put(
					"type", "select"
				)),
			GetterUtil.getString(workflowContext.get("sseEventSinkKey")),
			"component");

		return StringPool.BLANK;
	}

	@Override
	public String getKey() {
		return "javaDelegate#requestSpaceSelection";
	}

	private String[] _getAgentDefinitionExternalReferenceCodes(
		Map<String, Serializable> workflowContext) {

		String agentDefinitionExternalReferenceCode = GetterUtil.getString(
			workflowContext.get("agentDefinitionExternalReferenceCode"));

		if (Validator.isNull(agentDefinitionExternalReferenceCode)) {
			return null;
		}

		return new String[] {agentDefinitionExternalReferenceCode};
	}

	private void _updateWorkflowContext(
			Map<String, String> inputVariables,
			KaleoInstanceToken kaleoInstanceToken,
			Map<String, Serializable> workflowContext)
		throws Exception {

		JSONArray gapsJSONArray = _jsonFactory.createJSONArray(
			GetterUtil.getString(inputVariables.get("gaps")));

		if (gapsJSONArray.length() > 0) {
			JSONObject gapJSONObject = gapsJSONArray.getJSONObject(0);

			workflowContext.put(
				"funnelStage", gapJSONObject.getString("funnelStage"));
			workflowContext.put(
				"funnelStageId", gapJSONObject.getString("funnelStageId"));
			workflowContext.put("gap", gapJSONObject.toString());
			workflowContext.put("persona", gapJSONObject.getString("persona"));
			workflowContext.put(
				"personaId", gapJSONObject.getString("personaId"));
		}

		_workflowInstanceManager.updateWorkflowContext(
			kaleoInstanceToken.getCompanyId(),
			kaleoInstanceToken.getKaleoInstanceId(), workflowContext);
	}

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private WorkflowInstanceManager _workflowInstanceManager;

}