/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import {openToast} from 'frontend-js-components-web';
import React, {useEffect, useState} from 'react';

import {EAgent} from '../../shared/types';
import useAgent from '../../shared/useAgent';
import {generateForGap, updateMatrixCell} from './triggers';
import {Gap} from './types';

interface MatchingAsset {
	cellId: string;
	id: string;
	reason: string;
	relevance: number;
	space: string;
	title: string;
	type: string;
	url: string;
}

/**
 * Story 94219: searches the broader CMS for assets that could fill a gap and
 * lets the user attach them to the project matrix. Dedicated list because the
 * per-asset shape (relevance, why-it-matches, Attach) is specific to this flow.
 */
export default function FindAssetsBalloon({payload}: {payload: {gap: Gap}}) {
	const [attached, setAttached] = useState<string[]>([]);

	const {data, run, status} = useAgent<MatchingAsset[]>(
		EAgent.GAP_FIND_ASSETS
	);

	useEffect(() => {
		run({gap: payload.gap});
	}, [payload, run]);

	function attach(asset: MatchingAsset) {
		setAttached((previous) => [...previous, asset.id]);

		updateMatrixCell({cellId: asset.cellId, delta: 1});

		openToast({
			message: Liferay.Language.get('asset-attached-to-project'),
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

	const assets = data ?? [];

	if (!assets.length) {
		return (
			<div className="ai-assistant-chat__ai-assistant-message-balloon mb-2 p-2 rounded">
				<p>{Liferay.Language.get('no-matching-assets-were-found')}</p>

				<ClayButton
					displayType="primary"
					onClick={() => generateForGap(payload.gap)}
					size="sm"
				>
					{Liferay.Language.get('generate-content-for-gaps')}
				</ClayButton>
			</div>
		);
	}

	return (
		<div className="ai-assistant-chat__ai-assistant-message-balloon mb-2 p-2 rounded">
			{assets.map((asset) => (
				<div
					className="align-items-center border-top d-flex justify-content-between py-2"
					key={asset.id}
				>
					<div className="mr-2 text-truncate">
						<a href={asset.url}>{asset.title}</a>

						<div className="small text-secondary text-truncate">
							{asset.type} · {asset.space} — {asset.reason}
						</div>
					</div>

					<ClayButton
						disabled={attached.includes(asset.id)}
						displayType="secondary"
						onClick={() => attach(asset)}
						size="sm"
					>
						{Liferay.Language.get('attach-to-project')}
					</ClayButton>
				</div>
			))}
		</div>
	);
}
