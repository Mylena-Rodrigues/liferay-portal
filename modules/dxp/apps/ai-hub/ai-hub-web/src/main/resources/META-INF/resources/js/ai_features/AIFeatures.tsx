/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayForm, {
	ClayInput,
	ClaySelectWithOption,
	ClayToggle,
} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import ClayModal, {useModal} from '@clayui/modal';
import ClayPanel from '@clayui/panel';
import {Provider} from '@clayui/provider';
import React, {useEffect, useState} from 'react';

import './AIFeatures.scss';
import {
	AIFeatures as AIFeaturesType,
	getAIFeatures,
	patchAIFeatures,
} from './services/AIFeaturesService';

interface Props {
	auditURL?: string;
}

export default function AIFeatures({auditURL}: Props) {
	const [aiFeatures, setAIFeatures] = useState<AIFeaturesType>({
		enable: true,
	});
	const [comment, setComment] = useState('');
	const [reason, setReason] = useState('');
	const [submitting, setSubmitting] = useState(false);
	const [visibleModal, setVisibleModal] = useState(false);

	const {observer, onClose} = useModal({
		onClose: () => {
			setComment('');
			setReason('');
			setVisibleModal(false);
		},
	});

	useEffect(() => {
		getAIFeatures()
			.then(setAIFeatures)
			.catch(() => {});
	}, []);

	const targetEnable = !aiFeatures.enable;

	const reasonOptions = [
		{label: Liferay.Language.get('choose-an-option'), value: ''},
		{
			label: Liferay.Language.get('incident-or-data-leak-response'),
			value: 'incidentOrDataLeakResponse',
		},
		{
			label: Liferay.Language.get('inappropriate-or-unethical-content'),
			value: 'inappropriateOrUnethicalContent',
		},
		{
			label: Liferay.Language.get(
				'exposure-of-personal-sensitive-data-pii'
			),
			value: 'exposureOfPersonalOrSensitiveData',
		},
		{
			label: Liferay.Language.get('legal-service-deployment'),
			value: 'legalOrServiceDeployment',
		},
		{label: Liferay.Language.get('other'), value: 'other'},
	];

	const handleConfirm = () => {
		setSubmitting(true);

		patchAIFeatures(targetEnable, reason, comment)
			.then((data) => {
				setAIFeatures(data);
				setComment('');
				setReason('');
				setVisibleModal(false);

				Liferay.Util.openToast({
					message: targetEnable
						? Liferay.Language.get(
								'ai-features-were-successfully-enabled'
							)
						: Liferay.Language.get(
								'ai-features-were-successfully-disabled'
							),
					type: 'success',
				});
			})
			.catch(() => {
				Liferay.Util.openToast({
					message: Liferay.Language.get(
						'an-unexpected-error-occurred'
					),
					type: 'danger',
				});
			})
			.finally(() => setSubmitting(false));
	};

	return (
		<Provider spritemap={Liferay.Icons.spritemap}>
			<div className="ai-features container-fluid mt-3">
				<ClayPanel collapsable={false}>
					<ClayPanel.Body>
						<div className="ai-features__header">
							<h2 className="ai-features__title">
								{Liferay.Language.get('ai-features')}
							</h2>

							<ClayToggle
								aria-label={Liferay.Language.get(
									'enable-ai-features'
								)}
								disabled={submitting}
								onToggle={() => setVisibleModal(true)}
								toggled={aiFeatures.enable}
							/>
						</div>

						<p className="ai-features__description text-secondary">
							{Liferay.Language.get(
								'enable-or-disable-all-ai-agents-and-ai-powered-features-for-your-organization'
							)}
						</p>

						{aiFeatures.lastModifiedDate && auditURL ? (
							<ClayLink
								className="ai-features__view-logs"
								href={auditURL}
							>
								{Liferay.Language.get('view-logs')}

								<ClayIcon symbol="shortcut" />
							</ClayLink>
						) : null}
					</ClayPanel.Body>
				</ClayPanel>

				{visibleModal ? (
					<ClayModal observer={observer} status="warning">
						<ClayModal.Header>
							{targetEnable
								? Liferay.Language.get('enable-ai-features')
								: Liferay.Language.get('disable-ai-features')}
						</ClayModal.Header>

						<ClayModal.Body>
							{targetEnable ? (
								<p>
									{Liferay.Language.get(
										'ai-features-will-be-restored-for-your-organization'
									)}{' '}

									{Liferay.Language.get(
										'this-action-will-be-recorded-in-the-audit-log'
									)}
								</p>
							) : (
								<>
									<p>
										{Liferay.Language.get(
											'this-will-immediately'
										)}
									</p>

									<ul>
										<li>
											{Liferay.Language.get(
												'stop-all-agent-executions-in-progress'
											)}
										</li>

										<li>
											{Liferay.Language.get(
												'prevent-any-new-agent-from-running'
											)}
										</li>

										<li>
											{Liferay.Language.get(
												'display-a-notice-to-all-ai-hub-users-in-your-organization'
											)}
										</li>
									</ul>

									<p>
										{Liferay.Language.get(
											'this-action-will-be-recorded-in-the-audit-log'
										)}
									</p>

									<ClayForm.Group>
										<label htmlFor="aiFeaturesReason">
											{Liferay.Language.get('reason')}

											<span className="reference-mark">
												<ClayIcon symbol="asterisk" />
											</span>
										</label>

										<ClaySelectWithOption
											aria-label={Liferay.Language.get(
												'reason'
											)}
											id="aiFeaturesReason"
											onChange={(event) =>
												setReason(event.target.value)
											}
											options={reasonOptions}
											value={reason}
										/>
									</ClayForm.Group>

									<ClayForm.Group>
										<label htmlFor="aiFeaturesComment">
											{Liferay.Language.get('comment')}
										</label>

										<ClayInput
											component="textarea"
											id="aiFeaturesComment"
											onChange={(event) =>
												setComment(event.target.value)
											}
											placeholder={Liferay.Language.get(
												'comment'
											)}
											value={comment}
										/>
									</ClayForm.Group>
								</>
							)}
						</ClayModal.Body>

						<ClayModal.Footer
							last={
								<ClayButton.Group spaced>
									<ClayButton
										displayType="secondary"
										onClick={onClose}
									>
										{Liferay.Language.get('cancel')}
									</ClayButton>

									<ClayButton
										disabled={
											submitting ||
											(!targetEnable && !reason)
										}
										displayType={
											targetEnable ? 'primary' : 'danger'
										}
										onClick={handleConfirm}
									>
										{targetEnable
											? Liferay.Language.get('enable')
											: Liferay.Language.get('disable')}
									</ClayButton>
								</ClayButton.Group>
							}
						/>
					</ClayModal>
				) : null}
			</div>
		</Provider>
	);
}
