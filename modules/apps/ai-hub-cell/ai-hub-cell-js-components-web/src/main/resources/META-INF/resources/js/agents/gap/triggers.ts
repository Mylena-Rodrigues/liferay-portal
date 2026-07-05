/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fireInvokeAgent} from '../../shared/agentEvents';
import {EAgent} from '../../shared/types';
import {
	GAP_GENERATE_RENDERER,
	GAP_MATRIX_UPDATE_EVENT,
	GapAnalysisContext,
	MatrixUpdatePayload,
} from './types';

/** Standalone "Get AI-Insights" button and Quick Action (94215). */
export function getGapInsights(context: GapAnalysisContext) {
	fireInvokeAgent({
		agent: EAgent.GAP_ANALYSIS,
		context: context as Record<string, unknown>,
		label: Liferay.Language.get('get-ai-insights'),
	});
}

/** "Find matching assets in CMS" for the project's gaps (94219). */
export function findMatchingAssets(context: GapAnalysisContext) {
	fireInvokeAgent({
		agent: EAgent.GAP_FIND_ASSETS,
		context: context as Record<string, unknown>,
		label: Liferay.Language.get('find-matching-assets-in-cms'),
	});
}

/**
 * "Generate content for gaps" (94221). Reuses the content agent through the
 * event layer (never imports the content balloons); the gap-owned balloon
 * renders the result and attaches drafts to the matrix.
 */
export function generateForGaps(context: GapAnalysisContext) {
	fireInvokeAgent({
		agent: EAgent.GENERATE_CONTENT,
		context: context as Record<string, unknown>,
		label: Liferay.Language.get('generate-content-for-gaps'),
		renderAs: GAP_GENERATE_RENDERER,
	});
}

/**
 * "Create tasks for gaps" is a separately scoped epic; this placeholder keeps
 * the option present in the follow-up list until that work lands.
 */
export function createTasksForGaps(context: GapAnalysisContext) {
	Liferay.fire('cms:aiAssistant:createTasksForGaps', context);
}

/** Live matrix update after attaching or generating (94219 / 94221). */
export function updateMatrixCell(payload: MatrixUpdatePayload) {
	Liferay.fire(GAP_MATRIX_UPDATE_EVENT, payload);
}
