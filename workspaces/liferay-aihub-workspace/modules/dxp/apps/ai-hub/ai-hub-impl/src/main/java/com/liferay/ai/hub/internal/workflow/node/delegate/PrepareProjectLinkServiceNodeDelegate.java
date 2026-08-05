/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.internal.workflow.node.delegate;

import com.liferay.ai.hub.workflow.node.ServiceNodeDelegate;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowInstanceManager;
import com.liferay.portal.workflow.kaleo.runtime.ExecutionContext;
import com.liferay.portal.workflow.kaleo.runtime.KaleoSignaler;

import java.io.Serializable;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alberto Sousa
 */
@Component(service = ServiceNodeDelegate.class)
public class PrepareProjectLinkServiceNodeDelegate
	implements ServiceNodeDelegate {

	@Override
	public String execute(
			ExecutionContext executionContext,
			Map<String, String> inputVariables,
			Map<String, Serializable> workflowContext)
		throws Exception {

		if (Validator.isNull(inputVariables.get("projectId"))) {
			WorkflowNodeUtil.completeWorkflowNode(
				executionContext, _kaleoSignaler, "end", workflowContext,
				_workflowInstanceManager);

			return StringPool.BLANK;
		}

		String classExternalReferenceCode = _getClassExternalReferenceCode(
			inputVariables.get("contentEntriesPayload"));

		workflowContext.put(
			"classExternalReferenceCode", classExternalReferenceCode);

		workflowContext.put(
			"className",
			_getClassName(inputVariables.get("objectDefinitionResponse")));
		workflowContext.put(
			"groupExternalReferenceCode",
			GetterUtil.getString(
				inputVariables.get("spaceExternalReferenceCode")));
		workflowContext.put(
			"scopeKey",
			GetterUtil.getString(inputVariables.get("projectScopeKey")));

		WorkflowNodeUtil.completeWorkflowNode(
			executionContext, _kaleoSignaler, "linkAssetToProject",
			workflowContext, _workflowInstanceManager);

		return classExternalReferenceCode;
	}

	@Override
	public String getKey() {
		return "javaDelegate#prepareProjectLink";
	}

	private String _getClassExternalReferenceCode(String contentEntriesPayload)
		throws Exception {

		JSONArray contentEntriesJSONArray = _jsonFactory.createJSONArray(
			contentEntriesPayload);

		if (contentEntriesJSONArray.length() == 0) {
			return StringPool.BLANK;
		}

		JSONObject contentEntryJSONObject =
			contentEntriesJSONArray.getJSONObject(0);

		return contentEntryJSONObject.getString("externalReferenceCode");
	}

	private String _getClassName(String objectDefinitionResponse)
		throws Exception {

		JSONObject objectDefinitionJSONObject = _jsonFactory.createJSONObject(
			objectDefinitionResponse);

		return objectDefinitionJSONObject.getString("className");
	}

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private KaleoSignaler _kaleoSignaler;

	@Reference
	private WorkflowInstanceManager _workflowInstanceManager;

}