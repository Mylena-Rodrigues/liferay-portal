/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.rest.client.dto.v1_0;

import com.liferay.ai.hub.rest.client.function.UnsafeSupplier;
import com.liferay.ai.hub.rest.client.serdes.v1_0.ContentRetrieverSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * @author Feliphe Marinho
 * @generated
 */
@Generated("")
public class ContentRetriever implements Cloneable, Serializable {

	public static ContentRetriever toDTO(String json) {
		return ContentRetrieverSerDes.toDTO(json);
	}

	public Date getCrawlDate() {
		return crawlDate;
	}

	public void setCrawlDate(Date crawlDate) {
		this.crawlDate = crawlDate;
	}

	public void setCrawlDate(
		UnsafeSupplier<Date, Exception> crawlDateUnsafeSupplier) {

		try {
			crawlDate = crawlDateUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date crawlDate;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setDescription(
		UnsafeSupplier<String, Exception> descriptionUnsafeSupplier) {

		try {
			description = descriptionUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String description;

	public Map<String, String> getDescription_i18n() {
		return description_i18n;
	}

	public void setDescription_i18n(Map<String, String> description_i18n) {
		this.description_i18n = description_i18n;
	}

	public void setDescription_i18n(
		UnsafeSupplier<Map<String, String>, Exception>
			description_i18nUnsafeSupplier) {

		try {
			description_i18n = description_i18nUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, String> description_i18n;

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

	public String getIndexName() {
		return indexName;
	}

	public void setIndexName(String indexName) {
		this.indexName = indexName;
	}

	public void setIndexName(
		UnsafeSupplier<String, Exception> indexNameUnsafeSupplier) {

		try {
			indexName = indexNameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String indexName;

	public Integer getMaxCrawlDepth() {
		return maxCrawlDepth;
	}

	public void setMaxCrawlDepth(Integer maxCrawlDepth) {
		this.maxCrawlDepth = maxCrawlDepth;
	}

	public void setMaxCrawlDepth(
		UnsafeSupplier<Integer, Exception> maxCrawlDepthUnsafeSupplier) {

		try {
			maxCrawlDepth = maxCrawlDepthUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer maxCrawlDepth;

	public Integer getMaxDuration() {
		return maxDuration;
	}

	public void setMaxDuration(Integer maxDuration) {
		this.maxDuration = maxDuration;
	}

	public void setMaxDuration(
		UnsafeSupplier<Integer, Exception> maxDurationUnsafeSupplier) {

		try {
			maxDuration = maxDurationUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer maxDuration;

	public Integer getMaxLinksPerPage() {
		return maxLinksPerPage;
	}

	public void setMaxLinksPerPage(Integer maxLinksPerPage) {
		this.maxLinksPerPage = maxLinksPerPage;
	}

	public void setMaxLinksPerPage(
		UnsafeSupplier<Integer, Exception> maxLinksPerPageUnsafeSupplier) {

		try {
			maxLinksPerPage = maxLinksPerPageUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer maxLinksPerPage;

	public Integer getMaxUniqueURLsCount() {
		return maxUniqueURLsCount;
	}

	public void setMaxUniqueURLsCount(Integer maxUniqueURLsCount) {
		this.maxUniqueURLsCount = maxUniqueURLsCount;
	}

	public void setMaxUniqueURLsCount(
		UnsafeSupplier<Integer, Exception> maxUniqueURLsCountUnsafeSupplier) {

		try {
			maxUniqueURLsCount = maxUniqueURLsCountUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer maxUniqueURLsCount;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setTitle(
		UnsafeSupplier<String, Exception> titleUnsafeSupplier) {

		try {
			title = titleUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String title;

	public Map<String, String> getTitle_i18n() {
		return title_i18n;
	}

	public void setTitle_i18n(Map<String, String> title_i18n) {
		this.title_i18n = title_i18n;
	}

	public void setTitle_i18n(
		UnsafeSupplier<Map<String, String>, Exception>
			title_i18nUnsafeSupplier) {

		try {
			title_i18n = title_i18nUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, String> title_i18n;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setType(UnsafeSupplier<String, Exception> typeUnsafeSupplier) {
		try {
			type = typeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String type;

	@Override
	public ContentRetriever clone() throws CloneNotSupportedException {
		return (ContentRetriever)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ContentRetriever)) {
			return false;
		}

		ContentRetriever contentRetriever = (ContentRetriever)object;

		return Objects.equals(toString(), contentRetriever.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return ContentRetrieverSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:-1021476475