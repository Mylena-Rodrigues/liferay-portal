/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom';

import RemainingBalanceCard from '../../../../src/main/resources/META-INF/resources/js/activity_dashboard/components/RemainingBalanceCard';

(global as any).Liferay = {
	Language: {
		get: (key: string) => (key === 'expires-on-x' ? 'Expires on {0}' : key),
	},
	ThemeDisplay: {
		getBCP47LanguageId: () => 'en-US',
	},
};

describe('RemainingBalanceCard', () => {
	it('does not render a progress bar', () => {
		const {container} = render(
			<RemainingBalanceCard balance={2500} expiresAt={null} />
		);

		expect(container.querySelector('.progress')).not.toBeInTheDocument();
	});

	it('omits the expiration row when there is no expiration date', () => {
		render(<RemainingBalanceCard balance={2500} expiresAt={null} />);

		expect(screen.queryByText(/Expires on/)).not.toBeInTheDocument();
		expect(screen.queryByText('expired')).not.toBeInTheDocument();
	});

	it('renders the balance and the heading', () => {
		render(
			<RemainingBalanceCard
				balance={2500}
				expiresAt="2099-05-21T00:00:00Z"
			/>
		);

		expect(
			screen.getByRole('heading', {
				level: 2,
				name: 'remaining-balance-liferay-tokens',
			})
		).toBeInTheDocument();
		expect(screen.getByText('2,500 LRT')).toBeInTheDocument();
	});

	it('renders the expired indicator when the expiration date has passed', () => {
		render(
			<RemainingBalanceCard
				balance={2500}
				expiresAt="2020-01-01T00:00:00Z"
			/>
		);

		expect(screen.getByText('expired')).toBeInTheDocument();
		expect(screen.queryByText(/Expires on/)).not.toBeInTheDocument();
	});

	it('renders the formatted expiration date when not expired', () => {
		render(
			<RemainingBalanceCard
				balance={2500}
				expiresAt="2099-05-21T00:00:00Z"
			/>
		);

		expect(screen.getByText('Expires on May 21, 2099')).toBeInTheDocument();
	});
});
