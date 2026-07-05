/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fireInvokeAgent} from '../../shared/agentEvents';
import {EAgent} from '../../shared/types';
import {
	CONTENT_EDITOR_APPLY_EVENT,
	CONTENT_EDITOR_RENDERER,
	ContentEditorAction,
	ContentGenerationContext,
} from './types';

/**
 * Firing helper the New menu (94019) and any external composer (for example the
 * gap "Generate content for gaps" action, 94221) call to start content
 * generation. Composition happens here, at the event layer — callers never
 * import the content balloons.
 */
export function generateContent(context: ContentGenerationContext) {
	fireInvokeAgent({
		agent: EAgent.GENERATE_CONTENT,
		context: context as Record<string, unknown>,
		label: Liferay.Language.get('generate-content'),
	});
}

/** In-context editor actions (94020) running against the active entry. */
export function generateInEditor(
	action: ContentEditorAction,
	context: Record<string, unknown>
) {
	fireInvokeAgent({
		agent: EAgent.GENERATE_CONTENT,
		context: {...context, action},
		label:
			action === 'title'
				? Liferay.Language.get('generate-title')
				: Liferay.Language.get('generate-content'),
		renderAs: CONTENT_EDITOR_RENDERER,
	});
}

/**
 * Asks the content editor mount to write a generated title or body back into
 * the active entry's field.
 */
export function applyToContentEditor(payload: {field: string; value: string}) {
	Liferay.fire(CONTENT_EDITOR_APPLY_EVENT, payload);
}
