/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.seo.studio.web.internal.fragment.renderer;

import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.seo.studio.web.internal.constants.SEOStudioFDSNames;
import com.liferay.seo.studio.web.internal.display.context.ViewTechnicalDisplayContext;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;

/**
 * @author Brooke Dalton
 */
@Component(service = FragmentRenderer.class)
public class ViewTechnicalFragmentRenderer
	extends BaseFragmentRenderer<ViewTechnicalDisplayContext> {

	@Override
	public String getCollectionKey() {
		return "sections";
	}

	@Override
	public String getLabel(Locale locale) {
		return language.get(locale, "technical-view");
	}

	@Override
	protected ViewTechnicalDisplayContext getDisplayContext(
		HttpServletRequest httpServletRequest) {

		try {
			JSONArray filtersJSONArray = fdsSerializer.serializeFilters(
				SEOStudioFDSNames.INSIGHT_TYPE_SECTION, httpServletRequest);
			ObjectEntry seoStudioScanRunObjectEntry =
				fetchSEOStudioScanRunObjectEntry(httpServletRequest);
			JSONArray viewsJSONArray = fdsSerializer.serializeViews(
				SEOStudioFDSNames.INSIGHT_TYPE_SECTION, httpServletRequest);

			List<Long> seoStudioScanIds = Collections.emptyList();
			JSONObject seoStudioScanMetricJSONObject = null;

			if (seoStudioScanRunObjectEntry != null) {
				seoStudioScanIds = getSEOStudioScanIds(
					httpServletRequest, seoStudioScanRunObjectEntry.getId());
				seoStudioScanMetricJSONObject =
					fetchSEOStudioScanMetricJSONObject(
						httpServletRequest, "technical",
						seoStudioScanRunObjectEntry.getId());
			}

			return new ViewTechnicalDisplayContext(
				filtersJSONArray, httpServletRequest, language,
				seoStudioScanRunObjectEntry, seoStudioScanIds,
				seoStudioScanMetricJSONObject, viewsJSONArray);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}

			return null;
		}
	}

	@Override
	protected String getJSPPath() {
		return "/view_technical.jsp";
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ViewTechnicalFragmentRenderer.class);

}