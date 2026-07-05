/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {registerMessageRenderer} from '../../AIAssistantChat/messageRenderers';
import {EAgent} from '../../shared/types';
import FindAssetsBalloon from './FindAssetsBalloon';
import GapInsightsBalloon from './GapInsightsBalloon';
import GenerateForGapBalloon from './GenerateForGapBalloon';
import {GAP_GENERATE_RENDERER} from './types';

registerMessageRenderer(EAgent.GAP_ANALYSIS, GapInsightsBalloon);
registerMessageRenderer(EAgent.GAP_FIND_ASSETS, FindAssetsBalloon);
registerMessageRenderer(GAP_GENERATE_RENDERER, GenerateForGapBalloon);
