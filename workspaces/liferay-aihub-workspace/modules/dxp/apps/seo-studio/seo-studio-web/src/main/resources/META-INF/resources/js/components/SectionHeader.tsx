/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import React from 'react';

import './SectionHeader.scss';

export default function SectionHeader({
	lastScanDate,
	showRunScanButton = true,
	title,
}: {
	lastScanDate: string | null;
	showRunScanButton?: boolean;
	title: string;
}) {
	const lastScanLabel = lastScanDate || Liferay.Language.get('never');

	return (
		<div className="seo-studio-section-header">
			<h2 className="seo-studio-section-header-title">{title}</h2>

			<div className="seo-studio-section-header-actions text-right">
				{showRunScanButton && (
					<ClayButton
						disabled
						displayType="primary"
						title={Liferay.Language.get('run-scan-now')}
					>
						{Liferay.Language.get('run-scan-now')}
					</ClayButton>
				)}

				<div className="seo-studio-section-header-last-scan text-secondary">
					{Liferay.Language.get('last-scan')}: {lastScanLabel}
				</div>
			</div>
		</div>
	);
}
