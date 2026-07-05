/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {registerQuickAction} from '../../shared/quickActions';
import {generateImageForField, generateImageFromFolder} from './triggers';

registerQuickAction(Liferay.Language.get('generate-image'), (context) => {
	if (context.fieldId) {
		generateImageForField(
			context.fieldId as string,
			context.allowedFormats as string[] | undefined
		);
	}
	else {
		generateImageFromFolder(context.folderId as number | string);
	}
});
