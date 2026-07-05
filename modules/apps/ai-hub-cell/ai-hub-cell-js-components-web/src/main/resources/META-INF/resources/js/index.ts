/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export {default as AIAssistantChat} from './AIAssistantChat/AIAssistantChat';
export type {ChatContext} from './AIAssistantChat/api';
export {default as CategorizationSuggestions} from './Categorization/components/CategorizationSuggestions';
export {CATEGORIZE_EVENT, COMMIT_EVENT} from './Categorization/events';
export type {
	CategorizeEventPayload,
	CommitEventPayload,
} from './Categorization/events';
export {getCandidateCategories} from './Categorization/services/getCandidateCategories';
export {getExistingTags} from './Categorization/services/getExistingTags';
export {ECategorizationAgent} from './Categorization/types';
export type {
	CandidateCategory,
	CategorizationContext,
	CategorizationStatus,
	Suggestion,
} from './Categorization/types';
export {default as useCategorizationAgent} from './Categorization/useCategorizationAgent';
export {default as ReportFeedbackModal} from './ReportFeedback/ReportFeedbackModal';
export type {
	ReportFeedbackPayload,
	ReportFeedbackReason,
	ReportFeedbackSurface,
} from './ReportFeedback/api';
export {default as WritingAssistant} from './WritingAssistant/WritingAssistant';
export {generateContent} from './agents/content/triggers';
export {
	CONTENT_EDITOR_APPLY_EVENT,
	CONTENT_EDITOR_QUICK_ACTIONS,
} from './agents/content/types';
export {default as GapMatrix} from './agents/gap/GapMatrix';
export {getGapInsights} from './agents/gap/triggers';
export type {MatrixCell} from './agents/gap/types';
export {
	adaptForChannels,
	generateImageForField,
	generateImageFromFolder,
} from './agents/image/triggers';
export {AI_ASSISTANT_INVOKE_EVENT, fireInvokeAgent} from './shared/agentEvents';
export type {InvokeAgentEventPayload} from './shared/agentEvents';
export {EAgent} from './shared/types';
export type {AgentResultItem, AgentStatus, ChipOption} from './shared/types';
export {default as useAgent} from './shared/useAgent';
