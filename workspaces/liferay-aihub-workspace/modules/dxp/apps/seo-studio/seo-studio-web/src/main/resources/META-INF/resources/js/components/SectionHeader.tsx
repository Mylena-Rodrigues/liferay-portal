/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import React from 'react';

import './SectionHeader.scss';

export default function SectionHeader({
	icon,
	lastScanDate,
	title,
}: {
	icon?: string;
	lastScanDate: string | null;
	title: string;
}) {
	const lastScanLabel = lastScanDate || Liferay.Language.get('never');

	const themeImagesPath = Liferay.ThemeDisplay.getPathThemeImages?.() ?? '';

	const spritemap = `${themeImagesPath}/clay/icons.svg`;

	return (
		<div className="seo-studio-section-header">
			{icon && (
				<span className="seo-studio-section-header-icon">
					<ClayIcon spritemap={spritemap} symbol={icon} />
				</span>
			)}

			<h3 className="seo-studio-section-header-title">{title}</h3>

			<div className="seo-studio-section-header-last-scan text-secondary">
				<ClayIcon
					className="seo-studio-section-header-last-scan-icon text-success"
					spritemap={spritemap}
					symbol="check-circle"
				/>

				{Liferay.Language.get('last-scan')}: {lastScanLabel}
			</div>
		</div>
	);
}
