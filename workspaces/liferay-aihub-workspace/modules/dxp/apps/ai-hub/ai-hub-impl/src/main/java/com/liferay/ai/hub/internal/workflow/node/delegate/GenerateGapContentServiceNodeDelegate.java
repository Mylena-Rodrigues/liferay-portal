/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.internal.workflow.node.delegate;

import com.liferay.ai.hub.agent.AgentContext;
import com.liferay.ai.hub.agent.DefaultAgent;
import com.liferay.ai.hub.util.AccountEntryUtil;
import com.liferay.ai.hub.workflow.node.ServiceNodeDelegate;
import com.liferay.portal.kernel.encryptor.EncryptorUtil;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.workflow.WorkflowNodeManager;
import com.liferay.portal.workflow.kaleo.model.KaleoInstanceToken;
import com.liferay.portal.workflow.kaleo.runtime.ExecutionContext;

import java.io.Serializable;

import java.util.Arrays;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Feliphe Marinho
 */
@Component(service = ServiceNodeDelegate.class)
public class GenerateGapContentServiceNodeDelegate
	implements ServiceNodeDelegate {

	@Override
	public String execute(
			ExecutionContext executionContext,
			Map<String, String> inputVariables,
			Map<String, Serializable> workflowContext)
		throws Exception {

		KaleoInstanceToken kaleoInstanceToken =
			executionContext.getKaleoInstanceToken();

		Company company = _companyLocalService.getCompany(
			kaleoInstanceToken.getCompanyId());

		ServiceContext serviceContext = executionContext.getServiceContext();

		String output = String.valueOf(
			_defaultAgent.invoke(
				AgentContext.builder(
				).agentDefinitionExternalReferenceCode(
					"L_GENERATE_GAP_CONTENT"
				).asynchronous(
					false
				).companyId(
					company.getCompanyId()
				).groupId(
					AccountEntryUtil.getUserAccountEntryGroupId(
						kaleoInstanceToken.getUserId())
				).input(
					HashMapBuilder.<String, Object>put(
						"count", 1
					).put(
						"gaps",
						() -> {
							JSONObject jsonObject =
								_jsonFactory.createJSONObject(
									MapUtil.getString(
										workflowContext, "analysisResult"));

							return jsonObject.getJSONArray(
								"gaps"
							).toString();
						}
					).put(
						"objectFields", inputVariables.get("objectFields")
					).put(
						"projectId", inputVariables.get("projectId")
					).put(
						"projectScopeKey", inputVariables.get("projectScopeKey")
					).put(
						"spacesJSONArray", inputVariables.get("spacesJSONArray")
					).build()
				).inputVariableNames(
					Arrays.asList(
						"count", "gaps", "objectFields", "projectId",
						"projectScopeKey", "spacesJSONArray")
				).oAuth2ApplicationId(
					MapUtil.getLong(workflowContext, "oAuth2ApplicationId")
				).serviceContext(
					serviceContext
				).sseEventSinkKey(
					MapUtil.getString(workflowContext, "sseEventSinkKey")
				).userId(
					kaleoInstanceToken.getUserId()
				).userToken(
					EncryptorUtil.decrypt(
						company.getKeyObj(),
						GetterUtil.getString(workflowContext.get("userToken")))
				).workflowDefinitionName(
					"Generate Gap Content"
				).build()));

		workflowContext.put("output", output);

		WorkflowNodeUtil.completeWorkflowNode(
			executionContext, workflowContext, _workflowNodeManager);

		return output;
	}

	@Override
	public String getKey() {
		return "javaDelegate#generateGapContent";
	}

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private DefaultAgent _defaultAgent;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private WorkflowNodeManager _workflowNodeManager;

}