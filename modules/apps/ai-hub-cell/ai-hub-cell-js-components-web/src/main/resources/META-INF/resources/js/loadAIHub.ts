/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import type {AIHub} from './types';

const AI_HUB_MODULE = '@liferay/ai-hub-js-components-web';

export default function loadAIHub(): Promise<AIHub | null> {
	return import(AI_HUB_MODULE).then(
		({default: aiHub}: {default: AIHub}) => aiHub,
		() => null
	);
}
