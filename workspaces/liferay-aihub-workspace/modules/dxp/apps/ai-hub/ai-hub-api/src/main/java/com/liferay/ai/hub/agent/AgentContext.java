/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.agent;

import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author João Victor Alves
 */
public class AgentContext {

	public static AgentContext.Builder builder() {
		return new AgentContext.Builder();
	}

	public AgentContext(AgentContext.Builder builder) {
		_agentDefinitionExternalReferenceCode =
			builder._agentDefinitionExternalReferenceCode;
		_asynchronous = builder._asynchronous;
		_companyId = builder._companyId;
		_dtoConverterContext = builder._dtoConverterContext;
		_groupId = builder._groupId;
		_input = builder._input;
		_inputVariableNames = builder._inputVariableNames;
		_instructionDefinitionScope = builder._instructionDefinitionScope;
		_oAuth2ApplicationId = builder._oAuth2ApplicationId;
		_serviceContext = builder._serviceContext;
		_sseEventSinkKey = builder._sseEventSinkKey;
		_userId = builder._userId;
		_userToken = builder._userToken;
		_workflowDefinitionName = builder._workflowDefinitionName;

		if (builder._subagentsFunction != null) {
			_subagents = builder._subagentsFunction.apply(this);
		}
		else {
			_subagents = new Object[0];
		}
	}

	public String getAgentDefinitionExternalReferenceCode() {
		return _agentDefinitionExternalReferenceCode;
	}

	public long getCompanyId() {
		return _companyId;
	}

	public DTOConverterContext getDTOConverterContext() {
		return _dtoConverterContext;
	}

	public long getGroupId() {
		return _groupId;
	}

	public Map<String, ?> getInput() {
		return _input;
	}

	public List<String> getInputVariableNames() {
		return _inputVariableNames;
	}

	public String getInstructionDefinitionScope() {
		return _instructionDefinitionScope;
	}

	public long getOAuth2ApplicationId() {
		return _oAuth2ApplicationId;
	}

	public ServiceContext getServiceContext() {
		return _serviceContext;
	}

	public String getSseEventSinkKey() {
		return _sseEventSinkKey;
	}

	public Object[] getSubagents() {
		return _subagents;
	}

	public long getUserId() {
		return _userId;
	}

	public String getUserToken() {
		return _userToken;
	}

	public String getWorkflowDefinitionName() {
		return _workflowDefinitionName;
	}

	public boolean isAsynchronous() {
		return _asynchronous;
	}

	public static class Builder {

		public Builder agentDefinitionExternalReferenceCode(
			String agentDefinitionExternalReferenceCode) {

			_agentDefinitionExternalReferenceCode =
				agentDefinitionExternalReferenceCode;

			return this;
		}

		public Builder asynchronous(boolean asynchronous) {
			_asynchronous = asynchronous;

			return this;
		}

		public AgentContext build() {
			return new AgentContext(this);
		}

		public Builder companyId(long companyId) {
			_companyId = companyId;

			return this;
		}

		public Builder dtoConverterContext(
			DTOConverterContext dtoConverterContext) {

			_dtoConverterContext = dtoConverterContext;

			return this;
		}

		public Builder groupId(long groupId) {
			_groupId = groupId;

			return this;
		}

		public Builder input(Map<String, ?> input) {
			_input = input;

			return this;
		}

		public Builder inputVariableNames(List<String> inputVariableNames) {
			_inputVariableNames = inputVariableNames;

			return this;
		}

		public Builder instructionDefinitionScope(
			String instructionDefinitionScope) {

			_instructionDefinitionScope = instructionDefinitionScope;

			return this;
		}

		public Builder oAuth2ApplicationId(long oAuth2ApplicationId) {
			_oAuth2ApplicationId = oAuth2ApplicationId;

			return this;
		}

		public Builder serviceContext(ServiceContext serviceContext) {
			_serviceContext = serviceContext;

			return this;
		}

		public Builder sseEventSinkKey(String sseEventSinkKey) {
			_sseEventSinkKey = sseEventSinkKey;

			return this;
		}

		public Builder subagents(
			Function<AgentContext, Object[]> subagentsFunction) {

			_subagentsFunction = subagentsFunction;

			return this;
		}

		public Builder userId(long userId) {
			_userId = userId;

			return this;
		}

		public Builder userToken(String userToken) {
			_userToken = userToken;

			return this;
		}

		public Builder workflowDefinitionName(String workflowDefinitionName) {
			_workflowDefinitionName = workflowDefinitionName;

			return this;
		}

		private String _agentDefinitionExternalReferenceCode;
		private boolean _asynchronous;
		private long _companyId;
		private DTOConverterContext _dtoConverterContext;
		private long _groupId;
		private Map<String, ?> _input;
		private List<String> _inputVariableNames;
		private String _instructionDefinitionScope;
		private long _oAuth2ApplicationId;
		private ServiceContext _serviceContext;
		private String _sseEventSinkKey;
		private Function<AgentContext, Object[]> _subagentsFunction;
		private long _userId;
		private String _userToken;
		private String _workflowDefinitionName;

	}

	private final String _agentDefinitionExternalReferenceCode;
	private final boolean _asynchronous;
	private final long _companyId;
	private final DTOConverterContext _dtoConverterContext;
	private final long _groupId;
	private final Map<String, ?> _input;
	private final List<String> _inputVariableNames;
	private final String _instructionDefinitionScope;
	private final long _oAuth2ApplicationId;
	private final ServiceContext _serviceContext;
	private final String _sseEventSinkKey;
	private final Object[] _subagents;
	private final long _userId;
	private final String _userToken;
	private final String _workflowDefinitionName;

}