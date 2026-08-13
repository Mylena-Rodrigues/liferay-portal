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
@GraphQLName("ContentRetrieverConfiguration")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "ContentRetrieverConfiguration")
public class ContentRetrieverConfiguration implements Serializable {

	public static ContentRetrieverConfiguration toDTO(String json) {
		return ObjectMapperUtil.readValue(
			ContentRetrieverConfiguration.class, json);
	}

	public static ContentRetrieverConfiguration unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			ContentRetrieverConfiguration.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	public String getDomain() {
		if (_domainSupplier != null) {
			domain = _domainSupplier.get();

			_domainSupplier = null;
		}

		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;

		_domainSupplier = null;
	}

	@JsonIgnore
	public void setDomain(
		UnsafeSupplier<String, Exception> domainUnsafeSupplier) {

		_domainSupplier = () -> {
			try {
				return domainUnsafeSupplier.get();
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
	protected String domain;

	@JsonIgnore
	private Supplier<String> _domainSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getExcludePaths() {
		if (_excludePathsSupplier != null) {
			excludePaths = _excludePathsSupplier.get();

			_excludePathsSupplier = null;
		}

		return excludePaths;
	}

	public void setExcludePaths(String excludePaths) {
		this.excludePaths = excludePaths;

		_excludePathsSupplier = null;
	}

	@JsonIgnore
	public void setExcludePaths(
		UnsafeSupplier<String, Exception> excludePathsUnsafeSupplier) {

		_excludePathsSupplier = () -> {
			try {
				return excludePathsUnsafeSupplier.get();
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
	protected String excludePaths;

	@JsonIgnore
	private Supplier<String> _excludePathsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getExternalReferenceCode() {
		if (_externalReferenceCodeSupplier != null) {
			externalReferenceCode = _externalReferenceCodeSupplier.get();

			_externalReferenceCodeSupplier = null;
		}

		return externalReferenceCode;
	}

	public void setExternalReferenceCode(String externalReferenceCode) {
		this.externalReferenceCode = externalReferenceCode;

		_externalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setExternalReferenceCode(
		UnsafeSupplier<String, Exception> externalReferenceCodeUnsafeSupplier) {

		_externalReferenceCodeSupplier = () -> {
			try {
				return externalReferenceCodeUnsafeSupplier.get();
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
	protected String externalReferenceCode;

	@JsonIgnore
	private Supplier<String> _externalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getIncludePaths() {
		if (_includePathsSupplier != null) {
			includePaths = _includePathsSupplier.get();

			_includePathsSupplier = null;
		}

		return includePaths;
	}

	public void setIncludePaths(String includePaths) {
		this.includePaths = includePaths;

		_includePathsSupplier = null;
	}

	@JsonIgnore
	public void setIncludePaths(
		UnsafeSupplier<String, Exception> includePathsUnsafeSupplier) {

		_includePathsSupplier = () -> {
			try {
				return includePathsUnsafeSupplier.get();
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
	protected String includePaths;

	@JsonIgnore
	private Supplier<String> _includePathsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getSeedUrls() {
		if (_seedUrlsSupplier != null) {
			seedUrls = _seedUrlsSupplier.get();

			_seedUrlsSupplier = null;
		}

		return seedUrls;
	}

	public void setSeedUrls(String seedUrls) {
		this.seedUrls = seedUrls;

		_seedUrlsSupplier = null;
	}

	@JsonIgnore
	public void setSeedUrls(
		UnsafeSupplier<String, Exception> seedUrlsUnsafeSupplier) {

		_seedUrlsSupplier = () -> {
			try {
				return seedUrlsUnsafeSupplier.get();
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
	protected String seedUrls;

	@JsonIgnore
	private Supplier<String> _seedUrlsSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ContentRetrieverConfiguration)) {
			return false;
		}

		ContentRetrieverConfiguration contentRetrieverConfiguration =
			(ContentRetrieverConfiguration)object;

		return Objects.equals(
			toString(), contentRetrieverConfiguration.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		String domain = getDomain();

		if (domain != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"domain\": ");

			sb.append("\"");

			sb.append(_escape(domain));

			sb.append("\"");
		}

		String excludePaths = getExcludePaths();

		if (excludePaths != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"excludePaths\": ");

			sb.append("\"");

			sb.append(_escape(excludePaths));

			sb.append("\"");
		}

		String externalReferenceCode = getExternalReferenceCode();

		if (externalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(externalReferenceCode));

			sb.append("\"");
		}

		String includePaths = getIncludePaths();

		if (includePaths != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"includePaths\": ");

			sb.append("\"");

			sb.append(_escape(includePaths));

			sb.append("\"");
		}

		String seedUrls = getSeedUrls();

		if (seedUrls != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"seedUrls\": ");

			sb.append("\"");

			sb.append(_escape(seedUrls));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.ai.hub.rest.dto.v1_0.ContentRetrieverConfiguration",
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
// LIFERAY-REST-BUILDER-HASH:-1219503884