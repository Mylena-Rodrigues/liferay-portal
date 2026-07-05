/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {AgentResultItem} from '../../shared/types';

export interface SpaceOption {
	id: string;
	name: string;
}

export interface ContentGenerationContext {
	brief?: string;
	count?: number;
	spaceId?: number | string;
	spaceOptions?: SpaceOption[];
	structureId?: number | string;
	structureName?: string;
	submitToWorkflow?: boolean;
}

export interface GeneratedContentResult {
	drafts: AgentResultItem[];
}

export const CONTENT_EDITOR_QUICK_ACTIONS: string[] = [
	Liferay.Language.get('generate-title'),
	Liferay.Language.get('generate-content'),
];
