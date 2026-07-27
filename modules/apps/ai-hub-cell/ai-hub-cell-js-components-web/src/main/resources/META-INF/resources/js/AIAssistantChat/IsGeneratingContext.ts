/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {createContext, useContext} from 'react';

interface IsGeneratingContextValue {
	isGenerating: boolean;
	setIsGenerating: (isGenerating: boolean) => void;
}

export const IsGeneratingContext = createContext<IsGeneratingContextValue>({
	isGenerating: false,
	setIsGenerating: () => {},
});

export function useIsGenerating(): boolean {
	return useContext(IsGeneratingContext).isGenerating;
}

export function useSetIsGenerating(): (isGenerating: boolean) => void {
	return useContext(IsGeneratingContext).setIsGenerating;
}
