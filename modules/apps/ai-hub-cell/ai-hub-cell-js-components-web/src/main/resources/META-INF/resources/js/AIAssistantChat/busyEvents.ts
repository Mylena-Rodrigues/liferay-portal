/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export const BUSY_EVENT = 'cms:aiAssistant:busy';

export interface BusyEventPayload {
	agentERC: string;
	busy: boolean;
}

export function fireBusyEvent(agentERC: string, busy: boolean): void {
	Liferay.fire(BUSY_EVENT, {agentERC, busy});
}
