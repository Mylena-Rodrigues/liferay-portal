/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.pricing.internal.object.action.executor;

import com.liferay.account.model.AccountEntryUserRel;
import com.liferay.account.service.AccountEntryUserRelLocalService;
import com.liferay.ai.hub.pricing.internal.converter.util.TokenConverterUtil;
import com.liferay.ai.hub.quota.QuotaManager;
import com.liferay.ai.hub.quota.Source;
import com.liferay.ai.hub.quota.Usage;
import com.liferay.object.action.executor.BaseObjectActionExecutor;
import com.liferay.object.action.executor.ObjectActionExecutor;
import com.liferay.portal.kernel.exception.NoSuchUserException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alberto Sousa
 */
@Component(service = ObjectActionExecutor.class)
public class UpdateEmbeddingTokenUsageObjectActionExecutorImpl
	extends BaseObjectActionExecutor {

	@Override
	public String getKey() {
		return "update-embedding-token-usage";
	}

	@Override
	protected void doExecute(
			long companyId, long objectActionId,
			UnicodeProperties parametersUnicodeProperties,
			JSONObject payloadJSONObject, long userId)
		throws Exception {

		Map<String, Object> objectEntryMap =
			(Map<String, Object>)payloadJSONObject.get("objectEntry");

		Map<String, Object> valuesMap = (Map<String, Object>)objectEntryMap.get(
			"values");

		_quotaManager.updateUsage(
			companyId,
			Usage.builder(
			).source(
				Source.EMBEDDING
			).tokenCount(
				TokenConverterUtil.convertBytesToTokens(
					MapUtil.getLong(valuesMap, "indexedDocumentBytes"))
			).build(),
			_getUserId(
				MapUtil.getLong(
					valuesMap, "r_accountToAIHubCrawlerJobs_accountEntryId")));
	}

	private long _getUserId(long accountEntryId) throws Exception {
		for (AccountEntryUserRel accountEntryUserRel :
				_accountEntryUserRelLocalService.
					getAccountEntryUserRelsByAccountEntryId(accountEntryId)) {

			User user = accountEntryUserRel.getUser();

			if (user.isServiceAccountUser()) {
				return user.getUserId();
			}
		}

		throw new NoSuchUserException(
			"No service account user found for account entry " +
				accountEntryId);
	}

	@Reference
	private AccountEntryUserRelLocalService _accountEntryUserRelLocalService;

	@Reference
	private QuotaManager _quotaManager;

}