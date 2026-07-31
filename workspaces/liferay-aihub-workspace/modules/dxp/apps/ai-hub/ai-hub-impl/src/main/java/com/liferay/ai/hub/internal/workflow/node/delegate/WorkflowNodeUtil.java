/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.internal.workflow.node.delegate;

import com.liferay.portal.kernel.workflow.WorkflowNodeManager;
import com.liferay.portal.workflow.kaleo.model.KaleoInstanceToken;
import com.liferay.portal.workflow.kaleo.model.KaleoNode;
import com.liferay.portal.workflow.kaleo.model.KaleoTransition;
import com.liferay.portal.workflow.kaleo.runtime.ExecutionContext;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

/**
 * @author Feliphe Marinho
 */
public class WorkflowNodeUtil {

	public static void completeWorkflowNode(
			ExecutionContext executionContext,
			Map<String, Serializable> workflowContext,
			WorkflowNodeManager workflowNodeManager)
		throws Exception {

		KaleoInstanceToken kaleoInstanceToken =
			executionContext.getKaleoInstanceToken();

		KaleoNode kaleoNode = kaleoInstanceToken.getCurrentKaleoNode();

		List<KaleoTransition> kaleoTransitions =
			kaleoNode.getKaleoTransitions();

		KaleoTransition kaleoTransition = kaleoTransitions.get(0);

		completeWorkflowNode(
			executionContext, kaleoTransition.getName(), workflowContext,
			workflowNodeManager);
	}

	public static void completeWorkflowNode(
			ExecutionContext executionContext, String transitionName,
			Map<String, Serializable> workflowContext,
			WorkflowNodeManager workflowNodeManager)
		throws Exception {

		KaleoInstanceToken kaleoInstanceToken =
			executionContext.getKaleoInstanceToken();

		workflowNodeManager.completeWorkflowNode(
			kaleoInstanceToken.getCompanyId(), kaleoInstanceToken.getUserId(),
			kaleoInstanceToken.getKaleoInstanceTokenId(), transitionName,
			workflowContext, false);
	}

}