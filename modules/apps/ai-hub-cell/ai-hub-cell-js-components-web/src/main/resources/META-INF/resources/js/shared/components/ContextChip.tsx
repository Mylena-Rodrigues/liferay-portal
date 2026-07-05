/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import ClayLabel from '@clayui/label';
import React from 'react';

interface ContextChipProps {
	label: string;
	onRemove?: () => void;
	symbol?: string;
}

/**
 * Identifies the trigger scope carried into the Assistant (active folder,
 * "N Selected Items", active field, project matrix, ...). Shared by every
 * agent flow.
 */
export default function ContextChip({
	label,
	onRemove,
	symbol = 'thumbnails',
}: ContextChipProps) {
	return (
		<ClayLabel
			className="ai-assistant-chat__context-chip mb-2"
			closeButtonProps={
				onRemove
					? {
							'aria-label': Liferay.Language.get('remove'),
							'onClick': onRemove,
						}
					: undefined
			}
			displayType="info"
			style={{textTransform: 'none'}}
		>
			<span className="align-items-center d-inline-flex">
				<ClayIcon
					className="mr-1"
					spritemap={Liferay.Icons.spritemap}
					symbol={symbol}
				/>

				{label}
			</span>
		</ClayLabel>
	);
}
