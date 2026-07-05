/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLoadingIndicator from '@clayui/loading-indicator';
import React, {useEffect} from 'react';

import AIGeneratedBadge from '../../shared/components/AIGeneratedBadge';
import DraftLinkList from '../../shared/components/DraftLinkList';
import {AgentResultItem, EAgent} from '../../shared/types';
import useAgent from '../../shared/useAgent';
import {updateMatrixCell} from './triggers';
import {Gap} from './types';

interface GapGenerationResult {
	cellId: string;
	drafts: AgentResultItem[];
}

/**
 * Story 94221: generates content that fills a gap by composing with the content
 * agent at the hook layer (reuses useAgent + the shared draft list, never the
 * content balloons) and auto-attaches each new draft to the matrix cell.
 */
export default function GenerateForGapBalloon({
	payload,
}: {
	payload: {gap: Gap};
}) {
	const {data, run, status} = useAgent<GapGenerationResult>(
		EAgent.GENERATE_CONTENT
	);

	useEffect(() => {
		run({gap: payload.gap});
	}, [payload, run]);

	useEffect(() => {
		if (status === 'ready' && data) {
			updateMatrixCell({cellId: data.cellId, delta: data.drafts.length});
		}
	}, [data, status]);

	if (status === 'loading' || status === 'idle') {
		return (
			<div className="ai-assistant-chat__ai-assistant-message-balloon align-items-center d-flex mb-2 p-2 rounded">
				<ClayLoadingIndicator className="mr-2" />

				{Liferay.Language.get('generating')}
			</div>
		);
	}

	return (
		<div className="ai-assistant-chat__ai-assistant-message-balloon mb-2 p-2 rounded">
			<p>{Liferay.Language.get('here-is-your-new-draft')}</p>

			<DraftLinkList items={data?.drafts ?? []} />

			<AIGeneratedBadge />
		</div>
	);
}
