/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.rest.internal.manager.v1_0;

import com.liferay.ai.hub.rest.dto.v1_0.ContentRetrieverConfiguration;
import com.liferay.ai.hub.rest.manager.v1_0.ContentRetrieverConfigurationManager;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.object.rest.manager.v1_0.DefaultObjectEntryManager;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManager;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carolina Barbosa
 */
@Component(service = ContentRetrieverConfigurationManager.class)
public class ContentRetrieverConfigurationManagerImpl
	implements ContentRetrieverConfigurationManager {

	@Override
	public void deleteContentRetrieverConfiguration(
			long companyId, String contentRetrieverExternalReferenceCode,
			String externalReferenceCode)
		throws Exception {

		DefaultObjectEntryManager defaultObjectEntryManager =
			(DefaultObjectEntryManager)_objectEntryManager;

		defaultObjectEntryManager.deleteRelatedObjectEntry(
			externalReferenceCode,
			_objectRelationshipLocalService.getObjectRelationship(
				_getContentRetrieverObjectDefinitionId(companyId),
				"contentRetrieverToCRConfigurations"),
			contentRetrieverExternalReferenceCode, null);
	}

	@Override
	public Page<ContentRetrieverConfiguration>
			getContentRetrieverConfigurations(
				long companyId, String contentRetrieverExternalReferenceCode,
				DTOConverterContext dtoConverterContext, String filterString,
				Pagination pagination, String search)
		throws Exception {

		DefaultObjectEntryManager defaultObjectEntryManager =
			(DefaultObjectEntryManager)_objectEntryManager;

		Page<ObjectEntry> objectEntriesPage =
			defaultObjectEntryManager.getRelatedObjectEntries(
				null, dtoConverterContext,
				contentRetrieverExternalReferenceCode, filterString,
				_objectRelationshipLocalService.getObjectRelationship(
					_getContentRetrieverObjectDefinitionId(companyId),
					"contentRetrieverToCRConfigurations"),
				pagination, null, search, null);

		return Page.of(
			TransformUtil.transform(
				objectEntriesPage.getItems(),
				this::_toContentRetrieverConfiguration),
			pagination, objectEntriesPage.getTotalCount());
	}

	@Override
	public ContentRetrieverConfiguration putContentRetrieverConfiguration(
			long companyId,
			ContentRetrieverConfiguration contentRetrieverConfiguration,
			String contentRetrieverExternalReferenceCode,
			DTOConverterContext dtoConverterContext,
			String externalReferenceCode)
		throws Exception {

		DefaultObjectEntryManager defaultObjectEntryManager =
			(DefaultObjectEntryManager)_objectEntryManager;

		ObjectEntry objectEntry = defaultObjectEntryManager.fetchObjectEntry(
			dtoConverterContext, externalReferenceCode,
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_AI_HUB_CONTENT_RETRIEVER_CONFIGURATION", companyId),
			null);

		if (objectEntry != null) {
			return _toContentRetrieverConfiguration(
				defaultObjectEntryManager.updateRelatedObjectEntry(
					dtoConverterContext, externalReferenceCode,
					_toObjectEntry(
						contentRetrieverConfiguration, externalReferenceCode),
					_objectRelationshipLocalService.getObjectRelationship(
						_getContentRetrieverObjectDefinitionId(companyId),
						"contentRetrieverToCRConfigurations"),
					contentRetrieverExternalReferenceCode, null));
		}

		return _toContentRetrieverConfiguration(
			defaultObjectEntryManager.addRelatedObjectEntry(
				dtoConverterContext, contentRetrieverExternalReferenceCode,
				_toObjectEntry(
					contentRetrieverConfiguration, externalReferenceCode),
				_objectRelationshipLocalService.getObjectRelationship(
					_getContentRetrieverObjectDefinitionId(companyId),
					"contentRetrieverToCRConfigurations"),
				null));
	}

	private long _getContentRetrieverObjectDefinitionId(long companyId)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_AI_HUB_CONTENT_RETRIEVER", companyId);

		return objectDefinition.getObjectDefinitionId();
	}

	private ContentRetrieverConfiguration _toContentRetrieverConfiguration(
		ObjectEntry objectEntry) {

		return new ContentRetrieverConfiguration() {
			{
				setDomain(
					() -> GetterUtil.getString(
						objectEntry.getPropertyValue("domain")));
				setExcludePaths(
					() -> GetterUtil.getString(
						objectEntry.getPropertyValue("excludePaths")));
				setExternalReferenceCode(objectEntry::getExternalReferenceCode);
				setIncludePaths(
					() -> GetterUtil.getString(
						objectEntry.getPropertyValue("includePaths")));
				setSeedUrls(
					() -> GetterUtil.getString(
						objectEntry.getPropertyValue("seedUrls")));
			}
		};
	}

	private ObjectEntry _toObjectEntry(
		ContentRetrieverConfiguration contentRetrieverConfiguration,
		String contentRetrieverConfigurationExternalReferenceCode) {

		return new ObjectEntry() {
			{
				setExternalReferenceCode(
					() -> contentRetrieverConfigurationExternalReferenceCode);
				setProperties(
					() -> HashMapBuilder.<String, Object>put(
						"domain", contentRetrieverConfiguration.getDomain()
					).put(
						"excludePaths",
						contentRetrieverConfiguration.getExcludePaths()
					).put(
						"includePaths",
						contentRetrieverConfiguration.getIncludePaths()
					).put(
						"seedUrls", contentRetrieverConfiguration.getSeedUrls()
					).build());
			}
		};
	}

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference(target = "(object.entry.manager.storage.type=default)")
	private ObjectEntryManager _objectEntryManager;

	@Reference
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

}