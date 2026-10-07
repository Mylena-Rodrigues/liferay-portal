/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Plugin} from '@ckeditor/ckeditor5-core/dist/index.js';
import {ContextualBalloon} from '@ckeditor/ckeditor5-ui/dist/index.js';
import {loadModule} from 'frontend-js-web';

class WritingAssistantLoader extends Plugin {
	private writingAssistant: Plugin | null = null;

	static get requires() {
		return [ContextualBalloon];
	}

	destroy() {
		this.writingAssistant?.destroy();

		super.destroy();
	}

	async init() {
		try {
			const WritingAssistant = await loadModule(
				'{WritingAssistant} from @liferay/ai-hub-cell-js-components-web'
			);

			const writingAssistant = new WritingAssistant(this.editor);

			await writingAssistant.init();

			this.writingAssistant = writingAssistant;
		}
		catch (error) {
			if (process.env.NODE_ENV === 'development') {
				console.error(error);
			}
		}
	}
}

export default WritingAssistantLoader;
