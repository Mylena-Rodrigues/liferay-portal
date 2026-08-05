/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.internal.workflow.node.delegate;

import com.liferay.ai.hub.rest.resource.v1_0.util.SseUtil;
import com.liferay.ai.hub.workflow.node.ServiceNodeDelegate;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowInstanceManager;
import com.liferay.portal.workflow.kaleo.model.KaleoInstanceToken;
import com.liferay.portal.workflow.kaleo.runtime.ExecutionContext;
import com.liferay.portal.workflow.kaleo.runtime.KaleoSignaler;

import java.io.Serializable;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carolina Barbosa
 */
@Component(service = ServiceNodeDelegate.class)
public class ComposeProjectLinkOutputServiceNodeDelegate
	implements ServiceNodeDelegate {

	@Override
	public String execute(
			ExecutionContext executionContext,
			Map<String, String> inputVariables,
			Map<String, Serializable> workflowContext)
		throws Exception {

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			GetterUtil.getString(workflowContext.get("output")));

		JSONArray jsonArray = jsonObject.getJSONArray("results");

		if ((jsonArray == null) || (jsonArray.length() == 0)) {
			return _completeWorkflowNode(
				executionContext, "There are no assets to link to the project.",
				workflowContext);
		}

		if (Validator.isNull(
				GetterUtil.getString(workflowContext.get("linkResponse")))) {

			return _completeWorkflowNode(
				executionContext,
				"The suggested assets were not linked to the project.",
				workflowContext);
		}

		KaleoInstanceToken kaleoInstanceToken =
			executionContext.getKaleoInstanceToken();

		SseUtil.send(
			null, "Content Updated",
			kaleoInstanceToken.getCurrentKaleoNodeName(),
			GetterUtil.getString(workflowContext.get("sseEventSinkKey")));

		return _completeWorkflowNode(
			executionContext,
			"The suggested assets are now linked to the project.",
			workflowContext);
	}

	@Override
	public String getKey() {
		return "javaDelegate#composeProjectLinkOutput";
	}

	private String _completeWorkflowNode(
			ExecutionContext executionContext, String output,
			Map<String, Serializable> workflowContext)
		throws Exception {

		workflowContext.put("output", output);

		WorkflowNodeUtil.completeWorkflowNode(
			executionContext, _kaleoSignaler, workflowContext,
			_workflowInstanceManager);

		return output;
	}

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private KaleoSignaler _kaleoSignaler;

	@Reference
	private WorkflowInstanceManager _workflowInstanceManager;

}