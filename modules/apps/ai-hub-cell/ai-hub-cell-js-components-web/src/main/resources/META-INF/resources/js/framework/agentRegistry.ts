/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {registerRenderer} from './messageRegistry';
import {AgentFlow} from './types';

const flows: AgentFlow[] = [];

/**
 * Registers an agent plugin: the framework records the flow and wires each of
 * its renderers into the message registry the host reads.
 */
export function registerAgent(flow: AgentFlow) {
	flows.push(flow);

	flow.renderers.forEach((agentRenderer) =>
		registerRenderer(agentRenderer.type, agentRenderer.renderer)
	);
}

export function getAgentFlows(): AgentFlow[] {
	return flows;
}
