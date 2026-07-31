/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.internal.workflow.node.delegate;

import com.liferay.ai.hub.agent.AgentContext;
import com.liferay.ai.hub.agent.DefaultAgent;
import com.liferay.ai.hub.util.AccountEntryUtil;
import com.liferay.ai.hub.workflow.node.ServiceNodeDelegate;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManager;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.encryptor.EncryptorUtil;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowNodeManager;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.workflow.kaleo.model.KaleoInstanceToken;
import com.liferay.portal.workflow.kaleo.runtime.ExecutionContext;

import java.io.Serializable;

import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alberto Sousa
 */
@Component(service = ServiceNodeDelegate.class)
public class GenerateContentServiceNodeDelegate implements ServiceNodeDelegate {

	@Override
	public String execute(
			ExecutionContext executionContext,
			Map<String, String> inputVariables,
			Map<String, Serializable> workflowContext)
		throws Exception {

		KaleoInstanceToken kaleoInstanceToken =
			executionContext.getKaleoInstanceToken();
		ServiceContext serviceContext = executionContext.getServiceContext();

		_objectEntryManager.getObjectEntry(
			kaleoInstanceToken.getCompanyId(),
			new DefaultDTOConverterContext(
				false, Map.of(), _dtoConverterRegistry, null,
				serviceContext.getLocale(), null,
				_userLocalService.getUserById(kaleoInstanceToken.getUserId())),
			"L_GENERATE_CONTENT",
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_AI_HUB_AGENT_DEFINITION",
					kaleoInstanceToken.getCompanyId()),
			null);

		Company company = _companyLocalService.getCompany(
			kaleoInstanceToken.getCompanyId());

		String generatedContent = String.valueOf(
			_defaultAgent.invoke(
				AgentContext.builder(
				).agentDefinitionExternalReferenceCode(
					"L_GENERATE_CONTENT"
				).asynchronous(
					false
				).companyId(
					company.getCompanyId()
				).groupId(
					AccountEntryUtil.getUserAccountEntryGroupId(
						kaleoInstanceToken.getUserId())
				).input(
					HashMapBuilder.<String, Object>put(
						"brief", inputVariables.get("brief")
					).put(
						"count", inputVariables.get("count")
					).put(
						"funnelStageId", inputVariables.get("funnelStageId")
					).put(
						"objectDefinitionName", "CMSBasicWebContent"
					).put(
						"objectFields", inputVariables.get("objectFields")
					).put(
						"personaId", inputVariables.get("personaId")
					).put(
						"projectId", inputVariables.get("projectId")
					).put(
						"spaceId", inputVariables.get("spaceId")
					).build()
				).inputVariableNames(
					Arrays.asList("brief", "count")
				).oAuth2ApplicationId(
					MapUtil.getLong(workflowContext, "oAuth2ApplicationId")
				).serviceContext(
					serviceContext
				).userId(
					kaleoInstanceToken.getUserId()
				).userToken(
					EncryptorUtil.decrypt(
						company.getKeyObj(),
						GetterUtil.getString(workflowContext.get("userToken")))
				).workflowDefinitionName(
					"Generate Content"
				).build()));

		_completeWorkflowNode(
			executionContext, generatedContent, inputVariables,
			workflowContext);

		return generatedContent;
	}

	@Override
	public String getKey() {
		return "javaDelegate#generateContent";
	}

	private void _completeWorkflowNode(
			ExecutionContext executionContext, String generatedContent,
			Map<String, String> inputVariables,
			Map<String, Serializable> workflowContext)
		throws Exception {

		workflowContext.put(
			"classExternalReferenceCode",
			_getClassExternalReferenceCode(generatedContent));
		workflowContext.put(
			"className",
			_getStringValue(
				inputVariables.get("objectDefinitionResponse"), "className"));
		workflowContext.put("generatedContent", generatedContent);
		workflowContext.put(
			"groupExternalReferenceCode",
			GetterUtil.getString(
				inputVariables.get("spaceExternalReferenceCode")));
		workflowContext.put("output", generatedContent);
		workflowContext.put(
			"scopeKey",
			GetterUtil.getString(inputVariables.get("projectScopeKey")));

		WorkflowNodeUtil.completeWorkflowNode(
			executionContext, workflowContext, _workflowNodeManager);
	}

	private String _getClassExternalReferenceCode(String generatedContent) {
		if (Validator.isNull(generatedContent)) {
			return StringPool.BLANK;
		}

		Matcher matcher = _pattern.matcher(generatedContent);

		if (matcher.find()) {
			return URLCodec.decodeURL(matcher.group(1));
		}

		return StringPool.BLANK;
	}

	private String _getStringValue(String json, String key) throws Exception {
		if (Validator.isNull(json)) {
			return StringPool.BLANK;
		}

		JSONObject jsonObject = _jsonFactory.createJSONObject(json);

		return jsonObject.getString(key);
	}

	private static final Pattern _pattern = Pattern.compile(
		"externalReferenceCode=([^&]+)");

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private DefaultAgent _defaultAgent;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference(target = "(object.entry.manager.storage.type=default)")
	private ObjectEntryManager _objectEntryManager;

	@Reference
	private UserLocalService _userLocalService;

	@Reference
	private WorkflowNodeManager _workflowNodeManager;

}