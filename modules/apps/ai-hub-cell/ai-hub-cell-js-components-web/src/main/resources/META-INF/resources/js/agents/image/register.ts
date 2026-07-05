/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {registerMessageRenderer} from '../../AIAssistantChat/messageRenderers';
import {EAgent} from '../../shared/types';
import AdaptChannelsBalloon from './AdaptChannelsBalloon';
import ImageGenerationBalloon from './ImageGenerationBalloon';

registerMessageRenderer(EAgent.GENERATE_IMAGE, ImageGenerationBalloon);
registerMessageRenderer(EAgent.ADAPT_CHANNELS, AdaptChannelsBalloon);
