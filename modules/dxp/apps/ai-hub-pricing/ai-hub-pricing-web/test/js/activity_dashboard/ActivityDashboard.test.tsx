/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom';

import ActivityDashboard from '../../../src/main/resources/META-INF/resources/js/activity_dashboard/ActivityDashboard';

(global as any).Liferay = {
	Language: {
		get: (key: string) => key,
	},
	ThemeDisplay: {
		getBCP47LanguageId: () => 'en-US',
	},
};

function renderDashboard() {
	return render(
		<ActivityDashboard
			agentsCount={18}
			chatbotsCount={4}
			expiresAt="2027-05-21T00:00:00Z"
			totalLRT={2500}
		/>
	);
}

describe('ActivityDashboard', () => {
	it('renders the page heading', () => {
		renderDashboard();

		expect(
			screen.getByRole('heading', {level: 1, name: 'activity'})
		).toBeInTheDocument();
	});

	it('renders the agents and chatbots cards with their counts', () => {
		renderDashboard();

		['agents', 'chatbots'].forEach((name) => {
			expect(
				screen.getByRole('heading', {level: 2, name})
			).toBeInTheDocument();
		});

		expect(screen.getByText('18')).toBeInTheDocument();

		expect(screen.getByText('4')).toBeInTheDocument();
	});

	it('renders the remaining balance card', () => {
		renderDashboard();

		expect(
			screen.getByRole('heading', {
				level: 2,
				name: 'remaining-balance-liferay-tokens',
			})
		).toBeInTheDocument();
	});
});
