/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLayout from '@clayui/layout';
import React from 'react';

import AgentsCard from './components/AgentsCard';
import ChatbotsCard from './components/ChatbotsCard';
import RemainingBalanceCard from './components/RemainingBalanceCard';

import './ActivityDashboard.scss';

export default function ActivityDashboard({
	agentsCount,
	chatbotsCount,
	expiresAt,
	totalLRT,
}: {
	agentsCount: number;
	chatbotsCount: number;
	expiresAt: string;
	totalLRT: number;
}) {
	return (
		<ClayLayout.ContainerFluid className="ai-hub-activity-dashboard">
			<h1 className="ai-hub-activity-dashboard-title">
				{Liferay.Language.get('activity')}
			</h1>

			<ClayLayout.Row>
				<ClayLayout.Col className="mb-4" md={6}>
					<AgentsCard value={agentsCount} />
				</ClayLayout.Col>

				<ClayLayout.Col className="mb-4" md={6}>
					<ChatbotsCard value={chatbotsCount} />
				</ClayLayout.Col>
			</ClayLayout.Row>

			<ClayLayout.Row>
				<ClayLayout.Col className="mb-4" md={12}>
					<RemainingBalanceCard
						balance={totalLRT}
						expiresAt={expiresAt}
					/>
				</ClayLayout.Col>
			</ClayLayout.Row>
		</ClayLayout.ContainerFluid>
	);
}
