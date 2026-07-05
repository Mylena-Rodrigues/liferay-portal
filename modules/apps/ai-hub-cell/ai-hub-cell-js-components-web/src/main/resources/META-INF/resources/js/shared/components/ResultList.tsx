/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import React from 'react';

import {AgentResultItem} from '../types';

interface ResultListProps {
	items: AgentResultItem[];
	onDismiss?: (item: AgentResultItem) => void;
	onOpen?: (item: AgentResultItem) => void;
	onRegenerate?: (item: AgentResultItem) => void;
}

/**
 * Renders a list of agent results (generated drafts, matching assets, ...) with
 * the standard per-item actions. Actions only render when a handler is given,
 * so each flow opts into Open / Regenerate / Dismiss as needed.
 */
export default function ResultList({
	items,
	onDismiss,
	onOpen,
	onRegenerate,
}: ResultListProps) {
	return (
		<ul className="ai-assistant-chat__result-list list-unstyled m-0">
			{items.map((item) => (
				<li
					className="ai-assistant-chat__result-item align-items-center d-flex justify-content-between py-1"
					key={item.id}
				>
					<div className="mr-2 text-truncate">
						{item.href ? (
							<a href={item.href}>{item.title}</a>
						) : (
							<span>{item.title}</span>
						)}

						{item.subtitle && (
							<div className="small text-secondary text-truncate">
								{item.subtitle}
							</div>
						)}
					</div>

					<div className="align-items-center d-flex flex-shrink-0">
						{onOpen && (
							<ClayButton
								aria-label={Liferay.Language.get('open')}
								borderless
								displayType="secondary"
								onClick={() => onOpen(item)}
								size="sm"
							>
								<ClayIcon
									spritemap={Liferay.Icons.spritemap}
									symbol="shortcut"
								/>
							</ClayButton>
						)}

						{onRegenerate && (
							<ClayButton
								aria-label={Liferay.Language.get('regenerate')}
								borderless
								displayType="secondary"
								onClick={() => onRegenerate(item)}
								size="sm"
							>
								<ClayIcon
									spritemap={Liferay.Icons.spritemap}
									symbol="reload"
								/>
							</ClayButton>
						)}

						{onDismiss && (
							<ClayButton
								aria-label={Liferay.Language.get('dismiss')}
								borderless
								displayType="secondary"
								onClick={() => onDismiss(item)}
								size="sm"
							>
								<ClayIcon
									spritemap={Liferay.Icons.spritemap}
									symbol="times"
								/>
							</ClayButton>
						)}
					</div>
				</li>
			))}
		</ul>
	);
}
