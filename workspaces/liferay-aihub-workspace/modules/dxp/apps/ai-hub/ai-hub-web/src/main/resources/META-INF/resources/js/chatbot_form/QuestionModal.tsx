/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Button from '@clayui/button';
import ClayForm from '@clayui/form';
import ClayModal, {useModal} from '@clayui/modal';
import {InputLocalized} from 'frontend-js-components-web';
import React, {useState} from 'react';

import {SuggestedQuestion} from './types/Chatbot';

const QUESTION_MAXIMUM_LENGTH = 200;

export default function QuestionModal({
	onClose,
	onSave,
	question,
}: {
	onClose: () => void;
	onSave: (question_i18n: {[key: string]: string}) => void;
	question: SuggestedQuestion | null;
}) {
	const [question_i18n, setQuestion_i18n] = useState<{[key: string]: string}>(
		question?.question_i18n ?? {}
	);

	const {observer, onClose: closeModal} = useModal({
		onClose,
	});

	const defaultLanguageId = Liferay.ThemeDisplay.getDefaultLanguageId();

	const valid = Boolean(question_i18n[defaultLanguageId]?.trim());

	return (
		<ClayModal observer={observer}>
			<ClayModal.Header>
				{question
					? Liferay.Language.get('edit-question')
					: Liferay.Language.get('add-question')}
			</ClayModal.Header>

			<ClayModal.Body>
				<ClayForm.Group>
					<InputLocalized
						id="question"
						label={Liferay.Language.get('question')}
						maxLength={QUESTION_MAXIMUM_LENGTH}
						name="question_i18n"
						onChange={(value) => setQuestion_i18n(value)}
						onSelectedLocaleChange={() => {}}
						placeholder={Liferay.Language.get('question')}
						required
						translations={question_i18n}
					/>
				</ClayForm.Group>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<Button.Group spaced>
						<Button
							displayType="secondary"
							onClick={() => closeModal()}
						>
							{Liferay.Language.get('cancel')}
						</Button>

						<Button
							disabled={!valid}
							onClick={() => {
								onSave(question_i18n);

								closeModal();
							}}
						>
							{Liferay.Language.get('save')}
						</Button>
					</Button.Group>
				}
			/>
		</ClayModal>
	);
}
