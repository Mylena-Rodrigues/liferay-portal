/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {loadModule} from 'frontend-js-web';
import React, {useEffect, useState} from 'react';

export type ChatContext = {
	[key: string]: unknown;
};

interface AIAssistantTriggerButtonProps {
	getContext?: () => ChatContext;
	initialMessage?: string;
	instructionDefinitionScope: string;
	quickActions?: string[];
}

export default function AIAssistantTriggerButton(
	props: AIAssistantTriggerButtonProps
) {
	const [Component, setComponent] =
		useState<React.ComponentType<AIAssistantTriggerButtonProps> | null>(
			null
		);

	useEffect(() => {
		loadModule(
			'{AIAssistantTriggerButton} from @liferay/ai-hub-cell-js-components-web'
		)
			.then(
				(
					component: React.ComponentType<AIAssistantTriggerButtonProps>
				) => setComponent(() => component)
			)
			.catch((error: unknown) => {
				if (process.env.NODE_ENV === 'development') {
					console.error(error);
				}
			});
	}, []);

	return Component ? <Component {...props} /> : null;
}
