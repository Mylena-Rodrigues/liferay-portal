/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {act, fireEvent, render, screen, waitFor} from '@testing-library/react';
import React from 'react';
import {beforeEach, describe, expect, it, vi} from 'vitest';

import ChatbotWidget from '../../components/ChatbotWidget';

import type {Mock} from 'vitest';

vi.mock('../../api', () => ({
	createEventSource: vi.fn(),
	getChatbotConfiguration: vi.fn(),
	postChatMessage: vi.fn(),
}));

vi.mock('../../feedback', () => ({
	submitPositiveFeedback: vi.fn(),
}));

import {
	createEventSource,
	getChatbotConfiguration,
	postChatMessage,
} from '../../api';

const WIDGET_CONFIGURATION = {
	aiHubURL: 'http://localhost:8080',
	chatbotExternalReferenceCode: 'TEST-CHATBOT',
	liferayDXPURL: 'http://localhost:8080',
};

function createFakeEventSource() {
	const listeners: {[type: string]: (event: MessageEvent) => void} = {};

	return {
		addEventListener: (
			type: string,
			listener: (event: MessageEvent) => void
		) => {
			listeners[type] = listener;
		},
		close: vi.fn(),
		listeners,
		readyState: 1,
	};
}

describe('ChatbotWidget suggested questions', () => {
	let fakeEventSource: ReturnType<typeof createFakeEventSource>;

	beforeEach(() => {
		vi.clearAllMocks();

		fakeEventSource = createFakeEventSource();

		(createEventSource as Mock).mockResolvedValue(fakeEventSource);
		(getChatbotConfiguration as Mock).mockResolvedValue({
			active: true,
			suggestedQuestions: [
				'How do I qualify for unemployment benefits?',
				'How do I renew my business license?',
			],
			title: 'Test Chatbot',
		});
		(postChatMessage as Mock).mockResolvedValue({ok: true});
	});

	async function subscribe() {
		await waitFor(() => {
			expect(fakeEventSource.listeners['Subscribe']).toBeDefined();
		});

		act(() => {
			fakeEventSource.listeners['Subscribe']({
				data: 'sink-key',
			} as MessageEvent);
		});
	}

	it('renders the questions disabled until the subscription arrives', async () => {
		render(<ChatbotWidget widgetConfiguration={WIDGET_CONFIGURATION} />);

		const button = await screen.findByRole('button', {
			name: 'How do I qualify for unemployment benefits?',
		});

		expect(button).toBeDisabled();

		await subscribe();

		await waitFor(() => {
			expect(button).not.toBeDisabled();
		});
	});

	it('posts the clicked question as a user message and hides the questions', async () => {
		render(<ChatbotWidget widgetConfiguration={WIDGET_CONFIGURATION} />);

		const button = await screen.findByRole('button', {
			name: 'How do I renew my business license?',
		});

		await subscribe();

		await waitFor(() => {
			expect(button).not.toBeDisabled();
		});

		fireEvent.click(button);

		expect(postChatMessage).toHaveBeenCalledWith(
			'TEST-CHATBOT',
			'sink-key',
			'How do I renew my business license?'
		);

		await waitFor(() => {
			expect(
				screen.queryByRole('button', {
					name: 'How do I renew my business license?',
				})
			).not.toBeInTheDocument();
		});

		expect(
			screen.getByText('How do I renew my business license?')
		).toBeInTheDocument();
	});

	it('keeps the questions visible after an error balloon', async () => {
		render(<ChatbotWidget widgetConfiguration={WIDGET_CONFIGURATION} />);

		await screen.findByRole('button', {
			name: 'How do I qualify for unemployment benefits?',
		});

		await subscribe();

		act(() => {
			fakeEventSource.listeners['Agent Invocation Failed']({
				data: JSON.stringify({data: 'agent failed'}),
			} as MessageEvent);
		});

		expect(
			screen.getByRole('button', {
				name: 'How do I qualify for unemployment benefits?',
			})
		).toBeInTheDocument();
	});

	it('refetches the configuration on every panel open but subscribes once', async () => {
		render(<ChatbotWidget widgetConfiguration={WIDGET_CONFIGURATION} />);

		await screen.findByRole('button', {
			name: 'How do I qualify for unemployment benefits?',
		});

		expect(getChatbotConfiguration).toHaveBeenCalledTimes(1);

		const toggle = screen.getByRole('button', {
			name: 'Open AI Assistant',
		});

		fireEvent.click(toggle);

		await waitFor(() => {
			expect(getChatbotConfiguration).toHaveBeenCalledTimes(2);
		});

		fireEvent.click(
			screen.getByRole('button', {name: 'Close AI Assistant'})
		);
		fireEvent.click(
			screen.getByRole('button', {name: 'Open AI Assistant'})
		);

		await waitFor(() => {
			expect(getChatbotConfiguration).toHaveBeenCalledTimes(3);
		});

		expect(createEventSource).toHaveBeenCalledTimes(1);
	});
});
