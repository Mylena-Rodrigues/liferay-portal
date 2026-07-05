/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fetch} from 'frontend-js-web';

import {DEMO_ENABLED} from '../../_demo/mockAgent';
import {ContentType} from '../types';

/**
 * Loads the content types (structures) the user can generate, from the Liferay
 * environment. Returns an empty list on failure so the caller can surface the
 * error rather than inventing options.
 */
export async function getContentTypes(): Promise<ContentType[]> {
	if (DEMO_ENABLED) {
		return [
			{id: 1, name: 'Basic Web Content'},
			{id: 2, name: 'Blog'},
		];
	}

	try {
		const response = await fetch('/o/ai-hub-cell/v1.0/content-types');

		if (!response.ok) {
			throw new Error(
				`Unable to load content types: ${response.statusText}`
			);
		}

		const data = await response.json();

		return (data?.items ?? []).map(
			(item: {id: number | string; name: string}) => ({
				id: item.id,
				name: item.name,
			})
		);
	}
	catch (error) {
		console.warn((error as Error).message);

		return [];
	}
}
