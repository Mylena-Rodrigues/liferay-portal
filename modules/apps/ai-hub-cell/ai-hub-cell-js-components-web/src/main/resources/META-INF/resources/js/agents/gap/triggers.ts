/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fireInvokeAgent} from '../../shared/agentEvents';
import {EAgent} from '../../shared/types';
import {
	GAP_GENERATE_RENDERER,
	GAP_MATRIX_UPDATE_EVENT,
	Gap,
	GapAnalysisContext,
	MatrixUpdatePayload,
} from './types';

/** Standalone "Get GAP Insights" button and Quick Action (94215). */
export function getGapInsights(context: GapAnalysisContext) {
	fireInvokeAgent({
		agent: EAgent.GAP_ANALYSIS,
		context: context as Record<string, unknown>,
		label: Liferay.Language.get('get-gap-insights'),
	});
}

/** Per-gap "Find matching assets in CMS" follow-up (94219). */
export function findMatchingAssets(gap: Gap) {
	fireInvokeAgent({
		agent: EAgent.GAP_FIND_ASSETS,
		context: {gap} as Record<string, unknown>,
		label: Liferay.Language.get('find-matching-assets-in-cms'),
	});
}

/**
 * Per-gap "Generate content for gaps" follow-up (94221). Reuses the content
 * agent through the event layer (never imports the content balloons); the
 * gap-owned balloon renders the result and attaches drafts to the matrix.
 */
export function generateForGap(gap: Gap) {
	fireInvokeAgent({
		agent: EAgent.GENERATE_CONTENT,
		context: {gap} as Record<string, unknown>,
		label: Liferay.Language.get('generate-content-for-gaps'),
		renderAs: GAP_GENERATE_RENDERER,
	});
}

/** Live matrix update after attaching or generating (94219 / 94221). */
export function updateMatrixCell(payload: MatrixUpdatePayload) {
	Liferay.fire(GAP_MATRIX_UPDATE_EVENT, payload);
}
