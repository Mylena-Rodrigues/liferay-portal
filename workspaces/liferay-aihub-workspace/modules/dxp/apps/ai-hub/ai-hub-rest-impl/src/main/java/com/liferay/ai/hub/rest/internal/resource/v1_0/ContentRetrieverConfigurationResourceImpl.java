/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.rest.internal.resource.v1_0;

import com.liferay.ai.hub.rest.dto.v1_0.ContentRetrieverConfiguration;
import com.liferay.ai.hub.rest.manager.v1_0.ContentRetrieverConfigurationManager;
import com.liferay.ai.hub.rest.resource.v1_0.ContentRetrieverConfigurationResource;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Carolina Barbosa
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/content-retriever-configuration.properties",
	scope = ServiceScope.PROTOTYPE,
	service = ContentRetrieverConfigurationResource.class
)
public class ContentRetrieverConfigurationResourceImpl
	extends BaseContentRetrieverConfigurationResourceImpl {

	@Override
	public void
			deleteContentRetrieverByExternalReferenceCodeContentRetrieverConfiguration(
				String externalReferenceCode,
				String contentRetrieverConfigurationExternalReferenceCode)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled(
				contextCompany.getCompanyId(), "LPD-62272")) {

			throw new UnsupportedOperationException();
		}

		_contentRetrieverConfigurationManager.
			deleteContentRetrieverConfiguration(
				contextCompany.getCompanyId(), externalReferenceCode,
				contentRetrieverConfigurationExternalReferenceCode);
	}

	@Override
	public Page<ContentRetrieverConfiguration>
			getContentRetrieverByExternalReferenceCodeContentRetrieverConfigurationsPage(
				String externalReferenceCode, String search, Filter filter,
				Pagination pagination)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled(
				contextCompany.getCompanyId(), "LPD-62272")) {

			throw new UnsupportedOperationException();
		}

		return _contentRetrieverConfigurationManager.
			getContentRetrieverConfigurations(
				contextCompany.getCompanyId(), externalReferenceCode,
				new DefaultDTOConverterContext(
					contextAcceptLanguage.isAcceptAllLanguages(), null,
					_dtoConverterRegistry, contextHttpServletRequest, null,
					contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
					contextUser),
				ParamUtil.getString(contextHttpServletRequest, "filter"),
				pagination, search);
	}

	@Override
	public ContentRetrieverConfiguration
			putContentRetrieverByExternalReferenceCodeContentRetrieverConfiguration(
				String externalReferenceCode,
				String contentRetrieverConfigurationExternalReferenceCode,
				ContentRetrieverConfiguration contentRetrieverConfiguration)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled(
				contextCompany.getCompanyId(), "LPD-62272")) {

			throw new UnsupportedOperationException();
		}

		return _contentRetrieverConfigurationManager.
			putContentRetrieverConfiguration(
				contextCompany.getCompanyId(), contentRetrieverConfiguration,
				externalReferenceCode,
				new DefaultDTOConverterContext(
					contextAcceptLanguage.isAcceptAllLanguages(), null,
					_dtoConverterRegistry, contextHttpServletRequest, null,
					contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
					contextUser),
				contentRetrieverConfigurationExternalReferenceCode);
	}

	@Reference
	private ContentRetrieverConfigurationManager
		_contentRetrieverConfigurationManager;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

}