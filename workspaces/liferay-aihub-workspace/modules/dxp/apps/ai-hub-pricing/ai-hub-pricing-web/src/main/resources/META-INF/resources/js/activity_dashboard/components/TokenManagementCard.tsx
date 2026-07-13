/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import React from 'react';

import './TokenManagementCard.scss';

export default function TokenManagementCard() {
	return (
		<article className="ai-hub-token-management-card">
			<h2 className="ai-hub-token-management-card-title">
				{Liferay.Language.get('token-management')}
			</h2>

			<div className="ai-hub-token-management-card-actions">
				<ClayButton displayType="primary" size="sm">
					{Liferay.Language.get('buy-liferay-tokens')}
				</ClayButton>

				<ClayButton displayType="secondary" size="sm">
					{Liferay.Language.get('see-purchase-history')}
				</ClayButton>
			</div>
		</article>
	);
}
