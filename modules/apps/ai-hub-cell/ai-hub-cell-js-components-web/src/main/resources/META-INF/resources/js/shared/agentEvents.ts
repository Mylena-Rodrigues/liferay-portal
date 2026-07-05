/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {EAgent} from './types';

/**
 * Generic entry point for every embedded agent. Any trigger (a dataset menu, a
 * field sparkle, a kebab action, a bulk action or a standalone button) fires
 * this event; the AI Assistant is the single listener and opens routed to the
 * requested flow with the given context.
 */
export const AI_ASSISTANT_INVOKE_EVENT = 'cms:aiAssistant:invoke';

export interface InvokeAgentEventPayload {
	agent: EAgent;
	context?: Record<string, unknown>;
	initialMessage?: string;
	label?: string;

	/**
	 * Overrides the renderer the host picks. Lets a follow-up flow reuse a
	 * backend agent (for example the gap "Generate content" action reusing the
	 * content agent) while rendering its own balloon.
	 */
	renderAs?: string;
}

export function fireInvokeAgent(payload: InvokeAgentEventPayload) {
	Liferay.fire(AI_ASSISTANT_INVOKE_EVENT, payload);
}
