/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import ClayLabel from '@clayui/label';
import React from 'react';

/**
 * Marks an output as produced by AI so final users can identify generated or
 * adapted assets, as required by every agent epic.
 */
export default function AIGeneratedBadge() {
	return (
		<ClayLabel
			className="ai-assistant-chat__ai-generated-badge"
			displayType="info"
			style={{textTransform: 'none'}}
		>
			<span className="align-items-center d-inline-flex">
				<ClayIcon
					className="mr-1"
					spritemap={Liferay.Icons.spritemap}
					symbol="stars"
				/>

				{Liferay.Language.get('ai-generated')}
			</span>
		</ClayLabel>
	);
}
