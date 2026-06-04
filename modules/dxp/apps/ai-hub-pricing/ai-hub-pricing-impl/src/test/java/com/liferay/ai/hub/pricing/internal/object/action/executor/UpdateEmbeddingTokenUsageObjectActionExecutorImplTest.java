/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.pricing.internal.object.action.executor;

import com.liferay.account.model.AccountEntryUserRel;
import com.liferay.account.service.AccountEntryUserRelLocalService;
import com.liferay.ai.hub.quota.QuotaManager;
import com.liferay.ai.hub.quota.Source;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Collections;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Alberto Sousa
 */
public class UpdateEmbeddingTokenUsageObjectActionExecutorImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_setUpAccountEntryUserRelLocalService();
		_setUpQuotaManager();
		_setUpUser();
	}

	@Test
	public void testDoExecute() throws Exception {
		_updateEmbeddingTokenUsageObjectActionExecutorImpl.doExecute(
			_COMPANY_ID, _OBJECT_ACTION_ID, new UnicodeProperties(),
			_createPayloadJSONObject(0), _USER_ID);

		_verifyUpdateUsage(0);

		_updateEmbeddingTokenUsageObjectActionExecutorImpl.doExecute(
			_COMPANY_ID, _OBJECT_ACTION_ID, new UnicodeProperties(),
			_createPayloadJSONObject(1000000), _USER_ID);

		_verifyUpdateUsage(240000);
	}

	private JSONObject _createPayloadJSONObject(long indexedDocumentBytes) {
		return JSONUtil.put(
			"objectEntry",
			JSONUtil.put(
				"values",
				JSONUtil.put(
					"indexedDocumentBytes", indexedDocumentBytes
				).put(
					"r_accountToAIHubCrawlerJobs_accountEntryId",
					_ACCOUNT_ENTRY_ID
				)));
	}

	private void _setUpAccountEntryUserRelLocalService() throws Exception {
		AccountEntryUserRel accountEntryUserRel = Mockito.mock(
			AccountEntryUserRel.class);

		Mockito.when(
			accountEntryUserRel.getUser()
		).thenReturn(
			_user
		);

		Mockito.when(
			_accountEntryUserRelLocalService.
				getAccountEntryUserRelsByAccountEntryId(_ACCOUNT_ENTRY_ID)
		).thenReturn(
			Collections.singletonList(accountEntryUserRel)
		);

		ReflectionTestUtil.setFieldValue(
			_updateEmbeddingTokenUsageObjectActionExecutorImpl,
			"_accountEntryUserRelLocalService",
			_accountEntryUserRelLocalService);
	}

	private void _setUpQuotaManager() {
		ReflectionTestUtil.setFieldValue(
			_updateEmbeddingTokenUsageObjectActionExecutorImpl, "_quotaManager",
			_quotaManager);
	}

	private void _setUpUser() {
		Mockito.when(
			_user.getUserId()
		).thenReturn(
			_SERVICE_ACCOUNT_USER_ID
		);

		Mockito.when(
			_user.isServiceAccountUser()
		).thenReturn(
			true
		);
	}

	private void _verifyUpdateUsage(long expectedTokenCount) throws Exception {
		Mockito.verify(
			_quotaManager
		).updateUsage(
			Mockito.eq(_COMPANY_ID),
			Mockito.argThat(
				usage ->
					(usage.getSource() == Source.EMBEDDING) &&
					(usage.getTokenCount() == expectedTokenCount)),
			Mockito.eq(_SERVICE_ACCOUNT_USER_ID)
		);
	}

	private static final long _ACCOUNT_ENTRY_ID = RandomTestUtil.randomLong();

	private static final long _COMPANY_ID = RandomTestUtil.randomLong();

	private static final long _OBJECT_ACTION_ID = RandomTestUtil.randomLong();

	private static final long _SERVICE_ACCOUNT_USER_ID =
		RandomTestUtil.randomLong();

	private static final long _USER_ID = RandomTestUtil.randomLong();

	private final AccountEntryUserRelLocalService
		_accountEntryUserRelLocalService = Mockito.mock(
			AccountEntryUserRelLocalService.class);
	private final QuotaManager _quotaManager = Mockito.mock(QuotaManager.class);
	private final UpdateEmbeddingTokenUsageObjectActionExecutorImpl
		_updateEmbeddingTokenUsageObjectActionExecutorImpl =
			new UpdateEmbeddingTokenUsageObjectActionExecutorImpl();
	private final User _user = Mockito.mock(User.class);

}