/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLoadingIndicator from '@clayui/loading-indicator';
import React, {useEffect} from 'react';

import OptionsList from '../../shared/components/OptionsList';
import {EAgent} from '../../shared/types';
import useAgent from '../../shared/useAgent';
import {
	createTasksForGaps,
	findMatchingAssets,
	generateForGaps,
} from './triggers';
import {GapAnalysisContext, GapAnalysisResult} from './types';

/**
 * Story 94215: runs the coverage audit and reports the critical gaps as a
 * bulleted summary, then offers the follow-up actions through the reusable
 * OptionsList. The agent never mutates project data.
 */
export default function GapInsightsBalloon({
	payload,
}: {
	payload: GapAnalysisContext;
}) {
	const {data, run, status} = useAgent<GapAnalysisResult>(
		EAgent.GAP_ANALYSIS
	);

	useEffect(() => {
		run({
			projectId: payload.projectId,
			selectedCells: payload.selectedCells,
		});
	}, [payload, run]);

	if (status === 'loading' || status === 'idle') {
		return (
			<div className="ai-assistant-chat__ai-assistant-message-balloon align-items-center d-flex mb-2 p-2 rounded">
				<ClayLoadingIndicator className="mr-2" />

				{Liferay.Language.get('analyzing-coverage')}
			</div>
		);
	}

	return (
		<div className="ai-assistant-chat__ai-assistant-message-balloon mb-2 p-2 rounded">
			<p>
				{Liferay.Util.sub(
					Liferay.Language.get(
						'ive-completed-the-audit-for-the-x-content-coverage-matrix'
					),
					payload.projectName ?? Liferay.Language.get('project')
				)}
			</p>

			<p className="font-weight-semi-bold mb-1">
				{Liferay.Language.get('critical-gaps-identified')}
			</p>

			<ul className="mb-3 pl-3">
				{(data?.gaps ?? []).map((gap) => (
					<li key={gap.id}>{gap.reason}</li>
				))}
			</ul>

			<OptionsList
				options={[
					{
						label: Liferay.Language.get(
							'find-matching-assets-in-cms'
						),
						onClick: () => findMatchingAssets(payload),
					},
					{
						label: Liferay.Language.get('create-tasks-for-gaps'),
						onClick: () => createTasksForGaps(payload),
					},
					{
						label: Liferay.Language.get(
							'generate-content-for-gaps'
						),
						onClick: () => generateForGaps(payload),
					},
				]}
				title={Liferay.Language.get('what-would-you-like-to-do-next')}
			/>
		</div>
	);
}
