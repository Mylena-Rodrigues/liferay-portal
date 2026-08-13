/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.rest.client.serdes.v1_0;

import com.liferay.ai.hub.rest.client.dto.v1_0.ContentRetrieverConfiguration;
import com.liferay.ai.hub.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Feliphe Marinho
 * @generated
 */
@Generated("")
public class ContentRetrieverConfigurationSerDes {

	public static ContentRetrieverConfiguration toDTO(String json) {
		ContentRetrieverConfigurationJSONParser
			contentRetrieverConfigurationJSONParser =
				new ContentRetrieverConfigurationJSONParser();

		return contentRetrieverConfigurationJSONParser.parseToDTO(json);
	}

	public static ContentRetrieverConfiguration[] toDTOs(String json) {
		ContentRetrieverConfigurationJSONParser
			contentRetrieverConfigurationJSONParser =
				new ContentRetrieverConfigurationJSONParser();

		return contentRetrieverConfigurationJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		ContentRetrieverConfiguration contentRetrieverConfiguration) {

		if (contentRetrieverConfiguration == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (contentRetrieverConfiguration.getDomain() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"domain\": ");

			sb.append("\"");

			sb.append(_escape(contentRetrieverConfiguration.getDomain()));

			sb.append("\"");
		}

		if (contentRetrieverConfiguration.getExcludePaths() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"excludePaths\": ");

			sb.append("\"");

			sb.append(_escape(contentRetrieverConfiguration.getExcludePaths()));

			sb.append("\"");
		}

		if (contentRetrieverConfiguration.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(
					contentRetrieverConfiguration.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (contentRetrieverConfiguration.getIncludePaths() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"includePaths\": ");

			sb.append("\"");

			sb.append(_escape(contentRetrieverConfiguration.getIncludePaths()));

			sb.append("\"");
		}

		if (contentRetrieverConfiguration.getSeedUrls() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"seedUrls\": ");

			sb.append("\"");

			sb.append(_escape(contentRetrieverConfiguration.getSeedUrls()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ContentRetrieverConfigurationJSONParser
			contentRetrieverConfigurationJSONParser =
				new ContentRetrieverConfigurationJSONParser();

		return contentRetrieverConfigurationJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		ContentRetrieverConfiguration contentRetrieverConfiguration) {

		if (contentRetrieverConfiguration == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (contentRetrieverConfiguration.getDomain() == null) {
			map.put("domain", null);
		}
		else {
			map.put(
				"domain",
				String.valueOf(contentRetrieverConfiguration.getDomain()));
		}

		if (contentRetrieverConfiguration.getExcludePaths() == null) {
			map.put("excludePaths", null);
		}
		else {
			map.put(
				"excludePaths",
				String.valueOf(
					contentRetrieverConfiguration.getExcludePaths()));
		}

		if (contentRetrieverConfiguration.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(
					contentRetrieverConfiguration.getExternalReferenceCode()));
		}

		if (contentRetrieverConfiguration.getIncludePaths() == null) {
			map.put("includePaths", null);
		}
		else {
			map.put(
				"includePaths",
				String.valueOf(
					contentRetrieverConfiguration.getIncludePaths()));
		}

		if (contentRetrieverConfiguration.getSeedUrls() == null) {
			map.put("seedUrls", null);
		}
		else {
			map.put(
				"seedUrls",
				String.valueOf(contentRetrieverConfiguration.getSeedUrls()));
		}

		return map;
	}

	public static class ContentRetrieverConfigurationJSONParser
		extends BaseJSONParser<ContentRetrieverConfiguration> {

		@Override
		protected ContentRetrieverConfiguration createDTO() {
			return new ContentRetrieverConfiguration();
		}

		@Override
		protected ContentRetrieverConfiguration[] createDTOArray(int size) {
			return new ContentRetrieverConfiguration[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "domain")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "excludePaths")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "includePaths")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "seedUrls")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ContentRetrieverConfiguration contentRetrieverConfiguration,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "domain")) {
				if (jsonParserFieldValue != null) {
					contentRetrieverConfiguration.setDomain(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "excludePaths")) {
				if (jsonParserFieldValue != null) {
					contentRetrieverConfiguration.setExcludePaths(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					contentRetrieverConfiguration.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "includePaths")) {
				if (jsonParserFieldValue != null) {
					contentRetrieverConfiguration.setIncludePaths(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "seedUrls")) {
				if (jsonParserFieldValue != null) {
					contentRetrieverConfiguration.setSeedUrls(
						(String)jsonParserFieldValue);
				}
			}
		}

	}

	private static String _escape(Object object) {
		String string = String.valueOf(object);

		for (String[] strings : BaseJSONParser.JSON_ESCAPE_STRINGS) {
			string = string.replace(strings[0], strings[1]);
		}

		return string;
	}

	private static String _toJSON(Map<String, ?> map) {
		StringBuilder sb = new StringBuilder("{");

		@SuppressWarnings("unchecked")
		Set set = map.entrySet();

		@SuppressWarnings("unchecked")
		Iterator<Map.Entry<String, ?>> iterator = set.iterator();

		while (iterator.hasNext()) {
			Map.Entry<String, ?> entry = iterator.next();

			sb.append("\"");
			sb.append(entry.getKey());
			sb.append("\": ");

			Object value = entry.getValue();

			sb.append(_toJSON(value));

			if (iterator.hasNext()) {
				sb.append(", ");
			}
		}

		sb.append("}");

		return sb.toString();
	}

	private static String _toJSON(Object value) {
		if (value == null) {
			return "null";
		}

		if (value instanceof Map) {
			return _toJSON((Map)value);
		}

		Class<?> clazz = value.getClass();

		if (clazz.isArray()) {
			StringBuilder sb = new StringBuilder("[");

			Object[] values = (Object[])value;

			for (int i = 0; i < values.length; i++) {
				sb.append(_toJSON(values[i]));

				if ((i + 1) < values.length) {
					sb.append(", ");
				}
			}

			sb.append("]");

			return sb.toString();
		}

		if (value instanceof String) {
			return "\"" + _escape(value) + "\"";
		}

		return String.valueOf(value);
	}

}
// LIFERAY-REST-BUILDER-HASH:-1204199500