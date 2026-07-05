/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClaySelectWithOption} from '@clayui/form';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import React, {useEffect, useState} from 'react';

import {cancelUserInput, requestUserInput} from '../../shared/agentInput';
import {EAgent} from '../../shared/types';
import useAgent from '../../shared/useAgent';
import ContentDraftCard from './ContentDraftCard';
import {getContentTypes} from './services/getContentTypes';
import {
	ContentGenerationContext,
	ContentType,
	GeneratedContentResult,
} from './types';

/**
 * Story 94019: single content generation. When the active instruction requires
 * it, the user first picks a content type; then the brief is typed in the main
 * chat input and the resulting draft is shown as a card.
 */
export default function ContentGenerationBalloon({
	payload,
}: {
	payload: ContentGenerationContext;
}) {
	const [brief, setBrief] = useState(payload.brief ?? '');
	const [contentTypes, setContentTypes] = useState<ContentType[]>([]);
	const [structureId, setStructureId] = useState(payload.structureId);
	const [structureName, setStructureName] = useState(
		payload.structureName ?? ''
	);

	const {data, run, status} = useAgent<GeneratedContentResult>(
		EAgent.GENERATE_CONTENT
	);

	const needsType = Boolean(payload.requiresContentType) && !structureId;

	useEffect(() => {
		if (!needsType) {
			return;
		}

		let active = true;

		getContentTypes().then((types) => {
			if (active) {
				setContentTypes(types);
			}
		});

		return () => {
			active = false;
		};
	}, [needsType]);

	useEffect(() => {
		if (needsType || brief) {
			return;
		}

		let active = true;

		requestUserInput().then((text) => {
			if (active) {
				setBrief(text);
			}
		});

		return () => {
			active = false;

			cancelUserInput();
		};
	}, [brief, needsType]);

	useEffect(() => {
		if (!needsType && brief && status === 'idle') {
			run({brief, spaceId: payload.spaceId, structureId});
		}
	}, [brief, needsType, payload.spaceId, run, status, structureId]);

	if (needsType) {
		return (
			<div className="ai-assistant-chat__ai-assistant-message-balloon mb-2 p-2 rounded">
				<p>{Liferay.Language.get('what-type-of-content')}</p>

				<ClaySelectWithOption
					aria-label={Liferay.Language.get('select-content-type')}
					onChange={(event) => {
						const selected = contentTypes.find(
							(contentType) =>
								`${contentType.id}` === event.target.value
						);

						if (selected) {
							setStructureId(selected.id);
							setStructureName(selected.name);
						}
					}}
					options={[
						{
							label: Liferay.Language.get('select-content-type'),
							value: '',
						},
						...contentTypes.map((contentType) => ({
							label: contentType.name,
							value: `${contentType.id}`,
						})),
					]}
				/>
			</div>
		);
	}

	if (!brief) {
		return (
			<div className="ai-assistant-chat__ai-assistant-message-balloon mb-2 p-2 rounded">
				{Liferay.Util.sub(
					Liferay.Language.get('what-do-you-want-the-x-to-be-about'),
					structureName || Liferay.Language.get('content')
				)}
			</div>
		);
	}

	if (status === 'loading' || status === 'idle') {
		return (
			<div className="ai-assistant-chat__ai-assistant-message-balloon align-items-center d-flex mb-2 p-2 rounded">
				<ClayLoadingIndicator className="mr-2" />

				{Liferay.Util.sub(
					Liferay.Language.get('generating-x'),
					structureName || Liferay.Language.get('content')
				)}
			</div>
		);
	}

	return (
		<div className="ai-assistant-chat__ai-assistant-message-balloon mb-2 p-2 rounded">
			<p>
				{Liferay.Language.get(
					'done-your-draft-has-been-generated-you-can-review-and-edit-it-here'
				)}
			</p>

			{(data?.drafts ?? []).map((draft) => (
				<ContentDraftCard draft={draft} key={draft.id} />
			))}
		</div>
	);
}
