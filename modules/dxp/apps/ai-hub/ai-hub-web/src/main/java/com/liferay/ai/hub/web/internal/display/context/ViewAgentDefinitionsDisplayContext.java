/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.web.internal.display.context;

import com.liferay.account.model.AccountEntry;
import com.liferay.ai.hub.agent.AIFeaturesStateResolver;
import com.liferay.ai.hub.util.AccountEntryUtil;
import com.liferay.ai.hub.web.internal.util.DisplayContextUtil;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

/**
 * @author Feliphe Marinho
 * @author João Victor Alves
 */
public class ViewAgentDefinitionsDisplayContext {

	public ViewAgentDefinitionsDisplayContext(
		AIFeaturesStateResolver aiFeaturesStateResolver,
		GroupLocalService groupLocalService,
		HttpServletRequest httpServletRequest) {

		_aiFeaturesStateResolver = aiFeaturesStateResolver;
		_groupLocalService = groupLocalService;
		_httpServletRequest = httpServletRequest;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public String getAPIURL() {
		return "/o/ai-hub/v1.0/agent-definitions";
	}

	public CreationMenu getCreationMenu() throws Exception {
		return CreationMenuBuilder.addDropdownItem(
			dropdownItem -> {
				dropdownItem.setHref(_getAgentDefinitionURL());
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "new-agent"));
			}
		).build();
	}

	public List<FDSActionDropdownItem> getFDSActionDropdownItems()
		throws Exception {

		String href =
			getAPIURL() + "/by-external-reference-code/{externalReferenceCode}";

		Map<String, Object> activeVisibilityFilters = null;

		if (!_isAIFeaturesEnabled()) {
			activeVisibilityFilters = HashMapBuilder.<String, Object>put(
				"system", false
			).build();
		}

		return List.of(
			new FDSActionDropdownItem(
				HttpComponentsUtil.addParameters(
					_getAgentDefinitionURL(), "externalReferenceCode",
					"{externalReferenceCode}", "workflowDefinitionName",
					"{workflowDefinitionName}"),
				"view", "view", LanguageUtil.get(_httpServletRequest, "view"),
				"get", null, null),
			new FDSActionDropdownItem(
				href + "/copy", "copy", "copy",
				LanguageUtil.get(_httpServletRequest, "duplicate"), "post",
				"copy", "async"),
			new FDSActionDropdownItem(
				href, "trash", "delete",
				LanguageUtil.get(_httpServletRequest, "delete"), "delete",
				"delete", "async"),
			new FDSActionDropdownItem(
				href + "/update-active?active=false", "block", "deactivate",
				LanguageUtil.get(_httpServletRequest, "deactivate"), "patch",
				"deactivate", "async", activeVisibilityFilters),
			new FDSActionDropdownItem(
				href + "/update-active?active=true", "logout", "activate",
				LanguageUtil.get(_httpServletRequest, "activate"), "patch",
				"activate", "async", activeVisibilityFilters),
			new FDSActionDropdownItem(
				DisplayContextUtil.getPermissionsURL(
					"L_AI_HUB_AGENT_DEFINITION", _httpServletRequest),
				"password-policies", "permissions",
				LanguageUtil.get(_httpServletRequest, "permissions"), "get",
				"permissions", "modal-permissions"));
	}

	private String _getAgentDefinitionURL() throws Exception {
		Company company = _themeDisplay.getCompany();
		Group group = _groupLocalService.getGroup(
			_themeDisplay.getScopeGroupId());

		return StringBundler.concat(
			company.getPortalURL(GroupConstants.DEFAULT_PARENT_GROUP_ID),
			"/web", group.getFriendlyURL(), "/agent");
	}

	private boolean _isAIFeaturesEnabled() throws Exception {
		AccountEntry accountEntry = AccountEntryUtil.getUserAccountEntry(
			_themeDisplay.getUserId());

		if (accountEntry == null) {
			return true;
		}

		return _aiFeaturesStateResolver.isEnabled(
			_themeDisplay.getCompanyId(), accountEntry.getAccountEntryId());
	}

	private final AIFeaturesStateResolver _aiFeaturesStateResolver;
	private final GroupLocalService _groupLocalService;
	private final HttpServletRequest _httpServletRequest;
	private final ThemeDisplay _themeDisplay;

}