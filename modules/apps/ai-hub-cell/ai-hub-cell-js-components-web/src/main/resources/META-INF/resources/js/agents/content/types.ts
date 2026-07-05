/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {AgentResultItem} from '../../shared/types';

export const CONTENT_EDITOR_RENDERER = 'CONTENT_EDITOR';

export const CONTENT_EDITOR_APPLY_EVENT = 'cms:aiAssistant:contentEditorApply';

export type ContentEditorAction = 'content' | 'title';

export interface ContentType {
	id: number | string;
	name: string;
}

export interface ContentGenerationContext {
	brief?: string;
	count?: number;

	/**
	 * When true, the active instruction asks the user to pick a content type
	 * before generating; the type Select is shown only then.
	 */
	requiresContentType?: boolean;

	spaceId?: number | string;
	structureId?: number | string;
	structureName?: string;
}

export interface ContentEditorContext {
	action: ContentEditorAction;
	assetId?: number | string;
	structureId?: number | string;
}

export interface GeneratedContentResult {
	drafts: AgentResultItem[];
}

export interface ContentEditorResult {
	value: string;
}

export const CONTENT_EDITOR_QUICK_ACTIONS: string[] = [
	Liferay.Language.get('generate-title'),
	Liferay.Language.get('generate-content'),
];
