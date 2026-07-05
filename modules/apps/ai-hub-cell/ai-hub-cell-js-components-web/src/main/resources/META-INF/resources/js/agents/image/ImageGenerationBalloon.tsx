/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {ClayCheckbox} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import {openToast} from 'frontend-js-components-web';
import React, {useEffect, useState} from 'react';

import {cancelUserInput, requestUserInput} from '../../shared/agentInput';
import {EAgent} from '../../shared/types';
import useAgent from '../../shared/useAgent';
import {saveGeneratedImages} from './triggers';
import {GeneratedImage, ImageGenerationContext} from './types';

/**
 * Stories 93953 (dataset / Quick Action) and 93954 (File field sparkle): a
 * single conversational flow whose only difference is the save destination
 * carried in the context. The Assistant asks the user to describe the image;
 * the prompt (including any desired style) is typed in the main chat input.
 */
export default function ImageGenerationBalloon({
	payload,
}: {
	payload: ImageGenerationContext;
}) {
	const [prompt, setPrompt] = useState(payload.prompt ?? '');
	const [selected, setSelected] = useState<Record<string, boolean>>({});

	const {data, regenerate, run, status} = useAgent<GeneratedImage[]>(
		EAgent.GENERATE_IMAGE
	);

	useEffect(() => {
		if (prompt) {
			return;
		}

		let active = true;

		requestUserInput().then((text) => {
			if (active) {
				setPrompt(text);
			}
		});

		return () => {
			active = false;

			cancelUserInput();
		};
	}, [prompt]);

	useEffect(() => {
		if (prompt && status === 'idle') {
			run({destination: payload.destination, prompt});
		}
	}, [payload.destination, prompt, run, status]);

	useEffect(() => {
		if (data) {
			setSelected(
				Object.fromEntries(data.map((image) => [image.id, true]))
			);
		}
	}, [data]);

	if (!prompt) {
		return (
			<div className="ai-assistant-chat__ai-assistant-message-balloon mb-2 p-2 rounded">
				{Liferay.Language.get(
					'sure-describe-the-image-you-want-me-to-generate'
				)}
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

	const images = data ?? [];
	const multiple = images.length > 1;
	const selectedImages = images.filter((image) => selected[image.id]);

	return (
		<div className="ai-assistant-chat__ai-assistant-message-balloon mb-2 p-2 rounded">
			<p>
				{Liferay.Language.get(
					'your-image-is-ready-you-can-regenerate-if-needed-when-it-looks-good-to-you-save-the-image'
				)}
			</p>

			<div className="d-flex flex-wrap">
				{images.map((image) => (
					<div
						className="ai-assistant-chat__generated-image-wrapper mb-2 mr-2"
						key={image.id}
					>
						{multiple && (
							<ClayCheckbox
								checked={Boolean(selected[image.id])}
								onChange={() =>
									setSelected((previous) => ({
										...previous,
										[image.id]: !previous[image.id],
									}))
								}
							/>
						)}

						<img
							alt={image.alt ?? prompt}
							className="ai-assistant-chat__generated-image rounded"
							src={image.url}
							style={{maxWidth: 160}}
						/>
					</div>
				))}
			</div>

			<div className="align-items-center d-flex justify-content-end">
				<ClayButton
					aria-label={Liferay.Language.get('regenerate')}
					borderless
					className="mr-2"
					displayType="secondary"
					onClick={regenerate}
				>
					<ClayIcon
						spritemap={Liferay.Icons.spritemap}
						symbol="reload"
					/>
				</ClayButton>

				<ClayButton
					disabled={!selectedImages.length}
					displayType="primary"
					onClick={() => {
						saveGeneratedImages(payload, selectedImages);

						openToast({
							message: multiple
								? Liferay.Language.get('images-saved')
								: Liferay.Language.get('image-saved'),
							type: 'success',
						});
					}}
					size="sm"
				>
					{multiple
						? Liferay.Language.get('save-images')
						: Liferay.Language.get('save-image')}
				</ClayButton>
			</div>
		</div>
	);
}
