/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	getChatbotDefinitions,
	patchChatbotDefinition,
} from '../../../../src/main/resources/META-INF/resources/js/chatbot_form/services/ChatbotService';

const mockFetch = jest.fn();

jest.mock('frontend-js-web', () => ({
	fetch: (...args: any[]) => mockFetch(...args),
}));

(global as any).Liferay = {
	ThemeDisplay: {
		getBCP47LanguageId: () => 'en-US',
	},
};

const BASE_URI = '/o/ai-hub/chatbots';

describe('ChatbotService', () => {
	beforeEach(() => {
		mockFetch.mockReset();
	});

	describe('getChatbotDefinitions', () => {
		it('appends sort and page size params as a query string', async () => {
			mockFetch.mockResolvedValueOnce({
				json: () => Promise.resolve({items: []}),
				ok: true,
			});

			await getChatbotDefinitions({
				pageSize: '4',
				sort: 'dateModified:desc',
			});

			expect(mockFetch).toHaveBeenCalledWith(
				`${BASE_URI}?pageSize=4&sort=dateModified%3Adesc`,
				expect.objectContaining({method: 'GET'})
			);
		});

		it('does not append a trailing question mark for empty params', async () => {
			mockFetch.mockResolvedValueOnce({
				json: () => Promise.resolve({items: []}),
				ok: true,
			});

			await getChatbotDefinitions({});

			expect(mockFetch).toHaveBeenCalledWith(
				BASE_URI,
				expect.objectContaining({method: 'GET'})
			);
		});

		it('targets the base endpoint when no params are given', async () => {
			mockFetch.mockResolvedValueOnce({
				json: () => Promise.resolve({items: []}),
				ok: true,
			});

			await getChatbotDefinitions();

			expect(mockFetch).toHaveBeenCalledWith(
				BASE_URI,
				expect.objectContaining({method: 'GET'})
			);
		});

		it('throws when the response is not ok', async () => {
			mockFetch.mockResolvedValueOnce({ok: false});

			await expect(getChatbotDefinitions()).rejects.toThrow();
		});
	});

	describe('patchChatbotDefinition', () => {
		it('sends a PATCH request to the by-external-reference-code endpoint', async () => {
			mockFetch.mockResolvedValueOnce({
				json: () => Promise.resolve({}),
				ok: true,
			});

			await patchChatbotDefinition('CHATBOT-ERC', {
				description: 'Updated description',
			});

			expect(mockFetch).toHaveBeenCalledWith(
				`${BASE_URI}/by-external-reference-code/CHATBOT-ERC`,
				expect.objectContaining({
					body: JSON.stringify({
						description: 'Updated description',
					}),
					method: 'PATCH',
				})
			);
		});

		it('throws the error detail over the title when the response is not ok', async () => {
			mockFetch.mockResolvedValueOnce({
				json: () =>
					Promise.resolve({
						detail: 'The value is invalid',
						title: 'BAD_REQUEST',
					}),
				ok: false,
			});

			await expect(
				patchChatbotDefinition('CHATBOT-ERC', {})
			).rejects.toThrow('The value is invalid');
		});

		it('throws the error title when the response has no detail', async () => {
			mockFetch.mockResolvedValueOnce({
				json: () => Promise.resolve({title: 'Invalid chatbot'}),
				ok: false,
			});

			await expect(
				patchChatbotDefinition('CHATBOT-ERC', {})
			).rejects.toThrow('Invalid chatbot');
		});
	});
});
