/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import React, {useState} from 'react';

import AIGeneratedBadge from '../../shared/components/AIGeneratedBadge';
import BatchSummary from '../../shared/components/BatchSummary';
import {AgentResultItem, EAgent} from '../../shared/types';
import useAgent from '../../shared/useAgent';
import FormatPicker from './FormatPicker';
import {AdaptContext} from './types';

/**
 * Stories 93959 (single) and 93961 (bulk). Both share the format picker; the
 * bulk path additionally shows the batch summary with a Resume action. The path
 * is chosen from how many source files the context carries.
 */
export default function AdaptChannelsBalloon({
	payload,
}: {
	payload: AdaptContext;
}) {
	const [formats, setFormats] = useState<string[]>([]);

	const {data, regenerate, run, status} = useAgent<AgentResultItem[]>(
		EAgent.ADAPT_CHANNELS
	);

	const fileEntryIds = payload.fileEntryIds ?? [payload.fileEntryId];
	const bulk = fileEntryIds.length > 1;

	if (status === 'idle' || status === 'error') {
		return (
			<div className="ai-assistant-chat__ai-assistant-message-balloon mb-2 p-2 rounded">
				<p>{Liferay.Language.get('which-formats-do-you-need')}</p>

				<FormatPicker onChange={setFormats} value={formats} />

				<ClayButton
					className="mt-2"
					disabled={!formats.length}
					displayType="primary"
					onClick={() => run({fileEntryIds, formats})}
					size="sm"
				>
					{Liferay.Language.get('save-formats')}
				</ClayButton>
			</div>
		);
	}

	if (status === 'loading') {
		return (
			<div className="ai-assistant-chat__ai-assistant-message-balloon align-items-center d-flex mb-2 p-2 rounded">
				<ClayLoadingIndicator className="mr-2" />

				{Liferay.Language.get('generating')}
			</div>
		);
	}

	return (
		<div className="ai-assistant-chat__ai-assistant-message-balloon mb-2 p-2 rounded">
			{bulk ? (
				<BatchSummary items={data ?? []} onResume={regenerate} />
			) : (
				<p>{Liferay.Language.get('the-adaptations-have-been-saved')}</p>
			)}

			<AIGeneratedBadge />
		</div>
	);
}
