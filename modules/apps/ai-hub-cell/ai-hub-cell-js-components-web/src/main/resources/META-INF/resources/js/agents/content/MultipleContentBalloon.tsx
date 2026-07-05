/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLoadingIndicator from '@clayui/loading-indicator';
import React, {useEffect} from 'react';

import {EAgent} from '../../shared/types';
import useAgent from '../../shared/useAgent';
import ContentDraftCard from './ContentDraftCard';
import {ContentGenerationContext, GeneratedContentResult} from './types';

/**
 * Story 94023: multiple content generation from a single typed prompt. Runs
 * async, shows progress, then lists each generated draft as a card with a link.
 * Any failed items are listed separately without blocking the rest.
 */
export default function MultipleContentBalloon({
	payload,
}: {
	payload: ContentGenerationContext;
}) {
	const {data, run, status} = useAgent<GeneratedContentResult>(
		EAgent.GENERATE_CONTENT
	);

	useEffect(() => {
		run({
			brief: payload.brief,
			count: payload.count,
			spaceId: payload.spaceId,
			structureId: payload.structureId,
		});
	}, [payload, run]);

	if (status === 'loading' || status === 'idle') {
		return (
			<div className="ai-assistant-chat__ai-assistant-message-balloon align-items-center d-flex mb-2 p-2 rounded">
				<ClayLoadingIndicator className="mr-2" />

				{Liferay.Language.get('generating')}
			</div>
		);
	}

	const drafts = data?.drafts ?? [];
	const generated = drafts.filter((draft) => draft.status !== 'failed');
	const failed = drafts.filter((draft) => draft.status === 'failed');

	return (
		<div className="ai-assistant-chat__ai-assistant-message-balloon mb-2 p-2 rounded">
			<p>
				{Liferay.Language.get('done-your-drafts-have-been-generated')}
			</p>

			{generated.map((draft) => (
				<ContentDraftCard draft={draft} key={draft.id} />
			))}

			{!!failed.length && (
				<ul className="list-unstyled mb-0 mt-2 small text-secondary">
					{failed.map((draft) => (
						<li key={draft.id}>
							{draft.title}

							{draft.reason ? ` — ${draft.reason}` : ''}
						</li>
					))}
				</ul>
			)}
		</div>
	);
}
