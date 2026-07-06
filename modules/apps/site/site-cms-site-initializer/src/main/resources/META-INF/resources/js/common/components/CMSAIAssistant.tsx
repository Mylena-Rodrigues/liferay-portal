/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '../../../css/components/CMSAIAssistant.scss';

import {AIAssistantChat} from '@liferay/ai-hub-cell-js-components-web';
import React from 'react';

export default function CMSAIAssistant() {
	return (
		<AIAssistantChat
			getContext={() => ({})}
			hideTriggerLabel
			instructionDefinitionScope="cms"
			triggerClassName="cms-ai-assistant__trigger"
		/>
	);
}
