/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fireEvent, render, screen} from '@testing-library/react';
import React from 'react';
import {describe, expect, it, vi} from 'vitest';

import ChatbotSuggestions from '../../components/ChatbotSuggestions';

describe('ChatbotSuggestions', () => {
	it('renders one button per question', () => {
		render(
			<ChatbotSuggestions
				disabled={false}
				onSelect={vi.fn()}
				questions={[
					'How do I qualify for unemployment benefits?',
					'How do I renew my business license?',
				]}
			/>
		);

		expect(
			screen.getByRole('button', {
				name: 'How do I qualify for unemployment benefits?',
			})
		).toBeInTheDocument();
		expect(
			screen.getByRole('button', {
				name: 'How do I renew my business license?',
			})
		).toBeInTheDocument();
	});

	it('invokes onSelect with the exact question text on click', () => {
		const onSelect = vi.fn();

		render(
			<ChatbotSuggestions
				disabled={false}
				onSelect={onSelect}
				questions={['I need help finding a job.']}
			/>
		);

		fireEvent.click(
			screen.getByRole('button', {name: 'I need help finding a job.'})
		);

		expect(onSelect).toHaveBeenCalledTimes(1);
		expect(onSelect).toHaveBeenCalledWith('I need help finding a job.');
	});

	it('blocks clicks while disabled', () => {
		const onSelect = vi.fn();

		render(
			<ChatbotSuggestions
				disabled={true}
				onSelect={onSelect}
				questions={['How do I register to vote?']}
			/>
		);

		const button = screen.getByRole('button', {
			name: 'How do I register to vote?',
		});

		expect(button).toBeDisabled();

		fireEvent.click(button);

		expect(onSelect).not.toHaveBeenCalled();
	});
});
