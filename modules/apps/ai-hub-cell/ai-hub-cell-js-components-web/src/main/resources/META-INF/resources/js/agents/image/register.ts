/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {registerAgent} from '../../framework/agentRegistry';
import {EAgent} from '../../shared/types';
import AdaptChannelsBalloon from './AdaptChannelsBalloon';
import ImageGenerationBalloon from './ImageGenerationBalloon';

registerAgent({
	renderers: [
		{renderer: ImageGenerationBalloon, type: EAgent.GENERATE_IMAGE},
		{renderer: AdaptChannelsBalloon, type: EAgent.ADAPT_CHANNELS},
	],
});
