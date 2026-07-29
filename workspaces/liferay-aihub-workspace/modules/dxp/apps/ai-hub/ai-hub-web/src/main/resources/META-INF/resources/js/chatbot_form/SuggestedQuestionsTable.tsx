/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrontendDataSet} from '@liferay/frontend-data-set-web';
import React, {useState} from 'react';
import {v4 as uuidv4} from 'uuid';

import QuestionModal from './QuestionModal';
import {SuggestedQuestion} from './types/Chatbot';

export default function SuggestedQuestionsTable({
	onSuggestedQuestionsChange,
	readOnly,
	suggestedQuestions,
}: {
	onSuggestedQuestionsChange: (
		suggestedQuestions: SuggestedQuestion[]
	) => void;
	readOnly: boolean;
	suggestedQuestions: SuggestedQuestion[];
}) {
	const [modalVisible, setModalVisible] = useState(false);
	const [selectedQuestion, setSelectedQuestion] =
		useState<SuggestedQuestion | null>(null);

	const defaultLanguageId = Liferay.ThemeDisplay.getDefaultLanguageId();

	const items = suggestedQuestions.map((suggestedQuestion) => ({
		id: suggestedQuestion.id,
		question:
			suggestedQuestion.question_i18n[defaultLanguageId] ||
			Object.values(suggestedQuestion.question_i18n)[0] ||
			'',
	}));

	const moveQuestion = (id: string, offset: number) => {
		const index = suggestedQuestions.findIndex(
			(suggestedQuestion) => suggestedQuestion.id === id
		);

		const newIndex = index + offset;

		if (
			index < 0 ||
			newIndex < 0 ||
			newIndex >= suggestedQuestions.length
		) {
			return;
		}

		const newSuggestedQuestions = [...suggestedQuestions];

		[newSuggestedQuestions[index], newSuggestedQuestions[newIndex]] = [
			newSuggestedQuestions[newIndex],
			newSuggestedQuestions[index],
		];

		onSuggestedQuestionsChange(newSuggestedQuestions);
	};

	return (
		<>
			{modalVisible && (
				<QuestionModal
					onClose={() => {
						setModalVisible(false);
						setSelectedQuestion(null);
					}}
					onSave={(question_i18n) => {
						if (selectedQuestion) {
							onSuggestedQuestionsChange(
								suggestedQuestions.map((suggestedQuestion) =>
									suggestedQuestion.id ===
									selectedQuestion.id
										? {...suggestedQuestion, question_i18n}
										: suggestedQuestion
								)
							);
						}
						else {
							onSuggestedQuestionsChange([
								...suggestedQuestions,
								{id: uuidv4(), question_i18n},
							]);
						}
					}}
					question={selectedQuestion}
				/>
			)}

			<FrontendDataSet
				creationMenu={{
					primaryItems: readOnly
						? []
						: [
								{
									label: Liferay.Language.get('add-question'),
									onClick: () => setModalVisible(true),
								},
							],
				}}
				id="chatbotSuggestedQuestions"
				items={items}
				itemsActions={
					readOnly
						? []
						: [
								{
									icon: 'pencil',
									label: Liferay.Language.get('edit'),
									onClick: ({
										itemData,
									}: {
										itemData: {id: string};
									}) => {
										setSelectedQuestion(
											suggestedQuestions.find(
												(suggestedQuestion) =>
													suggestedQuestion.id ===
													itemData.id
											) ?? null
										);
										setModalVisible(true);
									},
								},
								{
									icon: 'order-arrow-up',
									label: Liferay.Language.get('move-up'),
									onClick: ({
										itemData,
									}: {
										itemData: {id: string};
									}) => moveQuestion(itemData.id, -1),
								},
								{
									icon: 'order-arrow-down',
									label: Liferay.Language.get('move-down'),
									onClick: ({
										itemData,
									}: {
										itemData: {id: string};
									}) => moveQuestion(itemData.id, 1),
								},
								{
									icon: 'trash',
									label: Liferay.Language.get('delete'),
									onClick: ({
										itemData,
									}: {
										itemData: {id: string};
									}) =>
										onSuggestedQuestionsChange(
											suggestedQuestions.filter(
												(suggestedQuestion) =>
													suggestedQuestion.id !==
													itemData.id
											)
										),
								},
							]
				}
				showManagementBar
				showPagination={false}
				showSearch={false}
				views={[
					{
						contentRenderer: 'table',
						name: 'table',
						schema: {
							fields: [
								{
									fieldName: 'question',
									label: Liferay.Language.get('question'),
									sortable: false,
								},
							],
						},
					},
				]}
			/>
		</>
	);
}
