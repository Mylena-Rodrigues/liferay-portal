/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

/**
 * Registers every embedded agent's message renderer with the chat host.
 * Imported for its side effects by AIAssistantChat. Each epic appends its
 * registration import below.
 */

import './content/register';

import './gap/register';

import './image/register';

export {};
