/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export const CONTENT_GAP_ANALYSIS_ERC = 'L_CONTENT_GAP_ANALYSIS';

interface Gap {
	funnelStageName: string;
	personaName: string;
	reason: string;
	severity: string;
}

interface GapAnalysis {
	gaps: Gap[];
	summary?: {
		overview?: string;
	};
}

export default function formatContentGapAnalysis(data: string): string | null {
	let gapAnalysis: GapAnalysis;

	try {
		gapAnalysis = JSON.parse(data);
	}
	catch {
		return null;
	}

	if (!gapAnalysis || !Array.isArray(gapAnalysis.gaps)) {
		return null;
	}

	const overview = gapAnalysis.summary?.overview ?? '';

	const gaps = gapAnalysis.gaps
		.map(
			(gap) =>
				`- **${gap.personaName} / ${gap.funnelStageName}** (${gap.severity}) — ${gap.reason}`
		)
		.join('\n');

	return [overview, gaps].filter(Boolean).join('\n\n') || null;
}
