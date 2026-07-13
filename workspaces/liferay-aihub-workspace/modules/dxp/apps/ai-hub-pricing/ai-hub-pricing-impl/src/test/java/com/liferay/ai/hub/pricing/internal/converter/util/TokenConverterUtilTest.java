/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.pricing.internal.converter.util;

import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Iliyan Peychev
 */
public class TokenConverterUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testConvertBytesToTokens() {
		Assert.assertEquals(
			0, TokenConverterUtil.convertBytesToTokens(Long.MIN_VALUE));
		Assert.assertEquals(0, TokenConverterUtil.convertBytesToTokens(-1));
		Assert.assertEquals(0, TokenConverterUtil.convertBytesToTokens(0));
		Assert.assertEquals(2, TokenConverterUtil.convertBytesToTokens(10));
		Assert.assertEquals(4, TokenConverterUtil.convertBytesToTokens(15));
		Assert.assertEquals(
			240000, TokenConverterUtil.convertBytesToTokens(1000000));
		Assert.assertEquals(
			2400000000L, TokenConverterUtil.convertBytesToTokens(10000000000L));
	}

}