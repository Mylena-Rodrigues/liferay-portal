/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {registerQuickAction} from '../../shared/quickActions';
import {generateContent, generateInEditor} from './triggers';

registerQuickAction(Liferay.Language.get('generate-content'), (context) => {
	if (context.assetId) {
		generateInEditor('content', context);
	}
	else {
		generateContent({
			requiresContentType: true,
			spaceId: context.spaceId as number | string | undefined,
		});
	}
});

registerQuickAction(Liferay.Language.get('generate-title'), (context) => {
	generateInEditor('title', context);
});
