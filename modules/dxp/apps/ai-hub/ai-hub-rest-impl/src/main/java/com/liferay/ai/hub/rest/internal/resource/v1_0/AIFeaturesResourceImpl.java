/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.rest.internal.resource.v1_0;

import com.liferay.ai.hub.rest.dto.v1_0.AIFeatures;
import com.liferay.ai.hub.rest.manager.v1_0.AIFeaturesManager;
import com.liferay.ai.hub.rest.resource.v1_0.AIFeaturesResource;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;

import jakarta.ws.rs.BadRequestException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Mylena Monte
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/ai-features.properties",
	scope = ServiceScope.PROTOTYPE, service = AIFeaturesResource.class
)
public class AIFeaturesResourceImpl extends BaseAIFeaturesResourceImpl {

	@Override
	public AIFeatures getAIFeatures() throws Exception {
		if (!FeatureFlagManagerUtil.isEnabled(
				contextCompany.getCompanyId(), "LPD-62272")) {

			throw new UnsupportedOperationException();
		}

		return _aiFeaturesManager.getAIFeatures(
			contextCompany.getCompanyId(), _createDTOConverterContext());
	}

	@Override
	public AIFeatures patchAIFeatures(AIFeatures aiFeatures) throws Exception {
		if (!FeatureFlagManagerUtil.isEnabled(
				contextCompany.getCompanyId(), "LPD-62272")) {

			throw new UnsupportedOperationException();
		}

		if (aiFeatures.getEnable() == null) {
			throw new BadRequestException(
				"A value for enable is required to update AI features");
		}

		return _aiFeaturesManager.patchAIFeatures(
			aiFeatures.getEnable(), contextCompany.getCompanyId(),
			_createDTOConverterContext(), aiFeatures.getReason(),
			aiFeatures.getComment());
	}

	private DTOConverterContext _createDTOConverterContext() {
		return new DefaultDTOConverterContext(
			contextAcceptLanguage.isAcceptAllLanguages(), null,
			_dtoConverterRegistry, contextHttpServletRequest, null,
			contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
			contextUser);
	}

	@Reference
	private AIFeaturesManager _aiFeaturesManager;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

}