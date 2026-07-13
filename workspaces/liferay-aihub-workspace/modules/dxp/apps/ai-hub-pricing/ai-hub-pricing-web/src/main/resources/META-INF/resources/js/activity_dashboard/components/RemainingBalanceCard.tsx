/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import React from 'react';

import {formatExpirationDate, isExpired} from '../utils/formatters';
import MetricCard from './MetricCard';

export default function RemainingBalanceCard({
	balance,
	expiresAt,
}: {
	balance: number;
	expiresAt: string | null;
}) {
	const languageId = Liferay.ThemeDisplay.getBCP47LanguageId();

	return (
		<MetricCard
			icon={<ClayIcon symbol="analytics" />}
			title={Liferay.Language.get('remaining-balance-liferay-tokens')}
			value={`${balance.toLocaleString(languageId)} LRT`}
		>
			{expiresAt &&
				(isExpired(expiresAt) ? (
					<span className="label label-danger">
						{Liferay.Language.get('expired')}
					</span>
				) : (
					<span className="label label-info">
						{Liferay.Language.get('expires-on-x').replace(
							'{0}',
							formatExpirationDate(expiresAt, languageId)
						)}
					</span>
				))}
		</MetricCard>
	);
}
