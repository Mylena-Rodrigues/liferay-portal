/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.internal.workflow.node.delegate;

import com.liferay.ai.hub.rest.resource.v1_0.util.SseUtil;
import com.liferay.ai.hub.workflow.node.ServiceNodeDelegate;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowNodeManager;
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
public class RequestContentGapCategoriesServiceNodeDelegate
	implements ServiceNodeDelegate {

	@Override
	public String execute(
			ExecutionContext executionContext,
			Map<String, String> inputVariables,
			Map<String, Serializable> workflowContext)
		throws Exception {

		String funnelStageId = inputVariables.get("funnelStageId");
		String personaId = inputVariables.get("personaId");
		String projectId = inputVariables.get("projectId");

		if (Validator.isNull(projectId) ||
			(Validator.isNotNull(funnelStageId) &&
			 Validator.isNotNull(personaId))) {

			workflowContext.put("output", StringPool.BLANK);

			WorkflowNodeUtil.completeWorkflowNode(
				executionContext, workflowContext, _workflowNodeManager);

			return StringPool.BLANK;
		}

		KaleoInstanceToken kaleoInstanceToken =
			executionContext.getKaleoInstanceToken();

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			GetterUtil.getString(inputVariables.get("contentCoverage")));

		SseUtil.send(
			JSONUtil.put(
				"action", "requestContentGapCategories"
			).put(
				"agentInstanceId", kaleoInstanceToken.getKaleoInstanceId()
			).put(
				"funnelStages",
				() -> {
					if (Validator.isNotNull(funnelStageId)) {
						return null;
					}

					return jsonObject.getJSONArray("funnelStages");
				}
			).put(
				"personas",
				() -> {
					if (Validator.isNotNull(personaId)) {
						return null;
					}

					return jsonObject.getJSONArray("personas");
				}
			).put(
				"projectId", projectId
			).toString(),
			"Chat Message Sent", kaleoInstanceToken.getCurrentKaleoNodeName(),
			GetterUtil.getString(workflowContext.get("sseEventSinkKey")));

		return StringPool.BLANK;
	}

	@Override
	public String getKey() {
		return "javaDelegate#requestContentGapCategories";
	}

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private WorkflowNodeManager _workflowNodeManager;

}