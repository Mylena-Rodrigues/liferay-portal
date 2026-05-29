/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom';

import ActivityDashboard from '../../../src/main/resources/META-INF/resources/js/activity_dashboard/ActivityDashboard';
import {ActivityMetrics} from '../../../src/main/resources/META-INF/resources/js/activity_dashboard/types/ActivityMetrics';

const mockUseActivityMetrics = jest.fn();

jest.mock(
	'../../../src/main/resources/META-INF/resources/js/activity_dashboard/hooks/useActivityMetrics',
	() => ({
		__esModule: true,
		default: (...args: any[]) => mockUseActivityMetrics(...args),
	})
);

(global as any).Liferay = {
	Language: {
		get: (key: string) => key,
	},
	ThemeDisplay: {
		getBCP47LanguageId: () => 'en-US',
	},
};

const SAMPLE_METRICS: ActivityMetrics = {
	agentsCount: 18,
	averageResponseTimeMs: 2600,
	chatbotsCount: 4,
	expiresAt: '2027-05-21T00:00:00Z',
	totalInteractions: 1245,
	totalLRT: 2500,
};

function mockResult(overrides: Record<string, unknown> = {}) {
	mockUseActivityMetrics.mockReturnValue({
		data: null,
		error: null,
		loading: false,
		refetch: jest.fn(),
		...overrides,
	});
}

describe('ActivityDashboard', () => {
	beforeEach(() => {
		mockUseActivityMetrics.mockReset();
	});

	it('forwards the account external reference code to the metrics hook', () => {
		mockResult({data: SAMPLE_METRICS});

		render(
			<ActivityDashboard accountEntryExternalReferenceCode="ACCOUNT_ERC" />
		);

		expect(mockUseActivityMetrics).toHaveBeenCalledWith('ACCOUNT_ERC');
	});

	it('renders a loading indicator while metrics are loading', () => {
		mockResult({loading: true});

		const {container} = render(<ActivityDashboard />);

		expect(
			container.querySelector('.loading-animation')
		).toBeInTheDocument();

		expect(
			screen.queryByRole('heading', {level: 1, name: 'activity'})
		).not.toBeInTheDocument();
	});

	it('renders an error alert when no metrics are available', () => {
		mockResult({data: null});

		render(<ActivityDashboard />);

		expect(
			screen.getByText('an-unexpected-error-occurred')
		).toBeInTheDocument();
	});

	it('renders an error alert when the metrics request fails', () => {
		mockResult({error: new Error('Boom')});

		render(<ActivityDashboard />);

		expect(
			screen.getByText('an-unexpected-error-occurred')
		).toBeInTheDocument();

		expect(
			screen.queryByRole('heading', {level: 1, name: 'activity'})
		).not.toBeInTheDocument();
	});

	it('renders the page heading and every metric card once data is loaded', () => {
		mockResult({data: SAMPLE_METRICS});

		const {container} = render(<ActivityDashboard />);

		expect(
			screen.getByRole('heading', {level: 1, name: 'activity'})
		).toBeInTheDocument();

		['agents', 'chatbots'].forEach((name) => {
			expect(
				screen.getByRole('heading', {level: 2, name})
			).toBeInTheDocument();
		});

		expect(
			screen.getByRole('heading', {
				level: 2,
				name: 'remaining-balance-liferay-tokens',
			})
		).toBeInTheDocument();

		expect(screen.getByText('18')).toBeInTheDocument();

		expect(
			container.querySelector('.loading-animation')
		).not.toBeInTheDocument();

		expect(container.querySelector('.alert')).not.toBeInTheDocument();
	});
});
