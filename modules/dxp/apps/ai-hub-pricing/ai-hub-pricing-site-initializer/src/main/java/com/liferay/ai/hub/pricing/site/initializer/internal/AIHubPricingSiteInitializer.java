/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.pricing.site.initializer.internal;

import com.liferay.site.exception.InitializationException;
import com.liferay.site.initializer.SiteInitializer;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carolina Barbosa
 */
@Component(
	property = {
		"service.ranking:Integer=100",
		"site.initializer.key=com.liferay.ai.hub.site.initializer"
	},
	service = SiteInitializer.class
)
public class AIHubPricingSiteInitializer implements SiteInitializer {

	@Override
	public String getDescription(Locale locale) {
		return _aiHubSiteInitializer.getDescription(locale);
	}

	@Override
	public String getKey() {
		return _aiHubSiteInitializer.getKey();
	}

	@Override
	public String getName(Locale locale) {
		return _aiHubSiteInitializer.getName(locale);
	}

	@Override
	public String getThumbnailSrc() {
		return _aiHubSiteInitializer.getThumbnailSrc();
	}

	@Override
	public void initialize(long groupId) throws InitializationException {
		_aiHubSiteInitializer.initialize(groupId);

		_aiHubPricingSiteInitializer.initialize(groupId);
	}

	@Override
	public boolean isActive(long companyId) {
		return _aiHubSiteInitializer.isActive(companyId);
	}

	@Reference(
		target = "(site.initializer.key=com.liferay.ai.hub.pricing.site.initializer)"
	)
	private SiteInitializer _aiHubPricingSiteInitializer;

	@Reference(
		target = "(&(site.initializer.key=com.liferay.ai.hub.site.initializer)(!(component.name=com.liferay.ai.hub.pricing.site.initializer.internal.AIHubPricingSiteInitializer)))"
	)
	private SiteInitializer _aiHubSiteInitializer;

}