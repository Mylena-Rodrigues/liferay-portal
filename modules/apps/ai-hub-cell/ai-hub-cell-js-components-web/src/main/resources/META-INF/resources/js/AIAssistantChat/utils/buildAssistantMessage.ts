/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {message} from '../types';
import formatContentGapAnalysis, {
	CONTENT_GAP_ANALYSIS_ERC,
} from './formatContentGapAnalysis';

export interface ChatMessageSentData {
	agentDefinitionExternalReferenceCodes?: string[];
	data?: string;
	mimeType?: string;
	type?: string;
}

const TEXT_ANSWER_FORMATTERS: Record<string, (data: string) => string | null> =
	{
		[CONTENT_GAP_ANALYSIS_ERC]: formatContentGapAnalysis,
	};

function formatAgentAnswer(
	data: string,
	agentDefinitionExternalReferenceCodes: string[]
): string {
	for (const agentDefinitionExternalReferenceCode of agentDefinitionExternalReferenceCodes) {
		const formatter =
			TEXT_ANSWER_FORMATTERS[agentDefinitionExternalReferenceCode];

		if (formatter) {
			return formatter(data) ?? data;
		}
	}

	return data;
}

export default function buildAssistantMessage(
	dataJSON: ChatMessageSentData
): message {
	const agentDefinitionExternalReferenceCodes =
		dataJSON.agentDefinitionExternalReferenceCodes ?? [];

	const data = dataJSON.data ?? '';

	return {
		agentDefinitionExternalReferenceCodes,
		sender: 'assistant',
		text:
			(dataJSON.type ?? 'text') === 'text'
				? formatAgentAnswer(data, agentDefinitionExternalReferenceCodes)
				: data,
	};
}
