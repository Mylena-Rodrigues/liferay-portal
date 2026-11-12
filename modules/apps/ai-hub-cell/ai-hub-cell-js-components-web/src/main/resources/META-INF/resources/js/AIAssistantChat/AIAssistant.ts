/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import loadAIHub from '../loadAIHub';

import type {AIAssistantOpenCommand} from '../types';

function close() {
	loadAIHub().then((aiHub) => aiHub?.AIAssistant.close());
}

function open(command: AIAssistantOpenCommand) {
	loadAIHub().then((aiHub) => aiHub?.AIAssistant.open(command));
}

export default {close, open};
