/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen} from '@testing-library/react';
import React from 'react';
import {describe, expect, it} from 'vitest';

import ChatbotFooter from '../../components/ChatbotFooter';

describe('ChatbotFooter', () => {
	it('renders a Markdown link in the disclaimer as a link opening in a new tab', () => {
		render(
			<ChatbotFooter disclaimerMessage="[Leave feedback on AskWA ChatBot](https://wa.gov/feedback)" />
		);

		const link = screen.getByRole('link', {
			name: 'Leave feedback on AskWA ChatBot',
		});

		expect(link).toHaveAttribute('href', 'https://wa.gov/feedback');
		expect(link).toHaveAttribute('rel', 'noopener noreferrer');
		expect(link).toHaveAttribute('target', '_blank');
	});

	it('renders emphasis in the disclaimer', () => {
		const {container} = render(
			<ChatbotFooter disclaimerMessage="**Notice:** responses may be inaccurate." />
		);

		expect(container.querySelector('strong')).toHaveTextContent('Notice:');
	});

	it('does not render a javascript URL in the disclaimer as a usable link', () => {
		const {container} = render(
			<ChatbotFooter disclaimerMessage="[Click here](javascript:alert(1))" />
		);

		expect(container.querySelector('a')).toHaveAttribute('href', '');
	});

	it('does not render raw HTML in the disclaimer', () => {
		const {container} = render(
			<ChatbotFooter disclaimerMessage="Read this <img src=x onerror=alert(1)> carefully." />
		);

		expect(container.querySelector('img')).toBeNull();
		expect(container.textContent).toContain('<img src=x onerror=alert(1)>');
	});

	it('renders the default disclaimer when none is configured', () => {
		render(<ChatbotFooter disclaimerMessage="" />);

		expect(
			screen.getByText(
				'AI generated responses may be inaccurate. Please review carefully.'
			)
		).toBeInTheDocument();
	});
});
