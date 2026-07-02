/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.web.internal.display.context;

import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.PortletRequest;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

/**
 * @author Mylena Monte
 */
public class AIFeaturesDisplayContext {

	public AIFeaturesDisplayContext(
		HttpServletRequest httpServletRequest, Portal portal) {

		_httpServletRequest = httpServletRequest;
		_portal = portal;
	}

	public Map<String, Object> getReactData() {
		return HashMapBuilder.<String, Object>put(
			"auditURL", _getAuditURL()
		).build();
	}

	private String _getAuditURL() {
		return String.valueOf(
			_portal.getControlPanelPortletURL(
				_httpServletRequest, _AUDIT_PORTLET_NAME,
				PortletRequest.RENDER_PHASE));
	}

	private static final String _AUDIT_PORTLET_NAME =
		"com_liferay_portal_security_audit_web_portlet_AuditPortlet";

	private final HttpServletRequest _httpServletRequest;
	private final Portal _portal;

}
