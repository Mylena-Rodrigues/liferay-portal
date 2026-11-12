/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import loadAIHub from '../../src/main/resources/META-INF/resources/js/loadAIHub';

describe('loadAIHub', () => {
	it('returns null when the AI Hub frontend is not deployed', async () => {
		expect(await loadAIHub()).toBeNull();
	});

	it('returns the AI Hub when the AI Hub frontend is deployed', async () => {
		jest.doMock(
			'@liferay/ai-hub-js-components-web',
			() => ({
				__esModule: true,
				default: {name: 'ai-hub'},
			}),
			{virtual: true}
		);

		expect(await loadAIHub()).toEqual({name: 'ai-hub'});
	});
});
