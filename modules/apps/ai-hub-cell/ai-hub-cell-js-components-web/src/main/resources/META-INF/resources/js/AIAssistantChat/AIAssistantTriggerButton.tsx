/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {ComponentType, Suspense, lazy} from 'react';

import loadAIHub from '../loadAIHub';

import type {AIAssistantTriggerButtonProps} from '../types';

const AIHubTriggerButton = lazy<ComponentType<AIAssistantTriggerButtonProps>>(
	() =>
		loadAIHub().then((aiHub) => ({
			default: aiHub?.AIAssistantTriggerButton ?? (() => null),
		}))
);

export default function AIAssistantTriggerButton(
	props: AIAssistantTriggerButtonProps
) {
	return (
		<Suspense fallback={null}>
			<AIHubTriggerButton {...props} />
		</Suspense>
	);
}
