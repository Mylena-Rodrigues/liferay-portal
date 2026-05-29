/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom';

import TokenManagementCard from '../../../../src/main/resources/META-INF/resources/js/activity_dashboard/components/TokenManagementCard';

(global as any).Liferay = {
	Language: {
		get: (key: string) => key,
	},
};

describe('TokenManagementCard', () => {
	it('renders the Buy Liferay Tokens and See Purchase History buttons', () => {
		render(<TokenManagementCard />);

		expect(
			screen.getByRole('button', {name: 'buy-liferay-tokens'})
		).toBeInTheDocument();

		expect(
			screen.getByRole('button', {name: 'see-purchase-history'})
		).toBeInTheDocument();
	});

	it('renders the Token Management heading', () => {
		render(<TokenManagementCard />);

		expect(
			screen.getByRole('heading', {level: 2, name: 'token-management'})
		).toBeInTheDocument();
	});
});
