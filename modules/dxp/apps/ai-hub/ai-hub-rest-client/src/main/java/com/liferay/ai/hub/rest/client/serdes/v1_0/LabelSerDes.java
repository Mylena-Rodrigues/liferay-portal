/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.rest.client.serdes.v1_0;

import com.liferay.ai.hub.rest.client.dto.v1_0.Label;
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
public class LabelSerDes {

	public static Label toDTO(String json) {
		LabelJSONParser labelJSONParser = new LabelJSONParser();

		return labelJSONParser.parseToDTO(json);
	}

	public static Label[] toDTOs(String json) {
		LabelJSONParser labelJSONParser = new LabelJSONParser();

		return labelJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Label label) {
		if (label == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (label.getCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"code\": ");

			sb.append(label.getCode());
		}

		if (label.getValue() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"value\": ");

			sb.append("\"");

			sb.append(_escape(label.getValue()));

			sb.append("\"");
		}

		if (label.getValue_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"value_i18n\": ");

			sb.append("\"");

			sb.append(_escape(label.getValue_i18n()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		LabelJSONParser labelJSONParser = new LabelJSONParser();

		return labelJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Label label) {
		if (label == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (label.getCode() == null) {
			map.put("code", null);
		}
		else {
			map.put("code", String.valueOf(label.getCode()));
		}

		if (label.getValue() == null) {
			map.put("value", null);
		}
		else {
			map.put("value", String.valueOf(label.getValue()));
		}

		if (label.getValue_i18n() == null) {
			map.put("value_i18n", null);
		}
		else {
			map.put("value_i18n", String.valueOf(label.getValue_i18n()));
		}

		return map;
	}

	public static class LabelJSONParser extends BaseJSONParser<Label> {

		@Override
		protected Label createDTO() {
			return new Label();
		}

		@Override
		protected Label[] createDTOArray(int size) {
			return new Label[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "code")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "value")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "value_i18n")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Label label, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "code")) {
				if (jsonParserFieldValue != null) {
					label.setCode(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "value")) {
				if (jsonParserFieldValue != null) {
					label.setValue((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "value_i18n")) {
				if (jsonParserFieldValue != null) {
					label.setValue_i18n((String)jsonParserFieldValue);
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