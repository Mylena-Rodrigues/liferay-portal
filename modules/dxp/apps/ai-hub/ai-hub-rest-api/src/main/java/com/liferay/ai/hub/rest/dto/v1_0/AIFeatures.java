/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.rest.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import jakarta.annotation.Generated;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Feliphe Marinho
 * @generated
 */
@Generated("")
@GraphQLName("AIFeatures")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "AIFeatures")
public class AIFeatures implements Serializable {

	public static AIFeatures toDTO(String json) {
		return ObjectMapperUtil.readValue(AIFeatures.class, json);
	}

	public static AIFeatures unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(AIFeatures.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	public String getComment() {
		if (_commentSupplier != null) {
			comment = _commentSupplier.get();

			_commentSupplier = null;
		}

		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;

		_commentSupplier = null;
	}

	@JsonIgnore
	public void setComment(
		UnsafeSupplier<String, Exception> commentUnsafeSupplier) {

		_commentSupplier = () -> {
			try {
				return commentUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	protected String comment;

	@JsonIgnore
	private Supplier<String> _commentSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getEnable() {
		if (_enableSupplier != null) {
			enable = _enableSupplier.get();

			_enableSupplier = null;
		}

		return enable;
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;

		_enableSupplier = null;
	}

	@JsonIgnore
	public void setEnable(
		UnsafeSupplier<Boolean, Exception> enableUnsafeSupplier) {

		_enableSupplier = () -> {
			try {
				return enableUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean enable;

	@JsonIgnore
	private Supplier<Boolean> _enableSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getLastModifiedBy() {
		if (_lastModifiedBySupplier != null) {
			lastModifiedBy = _lastModifiedBySupplier.get();

			_lastModifiedBySupplier = null;
		}

		return lastModifiedBy;
	}

	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;

		_lastModifiedBySupplier = null;
	}

	@JsonIgnore
	public void setLastModifiedBy(
		UnsafeSupplier<String, Exception> lastModifiedByUnsafeSupplier) {

		_lastModifiedBySupplier = () -> {
			try {
				return lastModifiedByUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String lastModifiedBy;

	@JsonIgnore
	private Supplier<String> _lastModifiedBySupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Date getLastModifiedDate() {
		if (_lastModifiedDateSupplier != null) {
			lastModifiedDate = _lastModifiedDateSupplier.get();

			_lastModifiedDateSupplier = null;
		}

		return lastModifiedDate;
	}

	public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;

		_lastModifiedDateSupplier = null;
	}

	@JsonIgnore
	public void setLastModifiedDate(
		UnsafeSupplier<Date, Exception> lastModifiedDateUnsafeSupplier) {

		_lastModifiedDateSupplier = () -> {
			try {
				return lastModifiedDateUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Date lastModifiedDate;

	@JsonIgnore
	private Supplier<Date> _lastModifiedDateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getReason() {
		if (_reasonSupplier != null) {
			reason = _reasonSupplier.get();

			_reasonSupplier = null;
		}

		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;

		_reasonSupplier = null;
	}

	@JsonIgnore
	public void setReason(
		UnsafeSupplier<String, Exception> reasonUnsafeSupplier) {

		_reasonSupplier = () -> {
			try {
				return reasonUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	protected String reason;

	@JsonIgnore
	private Supplier<String> _reasonSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof AIFeatures)) {
			return false;
		}

		AIFeatures aiFeatures = (AIFeatures)object;

		return Objects.equals(toString(), aiFeatures.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");

		String comment = getComment();

		if (comment != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"comment\": ");

			sb.append("\"");

			sb.append(_escape(comment));

			sb.append("\"");
		}

		Boolean enable = getEnable();

		if (enable != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"enable\": ");

			sb.append(enable);
		}

		String lastModifiedBy = getLastModifiedBy();

		if (lastModifiedBy != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"lastModifiedBy\": ");

			sb.append("\"");

			sb.append(_escape(lastModifiedBy));

			sb.append("\"");
		}

		Date lastModifiedDate = getLastModifiedDate();

		if (lastModifiedDate != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"lastModifiedDate\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(lastModifiedDate));

			sb.append("\"");
		}

		String reason = getReason();

		if (reason != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"reason\": ");

			sb.append("\"");

			sb.append(_escape(reason));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.ai.hub.rest.dto.v1_0.AIFeatures",
		name = "x-class-name"
	)
	public String xClassName;

	private static String _escape(Object object) {
		return StringUtil.replace(
			String.valueOf(object), _JSON_ESCAPE_STRINGS[0],
			_JSON_ESCAPE_STRINGS[1]);
	}

	private static boolean _isArray(Object value) {
		if (value == null) {
			return false;
		}

		Class<?> clazz = value.getClass();

		return clazz.isArray();
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
			sb.append(_escape(entry.getKey()));
			sb.append("\": ");

			Object value = entry.getValue();

			if (_isArray(value)) {
				sb.append("[");

				Object[] valueArray = (Object[])value;

				for (int i = 0; i < valueArray.length; i++) {
					if (valueArray[i] instanceof Map) {
						sb.append(_toJSON((Map<String, ?>)valueArray[i]));
					}
					else if (valueArray[i] instanceof String) {
						sb.append("\"");
						sb.append(valueArray[i]);
						sb.append("\"");
					}
					else {
						sb.append(valueArray[i]);
					}

					if ((i + 1) < valueArray.length) {
						sb.append(", ");
					}
				}

				sb.append("]");
			}
			else if (value instanceof Map) {
				sb.append(_toJSON((Map<String, ?>)value));
			}
			else if (value instanceof String) {
				sb.append("\"");
				sb.append(_escape(value));
				sb.append("\"");
			}
			else {
				sb.append(value);
			}

			if (iterator.hasNext()) {
				sb.append(", ");
			}
		}

		sb.append("}");

		return sb.toString();
	}

	private static final String[][] _JSON_ESCAPE_STRINGS = {
		{"\\", "\"", "\b", "\f", "\n", "\r", "\t"},
		{"\\\\", "\\\"", "\\b", "\\f", "\\n", "\\r", "\\t"}
	};

	private Map<String, Serializable> _extendedProperties;

}
// LIFERAY-REST-BUILDER-HASH:-705075971