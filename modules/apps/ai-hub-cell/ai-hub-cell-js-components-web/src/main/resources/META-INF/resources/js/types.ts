/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import type {Editor} from '@ckeditor/ckeditor5-core';
import type {ComponentType} from 'react';

export interface AIAssistantActionOutcome {
	response?: Response;
	success: boolean;
}

export type AIAssistantPresentation = 'dropdown' | 'sidebar';

export interface ChatContext {
	fileUploadSelector?: string;
	groupId?: number | string;
	objectEntryFolderExternalReferenceCode?: string;
	[key: string]: unknown;
}

export interface AIAssistantOpenCommand {
	anchorId?: string;
	chatbotExternalReferenceCode?: string;
	context?: ChatContext;
	enableFreeFormCategorization?: boolean;
	getContext?: () => ChatContext;
	greeting?: string;
	initialMessage?: string;
	instructionDefinitionScope: string;
	newSession?: boolean;
	onAction?: (outcome: AIAssistantActionOutcome) => void;
	presentation?: AIAssistantPresentation;
	pushContainer?: string;
	quickActions?: string[];
	sidebarBehavior?: 'overlay' | 'push';
	triggerId: string;
}

export type AIAssistantTriggerButtonProps = Omit<
	AIAssistantOpenCommand,
	'anchorId' | 'triggerId'
> & {
	anchorId?: string;
	className?: string;
	hideLabel?: boolean;
	label?: string;
	onOpen?: () => void;
	round?: boolean;
	triggerId?: string;
};

export interface AIHub {
	initWritingAssistant(editor: Editor): Promise<() => void>;
	AIAssistant: {
		close(): void;
		open(command: AIAssistantOpenCommand): void;
	};
	AIAssistantTriggerButton: ComponentType<AIAssistantTriggerButtonProps>;
}
