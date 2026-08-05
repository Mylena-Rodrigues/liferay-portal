/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.internal.workflow.node.delegate;

import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.workflow.WorkflowException;
import com.liferay.portal.kernel.workflow.WorkflowInstanceManager;
import com.liferay.portal.workflow.kaleo.model.KaleoInstanceToken;
import com.liferay.portal.workflow.kaleo.model.KaleoNode;
import com.liferay.portal.workflow.kaleo.model.KaleoTransition;
import com.liferay.portal.workflow.kaleo.runtime.ExecutionContext;
import com.liferay.portal.workflow.kaleo.runtime.KaleoSignaler;

import java.io.Serializable;

import java.util.Map;

/**
 * @author Feliphe Marinho
 */
public class WorkflowNodeUtil {

	public static void completeWorkflowNode(
			ExecutionContext executionContext, KaleoSignaler kaleoSignaler,
			Map<String, Serializable> workflowContext,
			WorkflowInstanceManager workflowInstanceManager)
		throws Exception {

		KaleoInstanceToken kaleoInstanceToken =
			executionContext.getKaleoInstanceToken();

		KaleoNode kaleoNode = kaleoInstanceToken.getCurrentKaleoNode();

		KaleoTransition kaleoTransition = kaleoNode.getDefaultKaleoTransition();

		completeWorkflowNode(
			executionContext, kaleoSignaler, kaleoTransition.getName(),
			workflowContext, workflowInstanceManager);
	}

	public static void completeWorkflowNode(
			ExecutionContext executionContext, KaleoSignaler kaleoSignaler,
			String transitionName, Map<String, Serializable> workflowContext,
			WorkflowInstanceManager workflowInstanceManager)
		throws Exception {

		KaleoInstanceToken kaleoInstanceToken =
			executionContext.getKaleoInstanceToken();

		try {
			workflowInstanceManager.updateWorkflowContext(
				kaleoInstanceToken.getCompanyId(),
				kaleoInstanceToken.getKaleoInstanceId(), workflowContext);

			ServiceContext serviceContext = new ServiceContext();

			serviceContext.setCompanyId(kaleoInstanceToken.getCompanyId());
			serviceContext.setUserId(kaleoInstanceToken.getUserId());

			kaleoSignaler.signalExit(
				transitionName,
				new ExecutionContext(
					kaleoInstanceToken, workflowContext, serviceContext),
				false);
		}
		catch (Exception exception) {
			throw new WorkflowException(
				"Unable to signal next transition", exception);
		}
	}

}