/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import buildAssistantMessage from '../../../../src/main/resources/META-INF/resources/js/AIAssistantChat/utils/buildAssistantMessage';
import {CONTENT_GAP_ANALYSIS_ERC} from '../../../../src/main/resources/META-INF/resources/js/AIAssistantChat/utils/formatContentGapAnalysis';

const GAP_ANSWER = JSON.stringify({
	gaps: [
		{
			funnelStageName: 'Awareness',
			personaName: 'Decision Maker',
			reason: 'No content yet.',
			severity: 'high',
		},
	],
	summary: {overview: 'One gap to address.'},
});

describe('buildAssistantMessage', () => {
	it('formats a Content Gap Analysis text answer into bulleted markdown', () => {
		expect(
			buildAssistantMessage({
				agentDefinitionExternalReferenceCodes: [
					CONTENT_GAP_ANALYSIS_ERC,
				],
				data: GAP_ANSWER,
				type: 'text',
			})
		).toEqual({
			agentDefinitionExternalReferenceCodes: [CONTENT_GAP_ANALYSIS_ERC],
			sender: 'assistant',
			text:
				'One gap to address.\n\n' +
				'- **Decision Maker / Awareness** (high) — No content yet.',
		});
	});

	it('formats when the answer omits the type (defaults to text)', () => {
		expect(
			buildAssistantMessage({
				agentDefinitionExternalReferenceCodes: [
					CONTENT_GAP_ANALYSIS_ERC,
				],
				data: GAP_ANSWER,
			}).text
		).toContain('- **Decision Maker / Awareness** (high)');
	});

	it('passes through text answers from agents without a formatter', () => {
		expect(
			buildAssistantMessage({
				agentDefinitionExternalReferenceCodes: ['L_SOMETHING_ELSE'],
				data: 'Plain answer.',
			}).text
		).toBe('Plain answer.');
	});

	it('leaves image answers untouched (owned by the image feature)', () => {
		expect(
			buildAssistantMessage({
				agentDefinitionExternalReferenceCodes: [
					CONTENT_GAP_ANALYSIS_ERC,
				],
				data: 'iVBORw0KGgo=',
				type: 'image',
			}).text
		).toBe('iVBORw0KGgo=');
	});

	it('defaults a missing data field to an empty string', () => {
		expect(buildAssistantMessage({}).text).toBe('');
	});

	it('defaults missing agent reference codes to an empty array', () => {
		expect(
			buildAssistantMessage({data: 'hi'})
				.agentDefinitionExternalReferenceCodes
		).toEqual([]);
	});
});
