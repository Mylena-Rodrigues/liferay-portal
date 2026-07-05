/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {EAgent} from '../../shared/types';

/**
 * Dev-only: makes useAgent and the content-type service return canned data so
 * the real balloons run their full lifecycle (prompt, generating, result)
 * without a backend. Only present on the style-preview branch.
 */
export const DEMO_ENABLED = true;

const IMAGE_SRC =
	"data:image/svg+xml;utf8,<svg xmlns='http://www.w3.org/2000/svg' width='160' height='120'><rect width='160' height='120' fill='%23c9d3e0'/><circle cx='80' cy='60' r='34' fill='%23f08a3c'/></svg>";

export function getMockAgentData(agent: string): unknown {
	if (agent === EAgent.GENERATE_IMAGE) {
		return [
			{alt: 'Demo image', id: 'i1', url: IMAGE_SRC},
			{alt: 'Demo image', id: 'i2', url: IMAGE_SRC},
		];
	}

	if (agent === EAgent.ADAPT_CHANNELS) {
		return {
			folder: {
				href: '#folder',
				id: 'f1',
				title: 'Madrid Social Media Formats',
			},
			items: [
				{id: 'a1', status: 'success', title: 'madrid_ig-post.jpg'},
				{id: 'a2', status: 'success', title: 'madrid_li-banner.jpg'},
				{
					id: 'a3',
					reason: 'incompatible_source',
					status: 'failed',
					title: 'logo.png',
				},
			],
		};
	}

	if (agent === EAgent.GENERATE_CONTENT) {

		// Superset covering the single, multiple, editor and gap consumers.

		return {
			cells: ['1-0', '1-2'],
			drafts: [
				{href: '#d1', id: 'd1', subtitle: 'Draft', title: 'Marketing trends'},
				{
					href: '#d2',
					id: 'd2',
					subtitle: 'Draft',
					title: 'Onboarding guide',
				},
			],
			value: 'Exploring the Wonders of Japan',
		};
	}

	if (agent === EAgent.GAP_ANALYSIS) {
		return {
			gaps: [
				{
					count: 0,
					dimensions: 'Operations Leadership x Awareness',
					id: 'g1',
					reason: 'No awareness-stage content for any persona',
					severity: 'high',
					threshold: 1,
				},
				{
					count: 0,
					dimensions: 'Procurement x Decision',
					id: 'g2',
					reason: 'Decision and retention stages are uncovered',
					severity: 'medium',
					threshold: 1,
				},
			],
			summary: '5 personas x 3 funnel stages analyzed. 8 gaps found.',
		};
	}

	if (agent === EAgent.GAP_FIND_ASSETS) {
		return {
			assets: [
				{
					cellId: '1-0',
					dimensions: 'Procurement x Awareness',
					id: 'm1',
					status: 'Approved',
					statusApproved: true,
					title: 'Vendor evaluation checklist for procurement',
					type: 'Basic Content',
					url: '#m1',
				},
				{
					cellId: '1-2',
					dimensions: 'Procurement x Decision',
					id: 'm2',
					status: 'Draft',
					title: 'Why Operations Teams Choose Liferay',
					type: 'Blog',
					url: '#m2',
				},
			],
		};
	}

	return null;
}
