/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {registerMessageRenderer} from '../../AIAssistantChat/messageRenderers';
import {EAgent} from '../../shared/types';
import ContentBalloon from './ContentBalloon';
import ContentEditorBalloon from './ContentEditorBalloon';
import {CONTENT_EDITOR_RENDERER} from './types';

registerMessageRenderer(EAgent.GENERATE_CONTENT, ContentBalloon);
registerMessageRenderer(CONTENT_EDITOR_RENDERER, ContentEditorBalloon);
