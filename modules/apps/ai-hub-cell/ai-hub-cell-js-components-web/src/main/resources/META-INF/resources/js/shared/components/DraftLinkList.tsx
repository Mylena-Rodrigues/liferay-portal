/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import {AgentResultItem} from '../types';

interface DraftLinkListProps {
	items: AgentResultItem[];
}

/**
 * Bulleted list of clickable links to the drafts an agent produced. Used by the
 * single, multiple and gap generation flows for the success response.
 */
export default function DraftLinkList({items}: DraftLinkListProps) {
	return (
		<ul className="ai-assistant-chat__draft-link-list mb-0 pl-3">
			{items.map((item) => (
				<li key={item.id}>
					{item.href ? (
						<a href={item.href}>{item.title}</a>
					) : (
						<span>{item.title}</span>
					)}
				</li>
			))}
		</ul>
	);
}
