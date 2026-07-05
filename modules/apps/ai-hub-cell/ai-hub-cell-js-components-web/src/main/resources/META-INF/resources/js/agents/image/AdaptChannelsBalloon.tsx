/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import React, {useEffect, useState} from 'react';

import BatchSummary from '../../shared/components/BatchSummary';
import DraftLinkList from '../../shared/components/DraftLinkList';
import {ChipOption, EAgent} from '../../shared/types';
import useAgent from '../../shared/useAgent';
import FormatPicker from './FormatPicker';
import {getChannelFormats} from './services/getChannelFormats';
import {AdaptContext, AdaptResult} from './types';

/**
 * Stories 93959 (single) and 93961 (bulk). Both share the format picker, whose
 * presets are loaded from the Liferay environment; the bulk path shows the
 * batch summary with a Resume action while the single path links the created
 * folder.
 */
export default function AdaptChannelsBalloon({
	payload,
}: {
	payload: AdaptContext;
}) {
	const [formats, setFormats] = useState<string[]>([]);
	const [options, setOptions] = useState<ChipOption[]>([]);

	const {data, regenerate, run, status} = useAgent<AdaptResult>(
		EAgent.ADAPT_CHANNELS
	);

	useEffect(() => {
		let active = true;

		getChannelFormats().then((channelFormats) => {
			if (active) {
				setOptions(channelFormats);
			}
		});

		return () => {
			active = false;
		};
	}, []);

	const fileEntryIds = payload.fileEntryIds ?? [payload.fileEntryId];
	const bulk = fileEntryIds.length > 1;

	if (status === 'idle' || status === 'error') {
		return (
			<div className="ai-assistant-chat__ai-assistant-message-balloon mb-2 p-2 rounded">
				<p>{Liferay.Language.get('which-formats-do-you-need')}</p>

				<FormatPicker
					onChange={setFormats}
					options={options}
					value={formats}
				/>

				<div className="d-flex justify-content-end">
					<ClayButton
						disabled={!formats.length}
						displayType="primary"
						onClick={() => run({fileEntryIds, formats})}
						size="sm"
					>
						{Liferay.Language.get('save-formats')}
					</ClayButton>
				</div>
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

	if (bulk) {
		return (
			<div className="ai-assistant-chat__ai-assistant-message-balloon mb-2 p-2 rounded">
				<BatchSummary items={data?.items ?? []} onResume={regenerate} />
			</div>
		);
	}

	return (
		<div className="ai-assistant-chat__ai-assistant-message-balloon mb-2 p-2 rounded">
			<p>
				{Liferay.Language.get(
					'done-your-formats-have-been-generated-you-can-review-and-edit-them-in-this-folder'
				)}
			</p>

			<DraftLinkList items={data?.folder ? [data.folder] : []} />
		</div>
	);
}
