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
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowNodeManager;
import com.liferay.portal.workflow.kaleo.model.KaleoInstanceToken;
import com.liferay.portal.workflow.kaleo.runtime.ExecutionContext;

import java.io.Serializable;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Feliphe Marinho
 */
@Component(service = ServiceNodeDelegate.class)
public class SuggestProjectLinkServiceNodeDelegate
	implements ServiceNodeDelegate {

	@Override
	public String execute(
			ExecutionContext executionContext,
			Map<String, String> inputVariables,
			Map<String, Serializable> workflowContext)
		throws Exception {

		KaleoInstanceToken kaleoInstanceToken =
			executionContext.getKaleoInstanceToken();

		String output = GetterUtil.getString(workflowContext.get("output"));

		JSONArray jsonArray = null;

		if (Validator.isNotNull(output)) {
			JSONObject jsonObject = _jsonFactory.createJSONObject(output);

			jsonArray = jsonObject.getJSONArray("results");
		}

		if ((jsonArray == null) || (jsonArray.length() == 0)) {
			SseUtil.send(
				_getAgentDefinitionExternalReferenceCodes(workflowContext),
				"There are no assets to link to the project.",
				"Chat Message Sent",
				kaleoInstanceToken.getCurrentKaleoNodeName(),
				GetterUtil.getString(workflowContext.get("sseEventSinkKey")));

			WorkflowNodeUtil.completeWorkflowNode(
				executionContext, "no", workflowContext, _workflowNodeManager);

			return StringPool.BLANK;
		}

		String resumeURL = StringBundler.concat(
			MapUtil.getString(workflowContext, "aiHubCellLiferayDXPURL"),
			"/o/ai-hub/v1.0/agent-instances/",
			String.valueOf(kaleoInstanceToken.getKaleoInstanceId()), "/resume");

		SseUtil.send(
			_getAgentDefinitionExternalReferenceCodes(workflowContext), null,
			"Chat Message Sent", kaleoInstanceToken.getCurrentKaleoNodeName(),
			JSONUtil.put(
				"component",
				JSONUtil.put(
					"options",
					JSONUtil.putAll(
						_getOptionJSONObject("Yes", resumeURL, "yes"),
						_getOptionJSONObject("No", resumeURL, "no"))
				).put(
					"title", "Would you like me to add all suggested assets?"
				).put(
					"type", "select"
				)),
			GetterUtil.getString(workflowContext.get("sseEventSinkKey")),
			"component");

		return StringPool.BLANK;
	}

	@Override
	public String getKey() {
		return "javaDelegate#suggestProjectLink";
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

	private JSONObject _getOptionJSONObject(
		String label, String resumeURL, String transitionName) {

		return JSONUtil.put(
			"action",
			JSONUtil.put(
				"http-request",
				JSONUtil.put(
					"body",
					JSONUtil.put(
						"context",
						JSONUtil.put("transitionName", transitionName))
				).put(
					"href", resumeURL
				).put(
					"method", "PUT"
				))
		).put(
			"label", label
		);
	}

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private WorkflowNodeManager _workflowNodeManager;

}