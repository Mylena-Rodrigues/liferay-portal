/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ChipOption} from '../../shared/types';

export const IMAGE_STYLE_OPTIONS: ChipOption[] = [
	{label: Liferay.Language.get('photorealistic'), value: 'photorealistic'},
	{label: Liferay.Language.get('illustration'), value: 'illustration'},
	{label: Liferay.Language.get('digital-art'), value: 'digital-art'},
	{label: Liferay.Language.get('watercolor'), value: 'watercolor'},
];

export const CHANNEL_FORMAT_OPTIONS: ChipOption[] = [
	{group: 'Instagram', label: Liferay.Language.get('post'), value: 'ig-post'},
	{
		group: 'Instagram',
		label: Liferay.Language.get('story'),
		value: 'ig-story',
	},
	{group: 'LinkedIn', label: Liferay.Language.get('post'), value: 'li-post'},
	{
		group: 'LinkedIn',
		label: Liferay.Language.get('article'),
		value: 'li-article',
	},
	{
		group: 'LinkedIn',
		label: Liferay.Language.get('image'),
		value: 'li-image',
	},
	{
		group: 'LinkedIn',
		label: Liferay.Language.get('banner'),
		value: 'li-banner',
	},
	{group: 'Facebook', label: Liferay.Language.get('post'), value: 'fb-post'},
	{
		group: 'Facebook',
		label: Liferay.Language.get('cover'),
		value: 'fb-cover',
	},
	{group: 'X', label: Liferay.Language.get('post'), value: 'x-post'},
	{group: 'X', label: Liferay.Language.get('header'), value: 'x-header'},
];

export interface ImageGenerationContext {
	allowedFormats?: string[];
	destination?: 'field' | 'folder';
	fieldId?: string;
	folderId?: number | string;
	prompt?: string;
	style?: string;
}

export interface GeneratedImage {
	alt?: string;
	id: string;
	url: string;
}

export interface AdaptContext {
	fileEntryId?: number | string;
	fileEntryIds?: Array<number | string>;
	sourceName?: string;
}
