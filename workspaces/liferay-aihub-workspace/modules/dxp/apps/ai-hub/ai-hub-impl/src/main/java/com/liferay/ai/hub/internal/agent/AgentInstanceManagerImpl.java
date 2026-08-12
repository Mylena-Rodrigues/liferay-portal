/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.internal.agent;

import com.liferay.ai.hub.agent.AgentContext;
import com.liferay.ai.hub.agent.AgentInstanceManager;
import com.liferay.ai.hub.agent.DefaultAgent;
import com.liferay.ai.hub.agent.SupervisorAgent;
import com.liferay.ai.hub.internal.langchain4j.agentic.internal.InternalAgentImpl;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.util.ProxyUtil;

import dev.langchain4j.agentic.planner.AgentArgument;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Feliphe Marinho
 */
@Component(service = AgentInstanceManager.class)
public class AgentInstanceManagerImpl implements AgentInstanceManager {

	@Override
	public Object invoke(AgentContext agentContext) {
		if (!Objects.equals(
				agentContext.getAgentDefinitionExternalReferenceCode(),
				"L_SUPERVISOR")) {

			return _invoke(() -> _defaultAgent.invoke(agentContext));
		}

		Object[] subagents = agentContext.getSubagents();

		if (!_isDirectlyInvocable(subagents)) {
			_supervisorAgent.invoke(agentContext);

			return null;
		}

		InternalAgentImpl internalAgentImpl =
			(InternalAgentImpl)ProxyUtil.getInvocationHandler(subagents[0]);

		internalAgentImpl.setOutBoundEventName("Chat Message Sent");

		return _invoke(() -> internalAgentImpl.invoke(agentContext.getInput()));
	}

	@Override
	public void resume(AgentContext agentContext, long agentInstanceId)
		throws Exception {

		_defaultAgent.resume(agentContext, agentInstanceId);
	}

	private Object _invoke(Callable<Object> callable) {
		try {
			return TransactionInvokerUtil.invoke(_transactionConfig, callable);
		}
		catch (Throwable throwable) {
			return ReflectionUtil.throwException(throwable);
		}
	}

	private boolean _isDirectlyInvocable(Object[] subagents) {
		if (subagents.length != 1) {
			return false;
		}

		InternalAgentImpl internalAgentImpl =
			(InternalAgentImpl)ProxyUtil.getInvocationHandler(subagents[0]);

		List<AgentArgument> agentArguments = internalAgentImpl.arguments();

		if (agentArguments.size() != 1) {
			return false;
		}

		AgentArgument agentArgument = agentArguments.get(0);

		return Objects.equals(agentArgument.name(), "request");
	}

	private static final TransactionConfig _transactionConfig =
		TransactionConfig.Factory.create(
			Propagation.NOT_SUPPORTED, new Class<?>[] {Exception.class});

	@Reference
	private DefaultAgent _defaultAgent;

	@Reference
	private SupervisorAgent _supervisorAgent;

}