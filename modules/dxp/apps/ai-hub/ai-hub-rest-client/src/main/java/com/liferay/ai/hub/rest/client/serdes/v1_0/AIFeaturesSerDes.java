/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.rest.client.serdes.v1_0;

import com.liferay.ai.hub.rest.client.dto.v1_0.AIFeatures;
import com.liferay.ai.hub.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

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
public class AIFeaturesSerDes {

	public static AIFeatures toDTO(String json) {
		AIFeaturesJSONParser aiFeaturesJSONParser = new AIFeaturesJSONParser();

		return aiFeaturesJSONParser.parseToDTO(json);
	}

	public static AIFeatures[] toDTOs(String json) {
		AIFeaturesJSONParser aiFeaturesJSONParser = new AIFeaturesJSONParser();

		return aiFeaturesJSONParser.parseToDTOs(json);
	}

	public static String toJSON(AIFeatures aiFeatures) {
		if (aiFeatures == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (aiFeatures.getComment() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"comment\": ");

			sb.append("\"");

			sb.append(_escape(aiFeatures.getComment()));

			sb.append("\"");
		}

		if (aiFeatures.getEnable() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"enable\": ");

			sb.append(aiFeatures.getEnable());
		}

		if (aiFeatures.getLastModifiedBy() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"lastModifiedBy\": ");

			sb.append("\"");

			sb.append(_escape(aiFeatures.getLastModifiedBy()));

			sb.append("\"");
		}

		if (aiFeatures.getLastModifiedDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"lastModifiedDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					aiFeatures.getLastModifiedDate()));

			sb.append("\"");
		}

		if (aiFeatures.getReason() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"reason\": ");

			sb.append("\"");

			sb.append(_escape(aiFeatures.getReason()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		AIFeaturesJSONParser aiFeaturesJSONParser = new AIFeaturesJSONParser();

		return aiFeaturesJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(AIFeatures aiFeatures) {
		if (aiFeatures == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (aiFeatures.getComment() == null) {
			map.put("comment", null);
		}
		else {
			map.put("comment", String.valueOf(aiFeatures.getComment()));
		}

		if (aiFeatures.getEnable() == null) {
			map.put("enable", null);
		}
		else {
			map.put("enable", String.valueOf(aiFeatures.getEnable()));
		}

		if (aiFeatures.getLastModifiedBy() == null) {
			map.put("lastModifiedBy", null);
		}
		else {
			map.put(
				"lastModifiedBy",
				String.valueOf(aiFeatures.getLastModifiedBy()));
		}

		if (aiFeatures.getLastModifiedDate() == null) {
			map.put("lastModifiedDate", null);
		}
		else {
			map.put(
				"lastModifiedDate",
				liferayToJSONDateFormat.format(
					aiFeatures.getLastModifiedDate()));
		}

		if (aiFeatures.getReason() == null) {
			map.put("reason", null);
		}
		else {
			map.put("reason", String.valueOf(aiFeatures.getReason()));
		}

		return map;
	}

	public static class AIFeaturesJSONParser
		extends BaseJSONParser<AIFeatures> {

		@Override
		protected AIFeatures createDTO() {
			return new AIFeatures();
		}

		@Override
		protected AIFeatures[] createDTOArray(int size) {
			return new AIFeatures[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "comment")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "enable")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "lastModifiedBy")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "lastModifiedDate")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "reason")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			AIFeatures aiFeatures, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "comment")) {
				if (jsonParserFieldValue != null) {
					aiFeatures.setComment((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "enable")) {
				if (jsonParserFieldValue != null) {
					aiFeatures.setEnable((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "lastModifiedBy")) {
				if (jsonParserFieldValue != null) {
					aiFeatures.setLastModifiedBy((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "lastModifiedDate")) {
				if (jsonParserFieldValue != null) {
					aiFeatures.setLastModifiedDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "reason")) {
				if (jsonParserFieldValue != null) {
					aiFeatures.setReason((String)jsonParserFieldValue);
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
// LIFERAY-REST-BUILDER-HASH:-1829425673