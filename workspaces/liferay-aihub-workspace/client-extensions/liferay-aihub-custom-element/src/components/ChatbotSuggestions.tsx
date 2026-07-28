/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

interface ChatbotSuggestionsProps {
	disabled: boolean;
	onSelect: (question: string) => void;
	questions: string[];
}

export default function ChatbotSuggestions({
	disabled,
	onSelect,
	questions,
}: ChatbotSuggestionsProps) {
	return (
		<div className="aihub-suggestions">
			{questions.map((question, index) => (
				<button
					className="aihub-suggestion"
					disabled={disabled}
					key={index}
					onClick={() => onSelect(question)}
					type="button"
				>
					{question}
				</button>
			))}
		</div>
	);
}
