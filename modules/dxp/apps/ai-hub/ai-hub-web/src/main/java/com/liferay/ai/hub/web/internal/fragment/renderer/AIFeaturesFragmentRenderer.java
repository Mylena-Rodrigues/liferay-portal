/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.web.internal.fragment.renderer;

import com.liferay.ai.hub.web.internal.display.context.AIFeaturesDisplayContext;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.portal.kernel.util.Portal;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mylena Monte
 */
@Component(service = FragmentRenderer.class)
public class AIFeaturesFragmentRenderer
	extends BaseFragmentRenderer<AIFeaturesDisplayContext> {

	@Override
	public String getCollectionKey() {
		return "ai-features";
	}

	@Override
	protected AIFeaturesDisplayContext getDisplayContext(
		HttpServletRequest httpServletRequest) {

		return new AIFeaturesDisplayContext(httpServletRequest, _portal);
	}

	@Override
	protected String getJSPPath() {
		return "/ai_features.jsp";
	}

	@Reference
	private Portal _portal;

}