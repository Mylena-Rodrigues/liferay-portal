/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

export type AgentMessageRenderer = React.FC<{payload: any}>;

export interface AgentMessage {
	payload?: unknown;
	type: string;
}

export interface AgentRenderer {
	renderer: AgentMessageRenderer;
	type: string;
}

/**
 * A self-contained agent plugin. Each agent declares one AgentFlow and registers
 * it with the framework; the chat host reads only the framework, never the
 * individual agents, so adding an agent never edits AIAssistantChat.
 */
export interface AgentFlow {
	renderers: AgentRenderer[];
}
