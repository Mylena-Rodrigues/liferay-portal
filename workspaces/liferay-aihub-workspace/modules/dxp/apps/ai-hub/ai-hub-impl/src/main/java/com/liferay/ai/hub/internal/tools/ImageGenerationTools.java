/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.internal.tools;

import com.liferay.ai.hub.internal.model.GoogleGenAiUtil;
import com.liferay.ai.hub.quota.QuotaManager;
import com.liferay.ai.hub.rest.resource.v1_0.util.SseUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.workflow.kaleo.model.KaleoInstanceToken;
import com.liferay.portal.workflow.kaleo.model.KaleoNode;
import com.liferay.portal.workflow.kaleo.runtime.ExecutionContext;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.data.image.Image;
import dev.langchain4j.invocation.InvocationParameters;
import dev.langchain4j.model.image.ImageModel;
import dev.langchain4j.model.output.Response;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

/**
 * @author Feliphe Marinho
 * @author Mario Gomes
 */
public class ImageGenerationTools {

	public ImageGenerationTools(QuotaManager quotaManager) {
		_quotaManager = quotaManager;
	}

	@Tool("Generate images based on a prompt.")
	public String generateImages(
		InvocationParameters invocationParameters,
		@P("Description of the images to be generated.") String prompt) {

		try {
			ExecutionContext executionContext = invocationParameters.get(
				"executionContext");

			ImageModel imageModel = GoogleGenAiUtil.createGoogleGenAiImageModel(
				_quotaManager, executionContext.getServiceContext());

			Response<List<Image>> response = imageModel.generate(prompt, -1);

			Map<String, Serializable> workflowContext =
				executionContext.getWorkflowContext();

			KaleoInstanceToken kaleoInstanceToken =
				executionContext.getKaleoInstanceToken();

			KaleoNode kaleoNode = kaleoInstanceToken.getCurrentKaleoNode();

			for (Image image : response.content()) {
				SseUtil.send(
					new String[] {
						GetterUtil.getString(
							workflowContext.get(
								"agentDefinitionExternalReferenceCode"))
					},
					image.base64Data(),
					GetterUtil.getString(
						workflowContext.get("outBoundEventName"),
						"Chat Message Sent"),
					kaleoNode.getName(),
					JSONUtil.put("mimeType", image.mimeType()),
					GetterUtil.getString(
						workflowContext.get("sseEventSinkKey")),
					"image");
			}

			return "Images have been generated.";
		}
		catch (Exception exception) {
			_log.error(exception);

			return "Unable to generate image. Rephrase the request.";
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ImageGenerationTools.class);

	private final QuotaManager _quotaManager;

}