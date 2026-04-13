/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.cell.security;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Rafael Praxedes
 */
@ProviderType
public interface JWTToken {

	public String generateToken(
		long expirationTime, String issuer, long userId);

	public long getUserId(String token);

}