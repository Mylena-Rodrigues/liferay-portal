/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.internal.workflow.node.delegate;

import com.liferay.ai.hub.workflow.node.ServiceNodeDelegate;
import com.liferay.portal.kernel.workflow.WorkflowNodeManager;
import com.liferay.portal.workflow.kaleo.model.KaleoInstanceToken;
import com.liferay.portal.workflow.kaleo.model.KaleoNode;
import com.liferay.portal.workflow.kaleo.model.KaleoTransition;
import com.liferay.portal.workflow.kaleo.runtime.ExecutionContext;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Carolina Barbosa
 */
public abstract class BaseServiceNodeDelegate implements ServiceNodeDelegate {

	@Override
	public String execute(
			ExecutionContext executionContext,
			Map<String, String> inputVariables,
			Map<String, Serializable> workflowContext)
		throws Exception {

		String output = doExecute(
			executionContext, inputVariables, workflowContext);

		workflowContext.put("output", output);

		KaleoInstanceToken kaleoInstanceToken =
			executionContext.getKaleoInstanceToken();

		KaleoNode kaleoNode = kaleoInstanceToken.getCurrentKaleoNode();

		List<KaleoTransition> kaleoTransitions =
			kaleoNode.getKaleoTransitions();

		KaleoTransition kaleoTransition = kaleoTransitions.get(0);

		workflowNodeManager.completeWorkflowNode(
			kaleoInstanceToken.getCompanyId(), kaleoInstanceToken.getUserId(),
			kaleoInstanceToken.getKaleoInstanceTokenId(),
			kaleoTransition.getName(), workflowContext, false);

		return output;
	}

	protected abstract String doExecute(
			ExecutionContext executionContext,
			Map<String, String> inputVariables,
			Map<String, Serializable> workflowContext)
		throws Exception;

	@Reference
	protected WorkflowNodeManager workflowNodeManager;

}