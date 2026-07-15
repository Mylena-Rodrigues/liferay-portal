/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import ClayCard from '@clayui/card';
import {ClayCheckbox} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import React, {useState} from 'react';

import {saveGeneratedImages} from '../services/saveGeneratedImages';

import '../chat.scss';
import injectImageIntoFileUploadField from '../utils/injectImageIntoFileUploadField';
import renderAIAssistantMessageMarkdown from '../utils/renderAIAssistantMessageMarkdown';
import SpaceSelectionModalContent from './SpaceSelectionModalContent';

export interface SaveProps {
	groupId?: number | string;
	objectEntryFolderExternalReferenceCode?: string;
	selectFileButton?: string;
}

interface ImageMessageBalloonProps {
	images: string[];
	message?: string;
	onRegenerate?: () => void;
	saveProps?: SaveProps;
}

const ImageMessageBalloon: React.FC<ImageMessageBalloonProps> = ({
	images,
	message,
	onRegenerate,
	saveProps = {},
}) => {
	const {groupId, objectEntryFolderExternalReferenceCode, selectFileButton} =
		saveProps;

	const multiple = images.length > 1;

	const [selectedIndexes, setSelectedIndexes] = useState<Set<number>>(
		() => new Set(images.map((_, index) => index))
	);
	const [saving, setSaving] = useState<boolean>(false);
	const [selectingSpace, setSelectingSpace] = useState<boolean>(false);

	function toggleSelected(index: number) {
		setSelectedIndexes((previousSelectedIndexes) => {
			const nextSelectedIndexes = new Set(previousSelectedIndexes);

			if (nextSelectedIndexes.has(index)) {
				nextSelectedIndexes.delete(index);
			}
			else {
				nextSelectedIndexes.add(index);
			}

			return nextSelectedIndexes;
		});
	}

	const selectedImages = images.filter((_, index) =>
		selectedIndexes.has(index)
	);

	async function saveToGroup(targetGroupId: number | string) {
		setSaving(true);

		try {
			await saveGeneratedImages(selectedImages, {
				groupId: targetGroupId,
				objectEntryFolderExternalReferenceCode,
			});
		}
		finally {
			setSaving(false);
		}
	}

	function handleSave() {
		if (!selectedImages.length) {
			return;
		}

		if (
			selectFileButton &&
			injectImageIntoFileUploadField(selectFileButton, selectedImages[0])
		) {
			return;
		}

		if (Number(groupId) > 0) {
			saveToGroup(groupId as number | string);

			return;
		}

		setSelectingSpace(true);
	}

	return (
		<div className="ai-assistant-chat__ai-assistant-message-balloon ai-assistant-chat__image-message-balloon">
			{message && (
				<div className="ai-assistant-chat__image-message-balloon-header">
					<div className="ai-assistant-chat__image-message-balloon-header-icon">
						<ClayIcon
							spritemap={Liferay.Icons.spritemap}
							symbol="stars"
						/>
					</div>

					<div
						className="ai-assistant-chat__image-message-balloon-header-text"
						dangerouslySetInnerHTML={{
							__html: renderAIAssistantMessageMarkdown(message),
						}}
					/>
				</div>
			)}

			<ul className="ai-assistant-chat__image-message-balloon-images">
				{images.map((image, index) => (
					<li
						className="ai-assistant-chat__image-message-balloon-item"
						key={index}
					>
						<ClayCard displayType="image" selectable={multiple}>
							{multiple ? (
								<ClayCheckbox
									checked={selectedIndexes.has(index)}
									disabled={saving}
									onChange={() => toggleSelected(index)}
								>
									<ClayCard.AspectRatio className="card-item-first card-item-last">
										<img
											alt={Liferay.Language.get(
												'generated-image'
											)}
											className="aspect-ratio-item-center-middle aspect-ratio-item-fluid"
											src={image}
										/>
									</ClayCard.AspectRatio>
								</ClayCheckbox>
							) : (
								<ClayCard.AspectRatio className="card-item-first card-item-last">
									<img
										alt={Liferay.Language.get(
											'generated-image'
										)}
										className="aspect-ratio-item-center-middle aspect-ratio-item-fluid"
										src={image}
									/>
								</ClayCard.AspectRatio>
							)}
						</ClayCard>
					</li>
				))}
			</ul>

			<div className="ai-assistant-chat__image-message-balloon-actions">
				{onRegenerate && (
					<ClayButtonWithIcon
						aria-label={Liferay.Language.get('regenerate')}
						disabled={saving}
						displayType="secondary"
						onClick={onRegenerate}
						outline
						spritemap={Liferay.Icons.spritemap}
						symbol="reload"
						title={Liferay.Language.get('regenerate')}
					/>
				)}

				<ClayButton
					disabled={
						saving || selectingSpace || !selectedImages.length
					}
					displayType="primary"
					onClick={handleSave}
				>
					{saving ? (
						<>
							<ClayLoadingIndicator size="sm" />

							{Liferay.Language.get('saving')}
						</>
					) : selectedImages.length > 1 ? (
						Liferay.Language.get('save-images')
					) : (
						Liferay.Language.get('save-image')
					)}
				</ClayButton>
			</div>

			{selectingSpace && (
				<SpaceSelectionModalContent
					onSelectSpace={(chosenGroupId) => {
						setSelectingSpace(false);

						if (chosenGroupId) {
							saveToGroup(chosenGroupId);
						}
					}}
				/>
			)}
		</div>
	);
};

export default ImageMessageBalloon;
