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
public class ApplyTaxonomyCategoriesServiceNodeDelegate
	implements ServiceNodeDelegate {

	@Override
	public String execute(
			ExecutionContext executionContext,
			Map<String, String> inputVariables,
			Map<String, Serializable> workflowContext)
		throws Exception {

		JSONArray contentEntriesPayloadJSONArray =
			_getContentEntriesPayloadJSONArray(inputVariables);

		workflowContext.put(
			"contentEntriesPayload", contentEntriesPayloadJSONArray.toString());

		WorkflowNodeUtil.completeWorkflowNode(
			executionContext, _kaleoSignaler, workflowContext,
			_workflowInstanceManager);

		return contentEntriesPayloadJSONArray.toString();
	}

	@Override
	public String getKey() {
		return "javaDelegate#applyTaxonomyCategories";
	}

	private JSONArray _getContentEntriesPayloadJSONArray(
			Map<String, String> inputVariables)
		throws Exception {

		JSONArray contentEntriesPayloadJSONArray = _jsonFactory.createJSONArray(
			GetterUtil.getString(inputVariables.get("contentEntriesPayload")));
		JSONArray taxonomyCategoryIdsJSONArray = _jsonFactory.createJSONArray(
			GetterUtil.getString(inputVariables.get("taxonomyCategoryIds")));

		if (taxonomyCategoryIdsJSONArray.length() == 0) {
			return contentEntriesPayloadJSONArray;
		}

		for (int i = 0; i < contentEntriesPayloadJSONArray.length(); i++) {
			JSONObject contentEntryPayloadJSONObject =
				contentEntriesPayloadJSONArray.getJSONObject(i);

			contentEntryPayloadJSONObject.put(
				"taxonomyCategoryIds", taxonomyCategoryIdsJSONArray);
		}

		return contentEntriesPayloadJSONArray;
	}

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private KaleoSignaler _kaleoSignaler;

	@Reference
	private WorkflowInstanceManager _workflowInstanceManager;

}