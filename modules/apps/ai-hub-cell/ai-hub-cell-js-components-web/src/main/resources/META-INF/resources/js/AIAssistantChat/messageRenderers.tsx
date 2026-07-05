/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

/**
 * Approach D foundation: message rendering is a `type -> component` map instead
 * of a hardcoded switch in the chat host. Each agent registers its balloon
 * under its agent reference code, so adding an agent never edits AIAssistantChat.
 */
export interface AgentMessage {
	payload?: unknown;
	type: string;
}

export type AgentMessageRenderer = React.FC<{payload: any}>;

const registry: Record<string, AgentMessageRenderer> = {};

export function registerMessageRenderer(
	type: string,
	renderer: AgentMessageRenderer
) {
	registry[type] = renderer;
}

export function getMessageRenderer(
	type: string
): AgentMessageRenderer | undefined {
	return registry[type];
}
