/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.rest.manager.v1_0;

import com.liferay.ai.hub.rest.dto.v1_0.ContentRetrieverConfiguration;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

/**
 * @author Carolina Barbosa
 */
public interface ContentRetrieverConfigurationManager {

	public void deleteContentRetrieverConfiguration(
			long companyId, String contentRetrieverExternalReferenceCode,
			String externalReferenceCode)
		throws Exception;

	public Page<ContentRetrieverConfiguration>
			getContentRetrieverConfigurations(
				long companyId, String contentRetrieverExternalReferenceCode,
				DTOConverterContext dtoConverterContext, String filterString,
				Pagination pagination, String search)
		throws Exception;

	public ContentRetrieverConfiguration putContentRetrieverConfiguration(
			long companyId,
			ContentRetrieverConfiguration contentRetrieverConfiguration,
			String contentRetrieverExternalReferenceCode,
			DTOConverterContext dtoConverterContext,
			String externalReferenceCode)
		throws Exception;

}