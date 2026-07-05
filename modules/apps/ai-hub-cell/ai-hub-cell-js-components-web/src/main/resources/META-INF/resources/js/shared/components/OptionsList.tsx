/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import React from 'react';

export interface Option {
	disabled?: boolean;
	label: string;
	onClick: () => void;
}

interface OptionsListProps {
	options: Option[];
	title?: string;
}

/**
 * Reusable "What would you like to do next?" style list: an optional title over
 * a wrap of action buttons. Used by the gap insights flow and available to any
 * agent that offers follow-up choices.
 */
export default function OptionsList({options, title}: OptionsListProps) {
	return (
		<div className="ai-assistant-chat__options-list">
			{title && <p className="font-weight-semi-bold mb-1">{title}</p>}

			<div className="d-flex flex-wrap">
				{options.map((option) => (
					<ClayButton
						className="mb-1 mr-1"
						disabled={option.disabled}
						displayType="secondary"
						key={option.label}
						onClick={option.onClick}
						size="sm"
					>
						{option.label}
					</ClayButton>
				))}
			</div>
		</div>
	);
}
