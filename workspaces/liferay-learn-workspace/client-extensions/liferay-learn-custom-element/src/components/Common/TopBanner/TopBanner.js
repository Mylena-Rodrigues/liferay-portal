/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayTooltipProvider} from '@clayui/tooltip';
import {useTranslation} from 'react-i18next';

import {getPersonas, getTooltipPersona} from '../../../utils/util';

import './TopBanner.scss';

const Banner = ({name, personas = null, tag, totalDuration = null}) => {
	const {t} = useTranslation();

	return (
		<div className="top-banner">
			<div className="p-4">
				<div className="top-banner__tag">
					<p className={tag}>{t(tag)}</p>
				</div>

				<h1 className="top-banner__name">{name}</h1>

				<div className="top-banner__info">
					{totalDuration && (
						<div className="d-flex info-tag mt-3">
							<p className="info-tag__content info-tag__content-duration">
								{[totalDuration, ' hours']}
							</p>
						</div>
					)}

					{personas && (
						<div className="d-flex info-tag mt-3">
							<ClayTooltipProvider>
								<div
									className="info-tag__content info-tag__content-persona"
									data-tool-tip-align="top"
									title={getTooltipPersona(personas)}
								>
									<p></p>
									{getPersonas(personas)}
								</div>
							</ClayTooltipProvider>
						</div>
					)}
				</div>
			</div>
		</div>
	);
};

export default Banner;
