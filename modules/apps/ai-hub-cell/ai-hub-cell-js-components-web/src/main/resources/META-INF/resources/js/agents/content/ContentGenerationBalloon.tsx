/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import React, {useState} from 'react';

import AIGeneratedBadge from '../../shared/components/AIGeneratedBadge';
import ChipPicker from '../../shared/components/ChipPicker';
import DraftLinkList from '../../shared/components/DraftLinkList';
import {EAgent} from '../../shared/types';
import useAgent from '../../shared/useAgent';
import {ContentGenerationContext, GeneratedContentResult} from './types';

/**
 * Story 94019: single content generation. Resolves the destination space
 * (infer-or-ask), collects the brief, then generates a draft and links to it.
 */
export default function ContentGenerationBalloon({
	payload,
}: {
	payload: ContentGenerationContext;
}) {
	const [brief, setBrief] = useState(payload.brief ?? '');
	const [spaceId, setSpaceId] = useState(payload.spaceId);

	const {data, regenerate, run, status} = useAgent<GeneratedContentResult>(
		EAgent.GENERATE_CONTENT
	);

	const spaceOptions = payload.spaceOptions ?? [];
	const mustAskSpace = !spaceId && spaceOptions.length > 1;

	if (mustAskSpace) {
		return (
			<div className="ai-assistant-chat__ai-assistant-message-balloon mb-2 p-2 rounded">
				<p>
					{Liferay.Language.get('which-space-should-i-save-this-in')}
				</p>

				<ChipPicker
					onChange={(value) => setSpaceId(value[0])}
					options={spaceOptions.map((space) => ({
						label: space.name,
						value: space.id,
					}))}
					value={spaceId ? [`${spaceId}`] : []}
				/>
			</div>
		);
	}

	if (!brief) {
		return (
			<div className="ai-assistant-chat__ai-assistant-message-balloon mb-2 p-2 rounded">
				<p>
					{Liferay.Util.sub(
						Liferay.Language.get(
							'what-do-you-want-the-x-to-be-about'
						),
						payload.structureName ?? Liferay.Language.get('content')
					)}
				</p>

				<textarea
					className="form-control mb-2"
					onChange={(event) => setBrief(event.target.value)}
					rows={2}
					value={brief}
				/>

				<ClayButton
					disabled={!brief}
					displayType="primary"
					onClick={() =>
						run({
							brief,
							spaceId,
							structureId: payload.structureId,
						})
					}
					size="sm"
				>
					{Liferay.Language.get('generate')}
				</ClayButton>
			</div>
		);
	}

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

			<div className="mt-2">
				<AIGeneratedBadge />

				<ClayButton
					className="ml-2"
					displayType="secondary"
					onClick={regenerate}
					size="sm"
				>
					{Liferay.Language.get('regenerate')}
				</ClayButton>
			</div>
		</div>
	);
}
