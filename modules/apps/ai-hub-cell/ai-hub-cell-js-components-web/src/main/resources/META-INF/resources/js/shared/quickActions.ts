/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

/**
 * Lets an agent handle a Quick Action so clicking it starts the agent flow
 * (fires an invoke) instead of sending the label as plain chat text. Handlers
 * receive the current chat context so they can route with the active folder,
 * space or entry. Labels without a handler fall back to a chat message.
 */
export type QuickActionHandler = (context: Record<string, unknown>) => void;

const handlers: Record<string, QuickActionHandler> = {};

export function registerQuickAction(
	label: string,
	handler: QuickActionHandler
) {
	handlers[label] = handler;
}

export function getQuickActionHandler(
	label: string
): QuickActionHandler | undefined {
	return handlers[label];
}
