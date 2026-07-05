/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLoadingIndicator from '@clayui/loading-indicator';
import {navigate} from 'frontend-js-web';
import React, {useEffect} from 'react';

import AIGeneratedBadge from '../../shared/components/AIGeneratedBadge';
import BatchSummary from '../../shared/components/BatchSummary';
import ResultList from '../../shared/components/ResultList';
import {AgentResultItem, EAgent} from '../../shared/types';
import useAgent from '../../shared/useAgent';
import {ContentGenerationContext, GeneratedContentResult} from './types';

/**
 * Story 94023: multiple content generation from a single typed prompt. Runs
 * async, shows progress, then a per-draft result list plus a batch summary for
 * partial failures. Each draft is a distinct angle produced server-side.
 */
export default function MultipleContentBalloon({
	payload,
}: {
	payload: ContentGenerationContext;
}) {
	const {data, regenerate, run, status} = useAgent<GeneratedContentResult>(
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

	function openDraft(item: AgentResultItem) {
		if (item.href) {
			navigate(item.href);
		}
	}

	return (
		<div className="ai-assistant-chat__ai-assistant-message-balloon mb-2 p-2 rounded">
			<ResultList
				items={drafts.filter((draft) => draft.status !== 'failed')}
				onOpen={openDraft}
				onRegenerate={regenerate}
			/>

			<BatchSummary items={drafts} onResume={regenerate} />

			<AIGeneratedBadge />
		</div>
	);
}
