/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLoadingIndicator from '@clayui/loading-indicator';
import React, {useEffect} from 'react';

import {EAgent} from '../../shared/types';
import useAgent from '../../shared/useAgent';
import {applyToContentEditor} from './triggers';
import {ContentEditorContext, ContentEditorResult} from './types';

/**
 * Story 94020: in-context Generate Title / Generate Content run against the
 * active entry. The result is written back to the editor field through the
 * apply event; the editor mount owns the actual field update.
 */
export default function ContentEditorBalloon({
	payload,
}: {
	payload: ContentEditorContext;
}) {
	const {data, run, status} = useAgent<ContentEditorResult>(
		EAgent.GENERATE_CONTENT
	);

	useEffect(() => {
		if (status === 'idle') {
			run({
				action: payload.action,
				assetId: payload.assetId,
				structureId: payload.structureId,
			});
		}
	}, [payload, run, status]);

	useEffect(() => {
		if (status === 'ready' && data) {
			applyToContentEditor({field: payload.action, value: data.value});
		}
	}, [data, payload.action, status]);

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
			{payload.action === 'title'
				? Liferay.Language.get(
						'done-the-title-has-been-generated-based-on-the-content'
					)
				: Liferay.Language.get('done-the-content-has-been-generated')}
		</div>
	);
}
