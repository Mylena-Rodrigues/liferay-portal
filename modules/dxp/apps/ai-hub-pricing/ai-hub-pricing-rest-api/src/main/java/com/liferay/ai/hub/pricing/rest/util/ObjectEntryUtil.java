/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.pricing.rest.util;

import com.liferay.account.model.AccountEntry;
import com.liferay.ai.hub.util.AccountEntryUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.object.rest.manager.v1_0.DefaultObjectEntryManager;
import com.liferay.object.rest.manager.v1_0.DefaultObjectEntryManagerProvider;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManagerRegistry;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.object.service.ObjectRelationshipLocalServiceUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import java.util.Collections;
import java.util.List;

/**
 * @author Carolina Barbosa
 */
public class ObjectEntryUtil {

	public static long getObjectEntriesCount(
		long companyId, DTOConverterRegistry dtoConverterRegistry,
		String objectDefinitionExternalReferenceCode,
		ObjectEntryManagerRegistry objectEntryManagerRegistry, long userId) {

		try {
			ObjectDefinition objectDefinition =
				ObjectDefinitionLocalServiceUtil.
					fetchObjectDefinitionByExternalReferenceCode(
						objectDefinitionExternalReferenceCode, companyId);

			DefaultObjectEntryManager defaultObjectEntryManager =
				DefaultObjectEntryManagerProvider.provide(
					objectEntryManagerRegistry.getObjectEntryManager(
						objectDefinition.getCompanyId(),
						objectDefinition.getStorageType()));

			User user = UserLocalServiceUtil.getUser(userId);

			Page<ObjectEntry> objectEntriesPage =
				defaultObjectEntryManager.getObjectEntries(
					companyId, objectDefinition, null, null,
					new DefaultDTOConverterContext(
						dtoConverterRegistry, null, user.getLocale(), null,
						user),
					(String)null, Pagination.of(1, 1), null, null);

			return objectEntriesPage.getTotalCount();
		}
		catch (Exception exception) {
			_log.error(exception);

			return 0;
		}
	}

	public static List<ObjectEntry> getQuotaBlockObjectEntries(
		long companyId, DTOConverterRegistry dtoConverterRegistry,
		ObjectEntryManagerRegistry objectEntryManagerRegistry, long userId) {

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();
		String name = PrincipalThreadLocal.getName();

		try {
			AccountEntry accountEntry = AccountEntryUtil.getUserAccountEntry(
				userId);

			if (accountEntry == null) {
				return Collections.emptyList();
			}

			User user = UserLocalServiceUtil.getUser(userId);

			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(user));
			PrincipalThreadLocal.setName(user.getUserId());

			ObjectDefinition quotaObjectDefinition =
				ObjectDefinitionLocalServiceUtil.
					getObjectDefinitionByExternalReferenceCode(
						"L_AI_HUB_QUOTA", companyId);

			DefaultObjectEntryManager defaultObjectEntryManager =
				DefaultObjectEntryManagerProvider.provide(
					objectEntryManagerRegistry.getObjectEntryManager(
						quotaObjectDefinition.getCompanyId(),
						quotaObjectDefinition.getStorageType()));

			Page<ObjectEntry> quotaBlockObjectEntriesPage =
				defaultObjectEntryManager.getRelatedObjectEntries(
					null,
					new DefaultDTOConverterContext(
						dtoConverterRegistry, null, user.getLocale(), null,
						user),
					"quota-" + accountEntry.getAccountEntryId(),
					StringBundler.concat(
						"purchaseExpirationDate ge ",
						Instant.now(
						).truncatedTo(
							ChronoUnit.SECONDS
						),
						" and remainingBalance gt 0"),
					ObjectRelationshipLocalServiceUtil.getObjectRelationship(
						quotaObjectDefinition.getObjectDefinitionId(),
						"aiHubQuotaToAIHubQuotaBlocks"),
					Pagination.of(QueryUtil.ALL_POS, QueryUtil.ALL_POS), null,
					null,
					new Sort[] {
						new Sort("purchaseDate", Sort.LONG_TYPE, false)
					});

			return ListUtil.fromCollection(
				quotaBlockObjectEntriesPage.getItems());
		}
		catch (Exception exception) {
			_log.error(exception);
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(permissionChecker);
			PrincipalThreadLocal.setName(name);
		}

		return Collections.emptyList();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ObjectEntryUtil.class);

}