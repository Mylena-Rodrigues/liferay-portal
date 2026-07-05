/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import AgentResultCard from '../../shared/components/AgentResultCard';
import {AgentResultItem} from '../../shared/types';

/**
 * Result card for a generated content draft. A thin wrapper over the shared
 * AgentResultCard so content drafts (94019/94023) and matching assets (94219)
 * render with the same component.
 */
export default function ContentDraftCard({draft}: {draft: AgentResultItem}) {
	return (
		<AgentResultCard
			href={draft.href}
			labels={[
				{
					displayType: 'info',
					text: draft.subtitle ?? Liferay.Language.get('draft'),
				},
			]}
			title={draft.title}
		/>
	);
}
