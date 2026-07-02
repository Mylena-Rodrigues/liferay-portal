/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import AIFeatures from '../../../src/main/resources/META-INF/resources/js/ai_features/AIFeatures';

const mockGetAIFeatures = jest.fn();
const mockPatchAIFeatures = jest.fn();

jest.mock(
	'../../../src/main/resources/META-INF/resources/js/ai_features/services/AIFeaturesService',
	() => ({
		getAIFeatures: (...args: any[]) => mockGetAIFeatures(...args),
		patchAIFeatures: (...args: any[]) => mockPatchAIFeatures(...args),
	})
);

describe('AIFeatures', () => {
	beforeEach(() => {
		jest.clearAllMocks();

		mockGetAIFeatures.mockResolvedValue({enable: true});
	});

	it('reflects the enabled state in the status indicator', async () => {
		mockGetAIFeatures.mockResolvedValue({enable: true});

		render(<AIFeatures />);

		expect(await screen.findByText('enabled')).toBeInTheDocument();
	});

	it('reflects the disabled state in the status indicator', async () => {
		mockGetAIFeatures.mockResolvedValue({enable: false});

		render(<AIFeatures />);

		expect(await screen.findByText('disabled')).toBeInTheDocument();
	});

	it('shows the last change details when provided', async () => {
		mockGetAIFeatures.mockResolvedValue({
			enable: false,
			lastModifiedBy: 'Jane Doe',
			lastModifiedDate: '2026-06-22T10:00:00.000Z',
		});

		render(<AIFeatures />);

		expect(
			await screen.findByText('Jane Doe', {exact: false})
		).toBeInTheDocument();
	});

	it('requires a reason to disable and sends the selected reason and comment', async () => {
		mockPatchAIFeatures.mockResolvedValue({
			enable: false,
			lastModifiedBy: 'Jane Doe',
			lastModifiedDate: '2026-06-22T10:00:00.000Z',
		});

		render(<AIFeatures />);

		await screen.findByText('enabled');

		await userEvent.click(screen.getByLabelText('enable-ai-features'));

		expect(
			await screen.findByText('disable-ai-features')
		).toBeInTheDocument();

		const disableButton = screen.getByRole('button', {name: 'disable'});

		expect(disableButton).toBeDisabled();

		await userEvent.selectOptions(
			screen.getByLabelText('reason'),
			'incidentOrDataLeakResponse'
		);

		await userEvent.type(screen.getByLabelText('comment'), 'Investigating');

		expect(disableButton).toBeEnabled();

		await userEvent.click(disableButton);

		expect(await screen.findByText('disabled')).toBeInTheDocument();
		expect(mockPatchAIFeatures).toHaveBeenCalledWith(
			false,
			'incidentOrDataLeakResponse',
			'Investigating'
		);
	});

	it('enables without requiring a reason', async () => {
		mockGetAIFeatures.mockResolvedValue({enable: false});
		mockPatchAIFeatures.mockResolvedValue({enable: true});

		render(<AIFeatures />);

		await screen.findByText('disabled');

		await userEvent.click(screen.getByLabelText('enable-ai-features'));

		await userEvent.click(
			await screen.findByRole('button', {name: 'enable'})
		);

		expect(await screen.findByText('enabled')).toBeInTheDocument();
		expect(mockPatchAIFeatures).toHaveBeenCalledWith(true, '', '');
	});

	it('cancels the confirmation without calling the service', async () => {
		render(<AIFeatures />);

		await screen.findByText('enabled');

		await userEvent.click(screen.getByLabelText('enable-ai-features'));

		expect(await screen.findByLabelText('reason')).toBeInTheDocument();

		await userEvent.click(screen.getByRole('button', {name: 'cancel'}));

		await waitFor(() =>
			expect(screen.queryByLabelText('reason')).not.toBeInTheDocument()
		);

		expect(mockPatchAIFeatures).not.toHaveBeenCalled();
	});
});
