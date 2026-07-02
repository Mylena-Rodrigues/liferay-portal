/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.rest.client.dto.v1_0;

import com.liferay.ai.hub.rest.client.function.UnsafeSupplier;
import com.liferay.ai.hub.rest.client.serdes.v1_0.AIFeaturesSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Date;
import java.util.Objects;

/**
 * @author Feliphe Marinho
 * @generated
 */
@Generated("")
public class AIFeatures implements Cloneable, Serializable {

	public static AIFeatures toDTO(String json) {
		return AIFeaturesSerDes.toDTO(json);
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public void setComment(
		UnsafeSupplier<String, Exception> commentUnsafeSupplier) {

		try {
			comment = commentUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String comment;

	public Boolean getEnable() {
		return enable;
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}

	public void setEnable(
		UnsafeSupplier<Boolean, Exception> enableUnsafeSupplier) {

		try {
			enable = enableUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean enable;

	public String getLastModifiedBy() {
		return lastModifiedBy;
	}

	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	public void setLastModifiedBy(
		UnsafeSupplier<String, Exception> lastModifiedByUnsafeSupplier) {

		try {
			lastModifiedBy = lastModifiedByUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String lastModifiedBy;

	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	public void setLastModifiedDate(
		UnsafeSupplier<Date, Exception> lastModifiedDateUnsafeSupplier) {

		try {
			lastModifiedDate = lastModifiedDateUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date lastModifiedDate;

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public void setReason(
		UnsafeSupplier<String, Exception> reasonUnsafeSupplier) {

		try {
			reason = reasonUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String reason;

	@Override
	public AIFeatures clone() throws CloneNotSupportedException {
		return (AIFeatures)super.clone();
	}

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
		return AIFeaturesSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:451967767