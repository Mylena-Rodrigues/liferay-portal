/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {endpoint} from '../utils/constants';
import {request} from '../utils/request';
import {getCurrentSiteId, getCurrentUserId} from '../utils/util';

export async function getUserEnrollmentByLearningPathId(learningPathId) {
	const data = await request({
		params: {
			filter: `r_learningPathEnrollment_c_learningPathId eq '${learningPathId}' and r_userenrollments_userId eq '${getCurrentUserId()}'`,
		},
		url: `${endpoint.enrollment}scopes/${getCurrentSiteId()}`,
	});

	return data?.items[0];
}

export async function updateCompletedSteps({completedStepsIds, id}, stepId) {
	const completedStepsList = completedStepsIds
		? (completedStepsIds += ',' + stepId)
		: stepId;

	await request({
		data: {
			completedStepsIds: completedStepsList,
		},
		method: 'PATCH',
		url: `${endpoint.enrollment}${id}`,
	});
}
