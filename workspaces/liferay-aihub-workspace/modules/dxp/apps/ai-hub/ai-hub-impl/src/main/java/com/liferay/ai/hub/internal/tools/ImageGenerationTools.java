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
import com.liferay.portal.kernel.security.auth.CompanyInheritableThreadLocalCallable;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.workflow.kaleo.model.KaleoInstanceToken;
import com.liferay.portal.workflow.kaleo.runtime.ExecutionContext;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.data.image.Image;
import dev.langchain4j.invocation.InvocationParameters;
import dev.langchain4j.model.image.ImageModel;
import dev.langchain4j.model.output.Response;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * @author Feliphe Marinho
 * @author Mario Gomes
 */
public class ImageGenerationTools {

	public ImageGenerationTools(QuotaManager quotaManager) {
		_callable = new CompanyInheritableThreadLocalCallable<>(
			() -> {
				ExecutionContext executionContext = _invocationParameters.get(
					"executionContext");

				KaleoInstanceToken kaleoInstanceToken =
					executionContext.getKaleoInstanceToken();

				ImageModel imageModel =
					GoogleGenAiUtil.createGoogleGenAiImageModel(
						quotaManager, executionContext.getServiceContext());

				Response<List<Image>> response = imageModel.generate(
					_prompt, -1);

				for (Image image : response.content()) {
					SseUtil.send(
						new String[] {
							MapUtil.getString(
								executionContext.getWorkflowContext(),
								"agentDefinitionExternalReferenceCode")
						},
						image.base64Data(),
						MapUtil.getString(
							executionContext.getWorkflowContext(),
							"outBoundEventName", "Chat Message Sent"),
						kaleoInstanceToken.getCurrentKaleoNodeName(),
						JSONUtil.put("mimeType", image.mimeType()),
						MapUtil.getString(
							executionContext.getWorkflowContext(),
							"sseEventSinkKey"),
						"image");
				}

				return "Images have been generated.";
			});
	}

	@Tool("Generate images based on a prompt.")
	public String generateImages(
		InvocationParameters invocationParameters,
		@P("Description of the images to be generated.") String prompt) {

		_invocationParameters = invocationParameters;
		_prompt = prompt;

		try {
			return _callable.call();
		}
		catch (Exception exception) {
			_log.error(exception);

			return "Unable to generate image. Rephrase the request.";
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ImageGenerationTools.class);

	private final Callable<String> _callable;
	private InvocationParameters _invocationParameters;
	private String _prompt;

}