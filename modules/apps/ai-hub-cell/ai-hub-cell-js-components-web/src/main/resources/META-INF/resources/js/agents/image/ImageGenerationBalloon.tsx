/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import {openToast} from 'frontend-js-components-web';
import React, {useEffect, useState} from 'react';

import AIGeneratedBadge from '../../shared/components/AIGeneratedBadge';
import ChipPicker from '../../shared/components/ChipPicker';
import {EAgent} from '../../shared/types';
import useAgent from '../../shared/useAgent';
import {saveGeneratedImages} from './triggers';
import {
	GeneratedImage,
	IMAGE_STYLE_OPTIONS,
	ImageGenerationContext,
} from './types';

/**
 * Stories 93953 (dataset / Quick Action) and 93954 (File field sparkle): a
 * single conversational flow whose only difference is the save destination
 * carried in the context. Ask for prompt, then style, then generate, then let
 * the user Save / Regenerate / Cancel.
 */
export default function ImageGenerationBalloon({
	payload,
}: {
	payload: ImageGenerationContext;
}) {
	const [draft, setDraft] = useState(payload.prompt ?? '');
	const [prompt, setPrompt] = useState(payload.prompt ?? '');
	const [style, setStyle] = useState(payload.style ?? '');

	const {data, regenerate, run, status} = useAgent<GeneratedImage[]>(
		EAgent.GENERATE_IMAGE
	);

	useEffect(() => {
		if (prompt && style && status === 'idle') {
			run({destination: payload.destination, prompt, style});
		}
	}, [payload.destination, prompt, run, status, style]);

	if (!prompt) {
		return (
			<div className="ai-assistant-chat__ai-assistant-message-balloon mb-2 p-2 rounded">
				<p>
					{Liferay.Language.get(
						'sure-describe-the-image-you-want-me-to-generate'
					)}
				</p>

				<textarea
					className="form-control mb-2"
					onChange={(event) => setDraft(event.target.value)}
					rows={2}
					value={draft}
				/>

				<ClayButton
					disabled={!draft.trim()}
					displayType="primary"
					onClick={() => setPrompt(draft)}
					size="sm"
				>
					{Liferay.Language.get('continue')}
				</ClayButton>
			</div>
		);
	}

	if (!style) {
		return (
			<div className="ai-assistant-chat__ai-assistant-message-balloon mb-2 p-2 rounded">
				<p>{Liferay.Language.get('choose-a-style')}</p>

				<ChipPicker
					onChange={(value) => setStyle(value[0])}
					options={IMAGE_STYLE_OPTIONS}
					value={style ? [style] : []}
				/>
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
			<div className="d-flex flex-wrap">
				{(data ?? []).map((image) => (
					<img
						alt={image.alt ?? prompt}
						className="ai-assistant-chat__generated-image mb-2 mr-2 rounded"
						key={image.id}
						src={image.url}
						style={{maxWidth: 160}}
					/>
				))}
			</div>

			<AIGeneratedBadge />

			<div className="mt-2">
				<ClayButton
					displayType="primary"
					onClick={() => {
						saveGeneratedImages(payload, data ?? []);

						openToast({
							message: Liferay.Language.get('images-saved'),
							type: 'success',
						});
					}}
					size="sm"
				>
					{Liferay.Language.get('save')}
				</ClayButton>

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
