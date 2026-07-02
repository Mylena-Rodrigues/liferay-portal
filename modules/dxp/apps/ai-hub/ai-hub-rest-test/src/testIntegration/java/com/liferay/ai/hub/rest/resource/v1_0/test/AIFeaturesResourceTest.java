/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.rest.resource.v1_0.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountEntryUserRelLocalService;
import com.liferay.ai.hub.rest.dto.v1_0.AIFeatures;
import com.liferay.ai.hub.rest.manager.v1_0.AIFeaturesManager;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.site.initializer.SiteInitializer;
import com.liferay.site.initializer.SiteInitializerRegistry;

import jakarta.ws.rs.BadRequestException;

import java.util.Map;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Mylena Monte
 */
@FeatureFlag("LPD-62272")
@RunWith(Arquillian.class)
public class AIFeaturesResourceTest extends BaseAIFeaturesResourceTestCase {

	@BeforeClass
	public static void setUpClass() throws Exception {
		_accountEntry = _accountEntryLocalService.addAccountEntry(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			AccountConstants.PARENT_ACCOUNT_ENTRY_ID_DEFAULT,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			RandomTestUtil.randomString() + "@liferay.com", null,
			RandomTestUtil.randomString(),
			AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS,
			WorkflowConstants.STATUS_APPROVED,
			ServiceContextTestUtil.getServiceContext());

		_accountEntryUserRelLocalService.addAccountEntryUserRel(
			_accountEntry.getAccountEntryId(), TestPropsValues.getUserId());

		_dtoConverterContext = new DefaultDTOConverterContext(
			false, Map.of(), _dtoConverterRegistry, null,
			LocaleUtil.getDefault(), null, TestPropsValues.getUser());

		_originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(TestPropsValues.getUser()));

		_originalName = PrincipalThreadLocal.getName();

		PrincipalThreadLocal.setName(TestPropsValues.getUserId());

		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId(), TestPropsValues.getUserId()));

		SiteInitializer siteInitializer =
			_siteInitializerRegistry.getSiteInitializer(
				"com.liferay.ai.hub.site.initializer");

		siteInitializer.initialize(TestPropsValues.getGroupId());

		_aiHubAccountEntry =
			_accountEntryLocalService.getAccountEntryByExternalReferenceCode(
				"L_AI_HUB", TestPropsValues.getCompanyId());

		_accountEntryUserRelLocalService.addAccountEntryUserRel(
			_aiHubAccountEntry.getAccountEntryId(),
			TestPropsValues.getUserId());
	}

	@AfterClass
	public static void tearDownClass() {
		PermissionThreadLocal.setPermissionChecker(_originalPermissionChecker);

		PrincipalThreadLocal.setName(_originalName);

		ServiceContextThreadLocal.popServiceContext();
	}

	@Override
	@Test
	public void testGetAIFeatures() throws Exception {
		_aiFeaturesManager.patchAIFeatures(
			true, TestPropsValues.getCompanyId(), _dtoConverterContext,
			RandomTestUtil.randomString(), RandomTestUtil.randomString());

		AIFeatures aiFeatures = _aiFeaturesManager.getAIFeatures(
			TestPropsValues.getCompanyId(), _dtoConverterContext);

		Assert.assertTrue(aiFeatures.getEnable());
	}

	@Override
	@Test
	public void testPatchAIFeatures() throws Exception {
		_aiFeaturesManager.patchAIFeatures(
			false, TestPropsValues.getCompanyId(), _dtoConverterContext,
			RandomTestUtil.randomString(), RandomTestUtil.randomString());

		AIFeatures disabledAIFeatures = _aiFeaturesManager.getAIFeatures(
			TestPropsValues.getCompanyId(), _dtoConverterContext);

		Assert.assertFalse(disabledAIFeatures.getEnable());

		_aiFeaturesManager.patchAIFeatures(
			true, TestPropsValues.getCompanyId(), _dtoConverterContext,
			RandomTestUtil.randomString(), RandomTestUtil.randomString());

		AIFeatures enabledAIFeatures = _aiFeaturesManager.getAIFeatures(
			TestPropsValues.getCompanyId(), _dtoConverterContext);

		Assert.assertTrue(enabledAIFeatures.getEnable());
	}

	@Test(expected = BadRequestException.class)
	public void testPatchAIFeaturesRequiresReason() throws Exception {
		_aiFeaturesManager.patchAIFeatures(
			false, TestPropsValues.getCompanyId(), _dtoConverterContext, null,
			null);
	}

	private static AccountEntry _accountEntry;

	@Inject
	private static AccountEntryLocalService _accountEntryLocalService;

	@Inject
	private static AccountEntryUserRelLocalService
		_accountEntryUserRelLocalService;

	private static AccountEntry _aiHubAccountEntry;
	private static DTOConverterContext _dtoConverterContext;

	@Inject
	private static DTOConverterRegistry _dtoConverterRegistry;

	private static String _originalName;
	private static PermissionChecker _originalPermissionChecker;

	@Inject
	private static SiteInitializerRegistry _siteInitializerRegistry;

	@Inject
	private AIFeaturesManager _aiFeaturesManager;

}