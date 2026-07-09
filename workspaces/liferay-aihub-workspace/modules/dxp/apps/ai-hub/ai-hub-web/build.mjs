/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {runBuild} from '../../../../../scripts/liferay-esm-build.mjs';

await runBuild({

	// @clayui/toolbar does not publish src/ to npm and the portal does not
	// export the src/Link submodule; the same component is available as a
	// property of the default Toolbar export.

	virtualModules: {
		'@clayui/toolbar/src/Link': `
import Toolbar from '@clayui/toolbar';

export const Link = Toolbar.Link;
`,
	},
	webContext: 'ai-hub-web',
});
