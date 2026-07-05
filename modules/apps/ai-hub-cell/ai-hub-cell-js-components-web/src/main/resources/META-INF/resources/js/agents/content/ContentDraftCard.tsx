/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import ClayLabel from '@clayui/label';
import React from 'react';

import {AgentResultItem} from '../../shared/types';

/**
 * Result card for a generated content draft: content-type icon, a link to the
 * draft and its status label. Used for both single (94019) and multiple (94023)
 * generation results.
 */
export default function ContentDraftCard({draft}: {draft: AgentResultItem}) {
	return (
		<div className="ai-assistant-chat__content-draft-card align-items-center border d-flex mb-2 p-2 rounded">
			<ClayIcon
				spritemap={Liferay.Icons.spritemap}
				symbol="document-text"
			/>

			<div className="ml-2">
				{draft.href ? (
					<a href={draft.href}>{draft.title}</a>
				) : (
					<span>{draft.title}</span>
				)}

				<div>
					<ClayLabel displayType="info">
						{draft.subtitle ?? Liferay.Language.get('draft')}
					</ClayLabel>
				</div>
			</div>
		</div>
	);
}
