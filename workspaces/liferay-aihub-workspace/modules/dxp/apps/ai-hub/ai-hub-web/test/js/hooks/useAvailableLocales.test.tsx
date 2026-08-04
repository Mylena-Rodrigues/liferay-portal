/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {renderHook} from '@testing-library/react';

import useAvailableLocales from '../../../src/main/resources/META-INF/resources/js/hooks/useAvailableLocales';

function setup({
	available,
	defaultLanguageId,
}: {
	available: Record<string, string>;
	defaultLanguageId: string;
}) {
	(global as any).Liferay = {
		Language: {available},
		ThemeDisplay: {
			getDefaultLanguageId: () => defaultLanguageId,
		},
	};

	return renderHook(() => useAvailableLocales());
}

describe('useAvailableLocales', () => {
	it('builds the icon from the locale id', () => {
		const {result} = setup({
			available: {pt_BR: 'português (Brasil)'},
			defaultLanguageId: 'pt_BR',
		});

		expect(result.current.availableLocales[0].icon).toBe('pt-br');
	});

	it('falls back to the first available locale when the default language is not available', () => {
		const {result} = setup({
			available: {ca_ES: 'Català', en_US: 'English'},
			defaultLanguageId: 'pt_BR',
		});

		expect(result.current.defaultLocale).toBe('ca_ES');
	});

	it('keeps the other locales in their original order after the default', () => {
		const {result} = setup({
			available: {
				ca_ES: 'Català',
				en_US: 'English',
				nl_NL: 'Nederlands',
				pt_BR: 'português (Brasil)',
			},
			defaultLanguageId: 'nl_NL',
		});

		expect(
			result.current.availableLocales.map(({localeId}) => localeId)
		).toEqual(['nl_NL', 'ca_ES', 'en_US', 'pt_BR']);
	});

	it('returns the default language as the default locale', () => {
		const {result} = setup({
			available: {
				ca_ES: 'Català',
				en_US: 'English',
				pt_BR: 'português (Brasil)',
			},
			defaultLanguageId: 'pt_BR',
		});

		expect(result.current.defaultLocale).toBe('pt_BR');
	});

	it('sorts the default language first when it is not first in the available map', () => {
		const {result} = setup({
			available: {
				ca_ES: 'Català',
				en_US: 'English',
				pt_BR: 'português (Brasil)',
			},
			defaultLanguageId: 'pt_BR',
		});

		expect(result.current.availableLocales[0].localeId).toBe('pt_BR');
	});
});
