/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import ContentGenerationBalloon from './ContentGenerationBalloon';
import MultipleContentBalloon from './MultipleContentBalloon';
import {ContentGenerationContext} from './types';

/**
 * Both single (94019) and multiple (94023) content generation share the same
 * agent reference; the requested count selects which balloon drives the flow.
 */
export default function ContentBalloon({
	payload,
}: {
	payload: ContentGenerationContext;
}) {
	if ((payload.count ?? 1) > 1) {
		return <MultipleContentBalloon payload={payload} />;
	}

	return <ContentGenerationBalloon payload={payload} />;
}
