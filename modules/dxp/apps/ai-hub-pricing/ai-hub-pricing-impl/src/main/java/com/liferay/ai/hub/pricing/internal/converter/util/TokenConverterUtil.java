/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.pricing.internal.converter.util;

import com.liferay.ai.hub.quota.Source;
import com.liferay.portal.kernel.util.MapUtil;

import java.io.Serializable;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.util.Map;

/**
 * @author Carolina Barbosa
 */
public class TokenConverterUtil {

	public static BigDecimal convertLRTToToken(
		Map<String, Serializable> conversionTableValues, BigDecimal lrtCount,
		Source source) {

		return lrtCount.multiply(
			_getTokensPerLRT(conversionTableValues, source));
	}

	public static BigDecimal convertTokenToLRT(
		Map<String, Serializable> conversionTableValues, Source source,
		BigDecimal tokenCount) {

		return tokenCount.divide(
			_getTokensPerLRT(conversionTableValues, source), 20,
			RoundingMode.HALF_DOWN);
	}

	private static BigDecimal _getTokensPerLRT(
		Map<String, Serializable> conversionTableValues, Source source) {

		SourceConverter sourceConverter = SourceConverter.valueOf(
			source.name());

		return BigDecimal.valueOf(
			MapUtil.getLong(
				conversionTableValues, sourceConverter.getObjectFieldName()));
	}

	private enum SourceConverter {

		EMBEDDING("embeddingTokensPerLRT"),
		MODEL_ARMOR("modelArmorTokensPerLRT"),
		VERTEX_INPUT("vertexInputTokensPerLRT"),
		VERTEX_OUTPUT("vertexOutputTokensPerLRT");

		public String getObjectFieldName() {
			return _objectFieldName;
		}

		private SourceConverter(String objectFieldName) {
			_objectFieldName = objectFieldName;
		}

		private final String _objectFieldName;

	}

}