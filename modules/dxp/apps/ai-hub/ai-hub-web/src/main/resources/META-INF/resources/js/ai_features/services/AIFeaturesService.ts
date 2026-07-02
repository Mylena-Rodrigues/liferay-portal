/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fetch} from 'frontend-js-web';

const AI_FEATURES_URI = '/o/ai-hub/v1.0/ai-features';

export interface AIFeatures {
	enable: boolean;
	lastModifiedBy?: string;
	lastModifiedDate?: string;
}

async function getAIFeatures(): Promise<AIFeatures> {
	const response = await fetch(AI_FEATURES_URI, {method: 'GET'});

	return response.json();
}

async function patchAIFeatures(
	enable: boolean,
	reason: string,
	comment = ''
): Promise<AIFeatures> {
	const response = await fetch(AI_FEATURES_URI, {
		body: JSON.stringify({comment, enable, reason}),
		headers: {'Content-Type': 'application/json'},
		method: 'PATCH',
	});

	if (!response.ok) {
		const errorBody = await response.json().catch(() => ({}));

		throw new Error(errorBody?.detail || errorBody?.title || '');
	}

	return response.json();
}

export {getAIFeatures, patchAIFeatures};
