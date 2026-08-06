/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.internal.workflow.node.delegate;

import com.liferay.ai.hub.rest.resource.v1_0.util.SseUtil;
import com.liferay.ai.hub.workflow.node.ServiceNodeDelegate;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.workflow.kaleo.model.KaleoInstanceToken;
import com.liferay.portal.workflow.kaleo.runtime.ExecutionContext;

import java.io.Serializable;

import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carolina Barbosa
 */
@Component(service = ServiceNodeDelegate.class)
public class RequestImageStyleServiceNodeDelegate
	implements ServiceNodeDelegate {

	@Override
	public String execute(
			ExecutionContext executionContext,
			Map<String, String> inputVariables,
			Map<String, Serializable> workflowContext)
		throws Exception {

		KaleoInstanceToken kaleoInstanceToken =
			executionContext.getKaleoInstanceToken();

		String resumeURL = MapUtil.getString(workflowContext, "resumeURL");

		ServiceContext serviceContext = executionContext.getServiceContext();

		Locale locale = serviceContext.getLocale();

		SseUtil.send(
			_getAgentDefinitionExternalReferenceCodes(workflowContext), null,
			"Chat Message Sent", kaleoInstanceToken.getCurrentKaleoNodeName(),
			JSONUtil.put(
				"component",
				JSONUtil.put(
					"options",
					JSONUtil.putAll(
						_getOptionJSONObject(
							_language.get(locale, "digital-art"), resumeURL,
							"Digital Art"),
						_getOptionJSONObject(
							_language.get(locale, "illustration"), resumeURL,
							"Illustration"),
						_getOptionJSONObject(
							_language.get(locale, "photorealistic"), resumeURL,
							"Photorealistic"),
						_getOptionJSONObject(
							_language.get(locale, "watercolor"), resumeURL,
							"Watercolor"))
				).put(
					"title", _language.get(locale, "which-style")
				).put(
					"type", "select"
				)),
			GetterUtil.getString(workflowContext.get("sseEventSinkKey")),
			"component");

		return StringPool.BLANK;
	}

	@Override
	public String getKey() {
		return "javaDelegate#requestImageStyle";
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
		String label, String resumeURL, String style) {

		return JSONUtil.put(
			"action",
			JSONUtil.put(
				"http-request",
				JSONUtil.put(
					"body",
					JSONUtil.put("context", JSONUtil.put("style", style))
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
	private Language _language;

}