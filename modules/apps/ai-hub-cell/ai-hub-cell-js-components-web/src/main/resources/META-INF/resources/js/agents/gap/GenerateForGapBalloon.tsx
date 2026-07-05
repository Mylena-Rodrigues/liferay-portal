/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLoadingIndicator from '@clayui/loading-indicator';
import React, {useEffect} from 'react';

import AgentResultCard from '../../shared/components/AgentResultCard';
import {AgentResultItem, EAgent} from '../../shared/types';
import useAgent from '../../shared/useAgent';
import {updateMatrixCell} from './triggers';
import {GapAnalysisContext} from './types';

interface GapGenerationResult {
	cells?: string[];
	drafts: AgentResultItem[];
}

/**
 * Story 94221: generates content that fills the project's gaps by composing with
 * the content agent at the hook layer (reuses useAgent + the shared result card,
 * never the content balloons) and attaches each new draft to its matrix cell.
 */
export default function GenerateForGapBalloon({
	payload,
}: {
	payload: GapAnalysisContext;
}) {
	const {data, run, status} = useAgent<GapGenerationResult>(
		EAgent.GENERATE_CONTENT
	);

	useEffect(() => {
		run({
			projectId: payload.projectId,
			selectedCells: payload.selectedCells,
		});
	}, [payload, run]);

	useEffect(() => {
		if (status === 'ready' && data?.cells) {
			data.cells.forEach((cellId) =>
				updateMatrixCell({cellId, delta: 1})
			);
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
			<p>
				{Liferay.Language.get('done-your-drafts-have-been-generated')}
			</p>

			{(data?.drafts ?? []).map((draft) => (
				<AgentResultCard
					href={draft.href}
					key={draft.id}
					labels={[
						{
							displayType: 'info',
							text:
								draft.subtitle ?? Liferay.Language.get('draft'),
						},
					]}
					title={draft.title}
				/>
			))}
		</div>
	);
}
