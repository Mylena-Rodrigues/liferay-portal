/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import ClayLabel from '@clayui/label';
import React from 'react';

export interface ResultCardLabel {
	displayType?: 'danger' | 'info' | 'secondary' | 'success' | 'warning';
	text: string;
}

interface AgentResultCardProps {
	href?: string;
	labels?: ResultCardLabel[];
	symbol?: string;
	title: string;
}

/**
 * Shared result card listing something an agent produced or found: a leading
 * icon, a link to the item and any number of status/dimension labels. Reused
 * for generated content drafts (94019/94023) and matching assets (94219).
 */
export default function AgentResultCard({
	href,
	labels = [],
	symbol = 'document-text',
	title,
}: AgentResultCardProps) {
	return (
		<div className="ai-assistant-chat__agent-result-card align-items-center border d-flex mb-2 p-2 rounded">
			<ClayIcon spritemap={Liferay.Icons.spritemap} symbol={symbol} />

			<div className="ml-2">
				{href ? <a href={href}>{title}</a> : <span>{title}</span>}

				{!!labels.length && (
					<div className="mt-1">
						{labels.map((label, index) => (
							<ClayLabel
								className="mr-1"
								displayType={label.displayType ?? 'info'}
								key={index}
							>
								{label.text}
							</ClayLabel>
						))}
					</div>
				)}
			</div>
		</div>
	);
}
