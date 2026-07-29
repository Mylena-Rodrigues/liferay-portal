/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.internal.workflow.node.delegate;

import com.liferay.ai.hub.workflow.node.ServiceNodeDelegate;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowNodeManager;
import com.liferay.portal.workflow.kaleo.runtime.ExecutionContext;

import java.io.Serializable;

import java.net.URLEncoder;

import java.nio.charset.StandardCharsets;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alberto Sousa
 */
@Component(service = ServiceNodeDelegate.class)
public class ComposeGapSearchServiceNodeDelegate
	implements ServiceNodeDelegate {

	@Override
	public String execute(
			ExecutionContext executionContext,
			Map<String, String> inputVariables,
			Map<String, Serializable> workflowContext)
		throws Exception {

		String gapSearchFilter = _getGapSearchFilter(
			inputVariables.get("gaps"));

		workflowContext.put("gapSearchFilter", gapSearchFilter);

		WorkflowNodeUtil.completeWorkflowNode(
			executionContext, workflowContext, _workflowNodeManager);

		return gapSearchFilter;
	}

	@Override
	public String getKey() {
		return "javaDelegate#composeGapSearch";
	}

	private String _getGapSearchFilter(String gaps) throws Exception {
		StringBundler sb = new StringBundler();

		sb.append("cmsSection in ('contents', 'files') and ");
		sb.append("rootDescendantNode eq false");

		List<String> gapClauses = new ArrayList<>();

		if (Validator.isNotNull(gaps)) {
			JSONArray jsonArray = _jsonFactory.createJSONArray(gaps);

			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);

				long funnelStageId = GetterUtil.getLong(
					jsonObject.get("funnelStageId"));
				long personaId = GetterUtil.getLong(
					jsonObject.get("personaId"));

				if ((funnelStageId <= 0) || (personaId <= 0)) {
					continue;
				}

				gapClauses.add(
					StringBundler.concat(
						"(internalTaxonomyCategoryIds/any(c:c eq ",
						String.valueOf(personaId),
						") and internalTaxonomyCategoryIds/any(c:c eq ",
						String.valueOf(funnelStageId), "))"));
			}
		}

		if (gapClauses.isEmpty()) {
			sb.append(" and internalTaxonomyCategoryIds/any(c:c eq -1)");
		}
		else {
			sb.append(" and (");
			sb.append(StringUtil.merge(gapClauses, " or "));
			sb.append(")");
		}

		return URLEncoder.encode(
			sb.toString(), StandardCharsets.UTF_8
		).replace(
			"+", "%20"
		);
	}

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private WorkflowNodeManager _workflowNodeManager;

}