/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {AvailableLocale} from 'dynamic-data-mapping-form-field-type';
import {useMemo} from 'react';

export default function useAvailableLocales(): AvailableLocale[] {
	return useMemo(() => {
		const defaultLanguageId = Liferay.ThemeDisplay.getDefaultLanguageId();

		return Object.entries(Liferay.Language.available)
			.map(([localeId, displayName]) => ({
				displayName,
				icon: localeId.replace(/_/g, '-').toLowerCase(),
				localeId: localeId as Liferay.Language.Locale,
			}))
			.sort(({localeId}) => (localeId === defaultLanguageId ? -1 : 1));
	}, []);
}
