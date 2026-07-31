/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.internal.workflow.node.delegate;

import com.liferay.ai.hub.workflow.node.ServiceNodeDelegate;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowNodeManager;
import com.liferay.portal.workflow.kaleo.runtime.ExecutionContext;

import java.io.Serializable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carolina Barbosa
 */
@Component(service = ServiceNodeDelegate.class)
public class ComposeProjectLinksServiceNodeDelegate
	implements ServiceNodeDelegate {

	@Override
	public String execute(
			ExecutionContext executionContext,
			Map<String, String> inputVariables,
			Map<String, Serializable> workflowContext)
		throws Exception {

		JSONArray projectLinksJSONArray = _jsonFactory.createJSONArray();

		JSONObject outputJSONObject = _jsonFactory.createJSONObject(
			GetterUtil.getString(inputVariables.get("output")));

		JSONArray resultsJSONArray = outputJSONObject.getJSONArray("results");

		if (resultsJSONArray != null) {
			Map<Long, JSONObject> assetJSONObjects = _getAssetJSONObjects(
				GetterUtil.getString(inputVariables.get("findResults")));

			for (int i = 0; i < resultsJSONArray.length(); i++) {
				JSONObject resultJSONObject = resultsJSONArray.getJSONObject(i);

				JSONObject assetJSONObject = assetJSONObjects.get(
					GetterUtil.getLong(resultJSONObject.get("id")));

				if (assetJSONObject == null) {
					continue;
				}

				projectLinksJSONArray.put(
					JSONUtil.put(
						"classExternalReferenceCode",
						assetJSONObject.getString("classExternalReferenceCode")
					).put(
						"className", assetJSONObject.getString("className")
					).put(
						"groupExternalReferenceCode",
						assetJSONObject.getString("groupExternalReferenceCode")
					).put(
						"r_cmpProjectToCMPProjectLinks_c_cmpProjectId",
						GetterUtil.getLong(inputVariables.get("projectId"))
					));
			}
		}

		workflowContext.put(
			"projectLinksPayload", projectLinksJSONArray.toString());

		WorkflowNodeUtil.completeWorkflowNode(
			executionContext, workflowContext, _workflowNodeManager);

		return projectLinksJSONArray.toString();
	}

	@Override
	public String getKey() {
		return "javaDelegate#composeProjectLinks";
	}

	private Map<Long, JSONObject> _getAssetJSONObjects(String json)
		throws Exception {

		if (Validator.isNull(json)) {
			return Collections.emptyMap();
		}

		JSONObject jsonObject = _jsonFactory.createJSONObject(json);

		JSONArray itemsJSONArray = jsonObject.getJSONArray("items");

		if (itemsJSONArray == null) {
			return Collections.emptyMap();
		}

		Map<Long, JSONObject> assetJSONObjects = new HashMap<>();

		for (int i = 0; i < itemsJSONArray.length(); i++) {
			JSONObject itemJSONObject = itemsJSONArray.getJSONObject(i);

			JSONObject embeddedJSONObject = itemJSONObject.getJSONObject(
				"embedded");

			if (embeddedJSONObject == null) {
				continue;
			}

			assetJSONObjects.put(
				embeddedJSONObject.getLong("id"),
				JSONUtil.put(
					"classExternalReferenceCode",
					embeddedJSONObject.getString("externalReferenceCode")
				).put(
					"className", itemJSONObject.getString("entryClassName")
				).put(
					"groupExternalReferenceCode",
					() -> {
						JSONObject systemPropertiesJSONObject =
							embeddedJSONObject.getJSONObject(
								"systemProperties");

						if (systemPropertiesJSONObject == null) {
							return null;
						}

						JSONObject scopeJSONObject =
							systemPropertiesJSONObject.getJSONObject("scope");

						if (scopeJSONObject == null) {
							return null;
						}

						return scopeJSONObject.getString(
							"externalReferenceCode");
					}
				));
		}

		return assetJSONObjects;
	}

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private WorkflowNodeManager _workflowNodeManager;

}