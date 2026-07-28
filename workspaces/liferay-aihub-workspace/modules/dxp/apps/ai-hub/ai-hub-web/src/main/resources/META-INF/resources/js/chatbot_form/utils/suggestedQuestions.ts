/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {v4 as uuidv4} from 'uuid';

import {SuggestedQuestion} from '../types/Chatbot';

export function toLocalizedValue(suggestedQuestions: SuggestedQuestion[]): {
	[key: string]: string;
} {
	const rows = suggestedQuestions.filter((suggestedQuestion) =>
		Object.values(suggestedQuestion.question_i18n).some((question) =>
			question.trim()
		)
	);

	const localizedValue: {[key: string]: string} = {};

	const languageIds = new Set(
		rows.flatMap((row) => Object.keys(row.question_i18n))
	);

	for (const languageId of languageIds) {
		const questions = rows.map((row) =>
			(row.question_i18n[languageId] ?? '').trim()
		);

		while (questions.length && !questions[questions.length - 1]) {
			questions.pop();
		}

		if (questions.length) {
			localizedValue[languageId] = questions.join('\n');
		}
	}

	return localizedValue;
}

export function toSuggestedQuestions(localizedValue: {
	[key: string]: string;
}): SuggestedQuestion[] {
	const linesByLanguageId = Object.entries(localizedValue ?? {})
		.filter(([, value]) => value)
		.map(
			([languageId, value]) =>
				[languageId, value.split(/\r\n|\r|\n/)] as const
		);

	const rowCount = Math.max(
		0,
		...linesByLanguageId.map(([, lines]) => lines.length)
	);

	const suggestedQuestions: SuggestedQuestion[] = [];

	for (let i = 0; i < rowCount; i++) {
		const question_i18n: {[key: string]: string} = {};

		for (const [languageId, lines] of linesByLanguageId) {
			const question = (lines[i] ?? '').trim();

			if (question) {
				question_i18n[languageId] = question;
			}
		}

		if (Object.keys(question_i18n).length) {
			suggestedQuestions.push({id: uuidv4(), question_i18n});
		}
	}

	return suggestedQuestions;
}
