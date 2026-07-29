/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen} from '@testing-library/react';
import React from 'react';
import {describe, expect, it} from 'vitest';

import ChatbotIntro from '../../components/ChatbotIntro';

describe('ChatbotIntro', () => {
	it('renders a Markdown link in the intro message as a link opening in a new tab', () => {
		render(
			<ChatbotIntro
				introMessage="Start with the [service catalog](https://wa.gov/services)."
				title="AskWA"
			/>
		);

		const link = screen.getByRole('link', {name: 'service catalog'});

		expect(link).toHaveAttribute('href', 'https://wa.gov/services');
		expect(link).toHaveAttribute('rel', 'noopener noreferrer');
		expect(link).toHaveAttribute('target', '_blank');
	});

	it('renders each paragraph of the intro message separately', () => {
		const {container} = render(
			<ChatbotIntro
				introMessage={'Welcome to AskWA.\n\nHow can I help you today?'}
				title="AskWA"
			/>
		);

		const paragraphs = container.querySelectorAll('.aihub-intro-text p');

		expect(paragraphs).toHaveLength(2);
		expect(paragraphs[0]).toHaveTextContent('Welcome to AskWA.');
		expect(paragraphs[1]).toHaveTextContent('How can I help you today?');
	});

	it('renders nothing but the title when no intro message is configured', () => {
		const {container} = render(
			<ChatbotIntro introMessage="" title="AskWA" />
		);

		expect(
			container.querySelector('.aihub-intro-text')
		).toBeEmptyDOMElement();
		expect(screen.getByText('AskWA')).toBeInTheDocument();
	});

	it('does not render raw HTML in the intro message', () => {
		const {container} = render(
			<ChatbotIntro
				introMessage="Welcome <img src=x onerror=alert(1)> to AskWA."
				title="AskWA"
			/>
		);

		expect(container.querySelector('img')).toBeNull();
		expect(container.textContent).toContain('<img src=x onerror=alert(1)>');
	});
});
