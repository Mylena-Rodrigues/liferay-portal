/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.internal.workflow.node.delegate;

import com.liferay.ai.hub.rest.resource.v1_0.util.SseUtil;
import com.liferay.ai.hub.workflow.node.ServiceNodeDelegate;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowInstanceManager;
import com.liferay.portal.workflow.kaleo.model.KaleoInstanceToken;
import com.liferay.portal.workflow.kaleo.runtime.ExecutionContext;
import com.liferay.portal.workflow.kaleo.runtime.KaleoSignaler;

import java.io.Serializable;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Feliphe Marinho
 */
@Component(service = ServiceNodeDelegate.class)
public class SendContentUpdatedEventServiceNodeDelegate
	implements ServiceNodeDelegate {

	@Override
	public String execute(
			ExecutionContext executionContext,
			Map<String, String> inputVariables,
			Map<String, Serializable> workflowContext)
		throws Exception {

		KaleoInstanceToken kaleoInstanceToken =
			executionContext.getKaleoInstanceToken();

		SseUtil.send(
			_getAgentDefinitionExternalReferenceCodes(workflowContext),
			inputVariables.get("linkResponse"), "Content Updated",
			kaleoInstanceToken.getCurrentKaleoNodeName(),
			MapUtil.getString(workflowContext, "sseEventSinkKey"));

		workflowContext.put(
			"output", "Content successfully created and linked to the project");

		WorkflowNodeUtil.completeWorkflowNode(
			executionContext, _kaleoSignaler, workflowContext,
			_workflowInstanceManager);

		return StringPool.BLANK;
	}

	@Override
	public String getKey() {
		return "javaDelegate#sendContentUpdatedEvent";
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

	@Reference
	private KaleoSignaler _kaleoSignaler;

	@Reference
	private WorkflowInstanceManager _workflowInstanceManager;

}