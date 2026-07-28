/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	toLocalizedValue,
	toSuggestedQuestions,
} from '../../../../src/main/resources/META-INF/resources/js/chatbot_form/utils/suggestedQuestions';

describe('toSuggestedQuestions', () => {
	it('returns an empty list for an empty localized value', () => {
		expect(toSuggestedQuestions({})).toEqual([]);
	});

	it('splits each locale into rows aligned by line index', () => {
		const suggestedQuestions = toSuggestedQuestions({
			en_US: 'Q1\nQ2',
			pt_BR: 'P1\nP2',
		});

		expect(
			suggestedQuestions.map(
				(suggestedQuestion) => suggestedQuestion.question_i18n
			)
		).toEqual([
			{en_US: 'Q1', pt_BR: 'P1'},
			{en_US: 'Q2', pt_BR: 'P2'},
		]);
	});

	it('keeps rows aligned when a locale has blank padding lines', () => {
		const suggestedQuestions = toSuggestedQuestions({
			en_US: 'Q1\nQ2\nQ3',
			pt_BR: 'P1\n\nP3',
		});

		expect(
			suggestedQuestions.map(
				(suggestedQuestion) => suggestedQuestion.question_i18n
			)
		).toEqual([
			{en_US: 'Q1', pt_BR: 'P1'},
			{en_US: 'Q2'},
			{en_US: 'Q3', pt_BR: 'P3'},
		]);
	});

	it('drops rows that are blank in every locale', () => {
		const suggestedQuestions = toSuggestedQuestions({
			en_US: 'Q1\n   \nQ3',
			pt_BR: 'P1\n\nP3',
		});

		expect(
			suggestedQuestions.map(
				(suggestedQuestion) => suggestedQuestion.question_i18n
			)
		).toEqual([
			{en_US: 'Q1', pt_BR: 'P1'},
			{en_US: 'Q3', pt_BR: 'P3'},
		]);
	});

	it('assigns a unique id to every row', () => {
		const suggestedQuestions = toSuggestedQuestions({en_US: 'Q1\nQ2'});

		expect(suggestedQuestions[0].id).not.toEqual(
			suggestedQuestions[1].id
		);
	});
});

describe('toLocalizedValue', () => {
	it('returns an empty object for no rows', () => {
		expect(toLocalizedValue([])).toEqual({});
	});

	it('joins each locale with newlines preserving row order', () => {
		expect(
			toLocalizedValue([
				{id: '1', question_i18n: {en_US: 'Q1', pt_BR: 'P1'}},
				{id: '2', question_i18n: {en_US: 'Q2', pt_BR: 'P2'}},
			])
		).toEqual({en_US: 'Q1\nQ2', pt_BR: 'P1\nP2'});
	});

	it('pads untranslated rows with blank lines to keep alignment', () => {
		expect(
			toLocalizedValue([
				{id: '1', question_i18n: {en_US: 'Q1', pt_BR: 'P1'}},
				{id: '2', question_i18n: {en_US: 'Q2'}},
				{id: '3', question_i18n: {en_US: 'Q3', pt_BR: 'P3'}},
			])
		).toEqual({en_US: 'Q1\nQ2\nQ3', pt_BR: 'P1\n\nP3'});
	});

	it('omits locales that have no text in any row', () => {
		expect(
			toLocalizedValue([
				{id: '1', question_i18n: {en_US: 'Q1', pt_BR: '  '}},
			])
		).toEqual({en_US: 'Q1'});
	});

	it('drops rows that are empty in every locale', () => {
		expect(
			toLocalizedValue([
				{id: '1', question_i18n: {en_US: 'Q1'}},
				{id: '2', question_i18n: {en_US: '   '}},
				{id: '3', question_i18n: {en_US: 'Q3'}},
			])
		).toEqual({en_US: 'Q1\nQ3'});
	});

	it('round-trips the rows produced by toSuggestedQuestions', () => {
		const localizedValue = {en_US: 'Q1\nQ2\nQ3', pt_BR: 'P1\n\nP3'};

		expect(
			toLocalizedValue(toSuggestedQuestions(localizedValue))
		).toEqual(localizedValue);
	});
});
