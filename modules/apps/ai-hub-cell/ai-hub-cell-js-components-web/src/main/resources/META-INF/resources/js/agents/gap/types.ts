/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export const GAP_GENERATE_RENDERER = 'GAP_GENERATE_CONTENT';

export const GAP_MATRIX_UPDATE_EVENT = 'cms:aiAssistant:gapMatrixUpdate';

export type GapSeverity = 'high' | 'low' | 'medium';

export interface Gap {
	count: number;
	dimensions: string;
	id: string;
	reason: string;
	severity: GapSeverity;
	threshold: number;
}

export interface GapAnalysisContext {
	projectId?: number | string;
	selectedCells?: string[];
}

export interface GapAnalysisResult {
	gaps: Gap[];
	summary: string;
}

export interface MatrixCell {
	cellId: string;
	column: string;
	count: number;
	row: string;
	threshold: number;
}

export interface MatrixUpdatePayload {
	cellId: string;
	delta: number;
}
