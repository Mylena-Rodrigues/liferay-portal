/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayLabel from '@clayui/label';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import React, {useEffect, useState} from 'react';

import {EAgent} from '../../shared/types';
import useAgent from '../../shared/useAgent';
import {findMatchingAssets, generateForGap} from './triggers';
import {Gap, GapAnalysisContext, GapAnalysisResult, GapSeverity} from './types';

const SEVERITY_DISPLAY_TYPE: Record<
	GapSeverity,
	'danger' | 'secondary' | 'warning'
> = {
	high: 'danger',
	low: 'secondary',
	medium: 'warning',
};

/**
 * Story 94215: runs the gap analysis and renders a structured summary plus a
 * per-gap list. Each gap exposes Find matching assets, Generate content and
 * Dismiss. The agent never mutates project data — dismissal is chat-local.
 */
export default function GapInsightsBalloon({
	payload,
}: {
	payload: GapAnalysisContext;
}) {
	const [dismissed, setDismissed] = useState<string[]>([]);

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

	const gaps = (data?.gaps ?? []).filter(
		(gap: Gap) => !dismissed.includes(gap.id)
	);

	return (
		<div className="ai-assistant-chat__ai-assistant-message-balloon mb-2 p-2 rounded">
			<p className="font-weight-semi-bold">{data?.summary}</p>

			{gaps.map((gap) => (
				<div className="border-top py-2" key={gap.id}>
					<div className="align-items-center d-flex justify-content-between">
						<span className="font-weight-semi-bold">
							{gap.dimensions}
						</span>

						<ClayLabel
							displayType={SEVERITY_DISPLAY_TYPE[gap.severity]}
						>
							{gap.severity}
						</ClayLabel>
					</div>

					<div className="small text-secondary">
						{Liferay.Util.sub(
							Liferay.Language.get('x-of-x-assets'),
							`${gap.count}`,
							`${gap.threshold}`
						)}{' '}

						— {gap.reason}
					</div>

					<div className="mt-1">
						<ClayButton
							displayType="secondary"
							onClick={() => findMatchingAssets(gap)}
							size="sm"
						>
							{Liferay.Language.get(
								'find-matching-assets-in-cms'
							)}
						</ClayButton>

						<ClayButton
							className="ml-2"
							displayType="secondary"
							onClick={() => generateForGap(gap)}
							size="sm"
						>
							{Liferay.Language.get('generate-content-for-gaps')}
						</ClayButton>

						<ClayButton
							className="ml-2"
							displayType="unstyled"
							onClick={() =>
								setDismissed((previous) => [
									...previous,
									gap.id,
								])
							}
							size="sm"
						>
							{Liferay.Language.get('dismiss')}
						</ClayButton>
					</div>
				</div>
			))}
		</div>
	);
}
