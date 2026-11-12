/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export {default as AIAssistant} from './AIAssistantChat/AIAssistant';
export {default as AIAssistantTriggerButton} from './AIAssistantChat/AIAssistantTriggerButton';
export {getObjectFields} from './AIAssistantChat/services/getObjectFields';
export type {ObjectField} from './AIAssistantChat/services/getObjectFields';
export {getSpaces} from './AIAssistantChat/services/getSpaces';
export type {Space} from './AIAssistantChat/services/getSpaces';
export {default as WritingAssistant} from './WritingAssistant/WritingAssistant';
export {CONTENT_CHANGED_EVENT} from './constants';
export {default as renderAIAssistantTrigger} from './renderAIAssistantTrigger';
export type {
	AIAssistantActionOutcome,
	AIAssistantOpenCommand,
	AIAssistantPresentation,
	AIAssistantTriggerButtonProps,
	AIHub,
	ChatContext,
} from './types';
