/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom';

import AIAssistantTriggerButton from '../../../src/main/resources/META-INF/resources/js/AIAssistantChat/AIAssistantTriggerButton';

jest.mock(
	'@liferay/ai-hub-js-components-web',
	() => ({
		__esModule: true,
		default: {
			AIAssistantTriggerButton: ({label}: {label: string}) => (
				<button type="button">{label}</button>
			),
		},
	}),
	{virtual: true}
);

describe('AIAssistantTriggerButton', () => {
	it('renders the AI Hub trigger button when the AI Hub frontend is deployed', async () => {
		render(
			<AIAssistantTriggerButton
				instructionDefinitionScope="cms"
				label="ai-assistant"
			/>
		);

		expect(
			await screen.findByRole('button', {name: 'ai-assistant'})
		).toBeInTheDocument();
	});
});
