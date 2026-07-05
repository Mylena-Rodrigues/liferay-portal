/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fireInvokeAgent} from '../../shared/agentEvents';
import {EAgent} from '../../shared/types';
import {AdaptContext, GeneratedImage, ImageGenerationContext} from './types';

/**
 * Firing helpers the CMS trigger surfaces call. The server-side contributions
 * (FDSCreationMenu, FDSItemsActions, FDSBulkActions, the File field sparkle)
 * only need to invoke one of these — they never reach into the chat internals.
 */

export function generateImageFromFolder(folderId: number | string) {
	fireInvokeAgent({
		agent: EAgent.GENERATE_IMAGE,
		context: {destination: 'folder', folderId} as Record<string, unknown>,
		label: Liferay.Language.get('generate-image'),
	});
}

export function generateImageForField(
	fieldId: string,
	allowedFormats?: string[]
) {
	fireInvokeAgent({
		agent: EAgent.GENERATE_IMAGE,
		context: {
			allowedFormats,
			destination: 'field',
			fieldId,
		} as Record<string, unknown>,
		label: Liferay.Language.get('generate-image'),
	});
}

export function adaptForChannels(context: AdaptContext) {
	fireInvokeAgent({
		agent: EAgent.ADAPT_CHANNELS,
		context: context as Record<string, unknown>,
		label: Liferay.Language.get('adapt-for-channels'),
	});
}

/**
 * Persists the chosen images to the destination the context describes (active
 * folder or the active File field). Wired to the DAM/content REST layer; the
 * outputs are tagged AI-generated server-side.
 */
export function saveGeneratedImages(
	context: ImageGenerationContext,
	images: GeneratedImage[]
) {
	Liferay.fire('cms:aiAssistant:imageSaveRequested', {context, images});
}
