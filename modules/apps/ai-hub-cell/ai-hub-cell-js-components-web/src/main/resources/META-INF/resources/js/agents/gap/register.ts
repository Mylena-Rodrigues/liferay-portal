/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {registerAgent} from '../../framework/agentRegistry';
import {EAgent} from '../../shared/types';
import FindAssetsBalloon from './FindAssetsBalloon';
import GapInsightsBalloon from './GapInsightsBalloon';
import GenerateForGapBalloon from './GenerateForGapBalloon';
import {GAP_GENERATE_RENDERER} from './types';

registerAgent({
	renderers: [
		{renderer: GapInsightsBalloon, type: EAgent.GAP_ANALYSIS},
		{renderer: FindAssetsBalloon, type: EAgent.GAP_FIND_ASSETS},
		{renderer: GenerateForGapBalloon, type: GAP_GENERATE_RENDERER},
	],
});
