/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {AvailableLocale} from 'dynamic-data-mapping-form-field-type';
import {useMemo} from 'react';

export default function useAvailableLocales(): {
	availableLocales: AvailableLocale[];
	defaultLocale: Liferay.Language.Locale;
} {
	return useMemo(() => {
		const defaultLanguageId = Liferay.ThemeDisplay.getDefaultLanguageId();

		const availableLocales = Object.entries(Liferay.Language.available)
			.map(([localeId, displayName]) => ({
				displayName,
				icon: localeId.replace(/_/g, '-').toLowerCase(),
				localeId: localeId as Liferay.Language.Locale,
			}))
			.sort((a, b) => {
				if (a.localeId === defaultLanguageId) {
					return -1;
				}

				if (b.localeId === defaultLanguageId) {
					return 1;
				}

				return 0;
			});

		return {
			availableLocales,
			defaultLocale:
				availableLocales.find(
					({localeId}) => localeId === defaultLanguageId
				)?.localeId ||
				availableLocales[0]?.localeId ||
				'en_US',
		};
	}, []);
}
