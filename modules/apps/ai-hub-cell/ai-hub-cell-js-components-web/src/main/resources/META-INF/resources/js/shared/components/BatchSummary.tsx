/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import React from 'react';

import {AgentResultItem} from '../types';

interface BatchSummaryProps {
	items: AgentResultItem[];
	onResume?: () => void;
}

function countByStatus(items: AgentResultItem[], status: string): number {
	return items.filter((item) => item.status === status).length;
}

/**
 * Success / failed / skipped roll-up for async batch runs (bulk adapt, multiple
 * content, generate for gaps), plus the Resume action that re-runs only the
 * failed items. Failed and skipped items list their reasons.
 */
export default function BatchSummary({items, onResume}: BatchSummaryProps) {
	const failed = items.filter((item) => item.status === 'failed');
	const skipped = items.filter((item) => item.status === 'skipped');
	const hasFailures = !!failed.length;

	return (
		<div className="ai-assistant-chat__batch-summary">
			<div className="d-flex flex-wrap small">
				<span className="mr-3 text-success">
					{Liferay.Util.sub(
						Liferay.Language.get('x-succeeded'),
						`${countByStatus(items, 'success')}`
					)}
				</span>

				<span className="mr-3 text-danger">
					{Liferay.Util.sub(
						Liferay.Language.get('x-failed'),
						`${failed.length}`
					)}
				</span>

				<span className="text-secondary">
					{Liferay.Util.sub(
						Liferay.Language.get('x-skipped'),
						`${skipped.length}`
					)}
				</span>
			</div>

			{(hasFailures || !!skipped.length) && (
				<ul className="list-unstyled mb-0 mt-2 small">
					{[...failed, ...skipped].map((item) => (
						<li className="text-secondary" key={item.id}>
							{item.title}

							{item.reason ? ` — ${item.reason}` : ''}
						</li>
					))}
				</ul>
			)}

			{hasFailures && onResume && (
				<ClayButton
					className="mt-2"
					displayType="secondary"
					onClick={onResume}
					size="sm"
				>
					{Liferay.Language.get('resume')}
				</ClayButton>
			)}
		</div>
	);
}
