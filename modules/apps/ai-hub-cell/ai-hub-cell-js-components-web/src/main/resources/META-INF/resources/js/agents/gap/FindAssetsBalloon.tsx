/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import {openToast} from 'frontend-js-components-web';
import React, {useEffect, useState} from 'react';

import AgentResultCard from '../../shared/components/AgentResultCard';
import OptionsList from '../../shared/components/OptionsList';
import {EAgent} from '../../shared/types';
import useAgent from '../../shared/useAgent';
import {generateForGaps, updateMatrixCell} from './triggers';
import {GapAnalysisContext, MatchingAssetsResult} from './types';

/**
 * Story 94219: scans the broader CMS for assets that fill the project's gaps,
 * lists them with the shared result card, and offers to attach them all at
 * once. On confirmation each asset updates its matrix cell.
 */
export default function FindAssetsBalloon({
	payload,
}: {
	payload: GapAnalysisContext;
}) {
	const [added, setAdded] = useState(false);

	const {data, run, status} = useAgent<MatchingAssetsResult>(
		EAgent.GAP_FIND_ASSETS
	);

	useEffect(() => {
		run({
			projectId: payload.projectId,
			selectedCells: payload.selectedCells,
		});
	}, [payload, run]);

	function addAll(assets: MatchingAssetsResult['assets']) {
		assets.forEach((asset) =>
			updateMatrixCell({cellId: asset.cellId, delta: 1})
		);

		setAdded(true);

		openToast({
			message: Liferay.Language.get('assets-attached-to-project'),
			type: 'success',
		});
	}

	if (status === 'loading' || status === 'idle') {
		return (
			<div className="ai-assistant-chat__ai-assistant-message-balloon align-items-center d-flex mb-2 p-2 rounded">
				<ClayLoadingIndicator className="mr-2" />

				{Liferay.Language.get('searching')}
			</div>
		);
	}

	const assets = data?.assets ?? [];

	if (!assets.length) {
		return (
			<div className="ai-assistant-chat__ai-assistant-message-balloon mb-2 p-2 rounded">
				<p>{Liferay.Language.get('no-matching-assets-were-found')}</p>

				<OptionsList
					options={[
						{
							label: Liferay.Language.get(
								'generate-content-for-gaps'
							),
							onClick: () => generateForGaps(payload),
						},
					]}
				/>
			</div>
		);
	}

	return (
		<div className="ai-assistant-chat__ai-assistant-message-balloon mb-2 p-2 rounded">
			<p>
				{Liferay.Util.sub(
					Liferay.Language.get(
						'ive-scanned-your-library-and-found-x-assets-that-match-some-gaps-of-your-project'
					),
					`${assets.length}`
				)}
			</p>

			{assets.map((asset) => (
				<AgentResultCard
					href={asset.url}
					key={asset.id}
					labels={[
						{
							displayType: asset.statusApproved
								? 'success'
								: 'info',
							text: asset.status,
						},
						{displayType: 'secondary', text: asset.dimensions},
					]}
					title={asset.title}
				/>
			))}

			{!added && (
				<>
					<p className="mt-2">
						{Liferay.Language.get(
							'would-you-like-me-to-add-all-suggested-assets'
						)}
					</p>

					<div className="d-flex">
						<ClayButton
							className="mr-2"
							displayType="primary"
							onClick={() => addAll(assets)}
							size="sm"
						>
							{Liferay.Language.get('yes')}
						</ClayButton>

						<ClayButton
							displayType="secondary"
							onClick={() => setAdded(true)}
							size="sm"
						>
							{Liferay.Language.get('no')}
						</ClayButton>
					</div>
				</>
			)}
		</div>
	);
}
