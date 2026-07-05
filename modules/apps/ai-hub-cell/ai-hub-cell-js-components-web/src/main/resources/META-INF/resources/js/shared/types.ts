/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export enum EAgent {
	ADAPT_CHANNELS = 'L_ADAPT_CHANNELS',
	GAP_ANALYSIS = 'L_GAP_ANALYSIS',
	GAP_FIND_ASSETS = 'L_GAP_FIND_ASSETS',
	GENERATE_CONTENT = 'L_GENERATE_CONTENT',
	GENERATE_IMAGE = 'L_GENERATE_IMAGE',
}

export type AgentStatus = 'empty' | 'error' | 'idle' | 'loading' | 'ready';

export type ResultItemStatus = 'failed' | 'skipped' | 'success';

export interface AgentResultItem {
	href?: string;
	id: string;
	reason?: string;
	status?: ResultItemStatus;
	subtitle?: string;
	title: string;
}

export interface ChipOption {
	group?: string;
	label: string;
	value: string;
}
