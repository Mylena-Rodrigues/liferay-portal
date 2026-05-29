/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayLayout from '@clayui/layout';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import React from 'react';

import AgentsCard from './components/AgentsCard';
import ChatbotsCard from './components/ChatbotsCard';
import RemainingBalanceCard from './components/RemainingBalanceCard';
import TokenManagementCard from './components/TokenManagementCard';
import useActivityMetrics from './hooks/useActivityMetrics';

import './ActivityDashboard.scss';

export default function ActivityDashboard({
	accountEntryExternalReferenceCode,
}: {
	accountEntryExternalReferenceCode?: string;
}) {
	const {data, error, loading} = useActivityMetrics(
		accountEntryExternalReferenceCode
	);

	if (loading) {
		return <ClayLoadingIndicator displayType="secondary" size="md" />;
	}

	if (error || !data) {
		return (
			<ClayAlert displayType="danger">
				{Liferay.Language.get('an-unexpected-error-occurred')}
			</ClayAlert>
		);
	}

	return (
		<ClayLayout.ContainerFluid className="ai-hub-activity-dashboard">
			<h1 className="ai-hub-activity-dashboard-title">
				{Liferay.Language.get('activity')}
			</h1>

			<ClayLayout.Row>
				<ClayLayout.Col className="mb-4" md={6}>
					<AgentsCard value={data.agentsCount} />
				</ClayLayout.Col>

				<ClayLayout.Col className="mb-4" md={6}>
					<ChatbotsCard value={data.chatbotsCount} />
				</ClayLayout.Col>
			</ClayLayout.Row>

			<ClayLayout.Row>
				<ClayLayout.Col className="mb-4" md={6}>
					<RemainingBalanceCard
						balance={data.totalLRT}
						expiresAt={data.expiresAt}
					/>
				</ClayLayout.Col>

				<ClayLayout.Col className="mb-4" md={6}>
					<TokenManagementCard />
				</ClayLayout.Col>
			</ClayLayout.Row>
		</ClayLayout.ContainerFluid>
	);
}
