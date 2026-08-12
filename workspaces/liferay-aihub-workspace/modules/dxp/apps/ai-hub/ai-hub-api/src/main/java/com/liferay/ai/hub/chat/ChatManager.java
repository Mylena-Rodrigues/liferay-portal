/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.chat;

import com.liferay.ai.hub.agent.AgentContext;

/**
 * @author Feliphe Marinho
 */
public interface ChatManager {

	public Object[] getSubagents(
		AgentContext agentContext, String chatbotExternalReferenceCode);

}