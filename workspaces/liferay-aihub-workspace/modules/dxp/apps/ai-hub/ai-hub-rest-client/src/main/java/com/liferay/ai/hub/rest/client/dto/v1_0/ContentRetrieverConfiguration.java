/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.rest.client.dto.v1_0;

import com.liferay.ai.hub.rest.client.function.UnsafeSupplier;
import com.liferay.ai.hub.rest.client.serdes.v1_0.ContentRetrieverConfigurationSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Feliphe Marinho
 * @generated
 */
@Generated("")
public class ContentRetrieverConfiguration implements Cloneable, Serializable {

	public static ContentRetrieverConfiguration toDTO(String json) {
		return ContentRetrieverConfigurationSerDes.toDTO(json);
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public void setDomain(
		UnsafeSupplier<String, Exception> domainUnsafeSupplier) {

		try {
			domain = domainUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String domain;

	public String getExcludePaths() {
		return excludePaths;
	}

	public void setExcludePaths(String excludePaths) {
		this.excludePaths = excludePaths;
	}

	public void setExcludePaths(
		UnsafeSupplier<String, Exception> excludePathsUnsafeSupplier) {

		try {
			excludePaths = excludePathsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String excludePaths;

	public String getExternalReferenceCode() {
		return externalReferenceCode;
	}

	public void setExternalReferenceCode(String externalReferenceCode) {
		this.externalReferenceCode = externalReferenceCode;
	}

	public void setExternalReferenceCode(
		UnsafeSupplier<String, Exception> externalReferenceCodeUnsafeSupplier) {

		try {
			externalReferenceCode = externalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String externalReferenceCode;

	public String getIncludePaths() {
		return includePaths;
	}

	public void setIncludePaths(String includePaths) {
		this.includePaths = includePaths;
	}

	public void setIncludePaths(
		UnsafeSupplier<String, Exception> includePathsUnsafeSupplier) {

		try {
			includePaths = includePathsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String includePaths;

	public String getSeedUrls() {
		return seedUrls;
	}

	public void setSeedUrls(String seedUrls) {
		this.seedUrls = seedUrls;
	}

	public void setSeedUrls(
		UnsafeSupplier<String, Exception> seedUrlsUnsafeSupplier) {

		try {
			seedUrls = seedUrlsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String seedUrls;

	@Override
	public ContentRetrieverConfiguration clone()
		throws CloneNotSupportedException {

		return (ContentRetrieverConfiguration)super.clone();
	}

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
		return ContentRetrieverConfigurationSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:1391244264