/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	formatExpirationDate,
	formatMillisecondsAsSeconds,
	isExpired,
} from '../../../../src/main/resources/META-INF/resources/js/activity_dashboard/utils/formatters';

describe('formatExpirationDate', () => {
	it('does not append an English ordinal suffix', () => {
		expect(formatExpirationDate('2027-05-01T00:00:00Z', 'en-US')).toBe(
			'May 1, 2027'
		);
	});

	it('formats the UTC date in the given locale', () => {
		expect(formatExpirationDate('2027-05-21T00:00:00Z', 'en-US')).toBe(
			'May 21, 2027'
		);
	});

	it('respects the provided locale', () => {
		expect(formatExpirationDate('2027-05-21T00:00:00Z', 'pt-BR')).toContain(
			'maio'
		);
	});
});

describe('formatMillisecondsAsSeconds', () => {
	it('converts whole seconds', () => {
		expect(formatMillisecondsAsSeconds(2000)).toBe('2.0s');
	});

	it('formats fractional milliseconds to one decimal', () => {
		expect(formatMillisecondsAsSeconds(2600)).toBe('2.6s');
	});

	it('formats zero as 0.0s', () => {
		expect(formatMillisecondsAsSeconds(0)).toBe('0.0s');
	});
});

describe('isExpired', () => {
	const now = new Date('2026-05-29T00:00:00Z');

	it('returns false when the expiration date is exactly now', () => {
		expect(isExpired('2026-05-29T00:00:00Z', now)).toBe(false);
	});

	it('returns false when the expiration date is in the future', () => {
		expect(isExpired('2030-01-01T00:00:00Z', now)).toBe(false);
	});

	it('returns true when the expiration date is in the past', () => {
		expect(isExpired('2020-01-01T00:00:00Z', now)).toBe(true);
	});
});
