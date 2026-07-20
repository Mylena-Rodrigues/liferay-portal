/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.internal.workflow.node.delegate;

import com.liferay.ai.hub.workflow.node.ServiceNodeDelegate;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.workflow.kaleo.model.KaleoNode;
import com.liferay.portal.workflow.kaleo.runtime.ExecutionContext;
import com.liferay.translation.translator.JSONTranslatorPacket;
import com.liferay.translation.translator.Translator;
import com.liferay.translation.translator.TranslatorPacket;
import com.liferay.translation.translator.TranslatorRegistry;

import java.io.Serializable;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carolina Barbosa
 */
@Component(service = ServiceNodeDelegate.class)
public class TranslateServiceNodeDelegate extends BaseServiceNodeDelegate {

	@Override
	public String execute(
			ExecutionContext executionContext,
			Map<String, String> inputVariables, KaleoNode kaleoNode,
			Map<String, Serializable> workflowContext)
		throws Exception {

		Translator translator = _translatorRegistry.getCompanyTranslator(
			kaleoNode.getCompanyId());

		if ((translator == null) || translator.isAIAssisted()) {
			completeWorkflowNode(
				executionContext.getKaleoInstanceToken(), kaleoNode,
				workflowContext);

			return StringPool.BLANK;
		}

		JSONArray resultsJSONArray = _jsonFactory.createJSONArray();

		JSONArray targetLanguageIdsJSONArray = _jsonFactory.createJSONArray(
			GetterUtil.getString(inputVariables.get("targetLanguageIds")));

		for (int i = 0; i < targetLanguageIdsJSONArray.length(); i++) {
			TranslatorPacket translatorPacket = translator.translate(
				new JSONTranslatorPacket(
					kaleoNode.getCompanyId(),
					JSONUtil.put(
						"fields",
						_jsonFactory.createJSONObject(
							inputVariables.get("fields"))
					).put(
						"html",
						_jsonFactory.createJSONObject(
							inputVariables.get("html"))
					).put(
						"sourceLanguageId",
						inputVariables.get("sourceLanguageId")
					).put(
						"targetLanguageId",
						targetLanguageIdsJSONArray.getString(i)
					)));

			resultsJSONArray.put(
				JSONUtil.put(
					"fields",
					_jsonFactory.createJSONObject(
						translatorPacket.getFieldsMap())
				).put(
					"targetLanguageId", targetLanguageIdsJSONArray.getString(i)
				));
		}

		String translatedContent = JSONUtil.put(
			"action", "translate"
		).put(
			"results", resultsJSONArray
		).toString();

		workflowContext.put("translatedContent", translatedContent);

		completeWorkflowNode(
			executionContext.getKaleoInstanceToken(), kaleoNode,
			workflowContext);

		return translatedContent;
	}

	@Override
	public String getKey() {
		return "javaDelegate#TranslateContent#translate";
	}

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private TranslatorRegistry _translatorRegistry;

}