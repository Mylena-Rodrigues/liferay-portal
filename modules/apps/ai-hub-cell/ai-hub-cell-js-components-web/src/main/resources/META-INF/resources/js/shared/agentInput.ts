/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

/**
 * Lets an agent flow collect the user's next chat message (e.g. the image
 * prompt) from the main input instead of an embedded textarea. The flow calls
 * requestUserInput() and the host routes the next submitted message to it.
 */
let pendingResolver: ((text: string) => void) | null = null;

export function requestUserInput(): Promise<string> {
	return new Promise((resolve) => {
		pendingResolver = resolve;
	});
}

export function fulfillUserInput(text: string): boolean {
	if (!pendingResolver) {
		return false;
	}

	const resolve = pendingResolver;

	pendingResolver = null;

	resolve(text);

	return true;
}

export function cancelUserInput() {
	pendingResolver = null;
}
