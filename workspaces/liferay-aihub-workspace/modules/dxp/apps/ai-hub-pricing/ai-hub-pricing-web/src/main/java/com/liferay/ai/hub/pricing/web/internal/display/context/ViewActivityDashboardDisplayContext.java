/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.pricing.web.internal.display.context;

import com.liferay.ai.hub.pricing.rest.util.ObjectEntryUtil;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManagerRegistry;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;

import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;

import java.util.List;
import java.util.Map;

/**
 * @author Igor Franca
 */
public class ViewActivityDashboardDisplayContext {

	public ViewActivityDashboardDisplayContext(
		DTOConverterRegistry dtoConverterRegistry,
		HttpServletRequest httpServletRequest,
		ObjectEntryManagerRegistry objectEntryManagerRegistry) {

		_dtoConverterRegistry = dtoConverterRegistry;
		_objectEntryManagerRegistry = objectEntryManagerRegistry;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public Map<String, Object> getProps() {
		List<ObjectEntry> quotaBlockObjectEntries =
			ObjectEntryUtil.getQuotaBlockObjectEntries(
				_themeDisplay.getCompanyId(), _dtoConverterRegistry,
				_objectEntryManagerRegistry, _themeDisplay.getUserId());

		return HashMapBuilder.<String, Object>put(
			"agentsCount",
			ObjectEntryUtil.getObjectEntriesCount(
				_themeDisplay.getCompanyId(), _dtoConverterRegistry,
				"L_AI_HUB_AGENT_DEFINITION", _objectEntryManagerRegistry,
				_themeDisplay.getUserId())
		).put(
			"chatbotsCount",
			ObjectEntryUtil.getObjectEntriesCount(
				_themeDisplay.getCompanyId(), _dtoConverterRegistry,
				"L_AI_HUB_CHATBOT", _objectEntryManagerRegistry,
				_themeDisplay.getUserId())
		).put(
			"expiresAt",
			() -> {
				if (ListUtil.isEmpty(quotaBlockObjectEntries)) {
					return StringPool.BLANK;
				}

				ObjectEntry quotaBlockObjectEntry = quotaBlockObjectEntries.get(
					0);

				return MapUtil.getString(
					quotaBlockObjectEntry.getProperties(),
					"purchaseExpirationDate");
			}
		).put(
			"totalLRT",
			() -> {
				BigDecimal totalLRT = BigDecimal.ZERO;

				for (ObjectEntry quotaBlockObjectEntry :
						quotaBlockObjectEntries) {

					Map<String, Object> properties =
						quotaBlockObjectEntry.getProperties();

					totalLRT = totalLRT.add(
						(BigDecimal)properties.get("remainingBalance"));
				}

				return totalLRT;
			}
		).build();
	}

	private final DTOConverterRegistry _dtoConverterRegistry;
	private final ObjectEntryManagerRegistry _objectEntryManagerRegistry;
	private final ThemeDisplay _themeDisplay;

}