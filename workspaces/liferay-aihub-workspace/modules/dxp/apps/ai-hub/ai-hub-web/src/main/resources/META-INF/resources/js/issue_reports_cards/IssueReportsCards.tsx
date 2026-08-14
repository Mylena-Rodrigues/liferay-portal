/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import React from 'react';

import MetricCard from '../components/MetricCard';

interface IssueReportsCardsProps {
	criticalIssuesCount: number;
	dislikeRatingPercent: number;
	positiveRatingPercent: number;
}

export default function IssueReportsCards({
	criticalIssuesCount,
	dislikeRatingPercent,
	positiveRatingPercent,
}: IssueReportsCardsProps) {
	return (
		<section className="container-fluid issue-reports-user-activity mb-4">
			<h2 className="h4 mb-3">{Liferay.Language.get('user-activity')}</h2>

			<div className="row">
				<div className="col-12 col-md-4 mb-3">
					<MetricCard
						icon={<ClayIcon symbol="thumbs-up" />}
						title={Liferay.Language.get('positive-rating')}
						value={`${positiveRatingPercent}%`}
					/>
				</div>

				<div className="col-12 col-md-4 mb-3">
					<MetricCard
						icon={<ClayIcon symbol="thumbs-down" />}
						title={Liferay.Language.get('dislike-rating')}
						value={`${dislikeRatingPercent}%`}
					/>
				</div>

				<div className="col-12 col-md-4 mb-3">
					<MetricCard
						icon={<ClayIcon symbol="exclamation-full" />}
						title={Liferay.Language.get('critical-issues')}
						value={criticalIssuesCount}
					/>
				</div>
			</div>
		</section>
	);
}
