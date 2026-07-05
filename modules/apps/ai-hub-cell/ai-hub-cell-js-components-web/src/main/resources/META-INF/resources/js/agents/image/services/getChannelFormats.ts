/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fetch} from 'frontend-js-web';

import {ChipOption} from '../../../shared/types';
import {DEFAULT_CHANNEL_FORMAT_OPTIONS} from '../types';

/**
 * Loads the channel format presets from the Liferay environment. Falls back to
 * the built-in defaults if the environment does not provide any.
 */
export async function getChannelFormats(): Promise<ChipOption[]> {
	try {
		const response = await fetch('/o/ai-hub-cell/v1.0/channel-formats');

		if (!response.ok) {
			throw new Error(
				`Unable to load channel formats: ${response.statusText}`
			);
		}

		const data = await response.json();

		const options: ChipOption[] = (data?.items ?? []).map(
			(item: {group: string; label: string; value: string}) => ({
				group: item.group,
				label: item.label,
				value: item.value,
			})
		);

		return options.length ? options : DEFAULT_CHANNEL_FORMAT_OPTIONS;
	}
	catch (error) {
		console.warn((error as Error).message);

		return DEFAULT_CHANNEL_FORMAT_OPTIONS;
	}
}
