/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.seo.studio.web.internal.object.action.executor.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.action.engine.ObjectActionEngine;
import com.liferay.object.constants.ObjectActionTriggerConstants;
import com.liferay.object.model.ObjectAction;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectActionLocalService;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.seo.studio.web.internal.test.BaseTestCase;

import java.io.Serializable;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Noor Najjar
 */
@FeatureFlag("LPD-44511")
@RunWith(Arquillian.class)
public class CalculateSEOStudioScanMetricsObjectActionExecutorTest
	extends BaseTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_seoStudioScanObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_SEO_STUDIO_SCAN", TestPropsValues.getCompanyId());
		_seoStudioScanRunObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_SEO_STUDIO_SCAN_RUN", TestPropsValues.getCompanyId());

		ObjectAction objectAction = _objectActionLocalService.fetchObjectAction(
			_seoStudioScanObjectDefinition.getObjectDefinitionId(),
			"calculateScanMetrics");

		objectAction.setActive(true);

		_objectActionLocalService.updateObjectAction(objectAction);
	}

	@Test
	public void testExecute() throws Exception {
		_addSEOStudioScanRunObjectEntry();

		ObjectEntry completedScanObjectEntry = _addSEOStudioScanObjectEntry(
			"crawler", "completed");

		ObjectEntry aeoReadinessHighInsightTypeObjectEntry =
			_addSEOStudioInsightTypeObjectEntry(
				"aeoReadiness", completedScanObjectEntry, "3");
		ObjectEntry contentStructureMediumInsightTypeObjectEntry =
			_addSEOStudioInsightTypeObjectEntry(
				"contentStructure", completedScanObjectEntry, "2");
		ObjectEntry imagesHighInsightTypeObjectEntry =
			_addSEOStudioInsightTypeObjectEntry(
				"images", completedScanObjectEntry, "3");
		ObjectEntry linksAndURLsMediumInsightTypeObjectEntry =
			_addSEOStudioInsightTypeObjectEntry(
				"linksAndURLs", completedScanObjectEntry, "2");
		ObjectEntry metadataHighInsightTypeObjectEntry =
			_addSEOStudioInsightTypeObjectEntry(
				"metadata", completedScanObjectEntry, "3");
		ObjectEntry metadataLowInsightTypeObjectEntry =
			_addSEOStudioInsightTypeObjectEntry(
				"metadata", completedScanObjectEntry, "1");

		ObjectEntry pageObjectEntry1 = _addSEOStudioPageObjectEntry(
			completedScanObjectEntry);
		ObjectEntry pageObjectEntry2 = _addSEOStudioPageObjectEntry(
			completedScanObjectEntry);
		ObjectEntry pageObjectEntry3 = _addSEOStudioPageObjectEntry(
			completedScanObjectEntry);

		_addSEOStudioScanInsightObjectEntry(
			aeoReadinessHighInsightTypeObjectEntry, pageObjectEntry1,
			completedScanObjectEntry);
		_addSEOStudioScanInsightObjectEntry(
			contentStructureMediumInsightTypeObjectEntry, pageObjectEntry3,
			completedScanObjectEntry);
		_addSEOStudioScanInsightObjectEntry(
			imagesHighInsightTypeObjectEntry, pageObjectEntry1,
			completedScanObjectEntry);
		_addSEOStudioScanInsightObjectEntry(
			linksAndURLsMediumInsightTypeObjectEntry, pageObjectEntry2,
			completedScanObjectEntry);
		_addSEOStudioScanInsightObjectEntry(
			metadataHighInsightTypeObjectEntry, pageObjectEntry1,
			completedScanObjectEntry);
		_addSEOStudioScanInsightObjectEntry(
			metadataHighInsightTypeObjectEntry, pageObjectEntry2,
			completedScanObjectEntry);
		_addSEOStudioScanInsightObjectEntry(
			metadataLowInsightTypeObjectEntry, pageObjectEntry1,
			completedScanObjectEntry);

		_executeObjectAction(completedScanObjectEntry);

		Map<String, Map<String, Serializable>> seoStudioScanMetricValuesMap =
			_getSEOStudioScanMetricValuesMap(
				_getSEOStudioScanMetricObjectEntries(
					_seoStudioScanRunObjectEntry));

		Map<String, Serializable> onPageValues =
			seoStudioScanMetricValuesMap.get("onPage");

		_assertSEOStudioScanMetricValues(3, 3, 5, onPageValues);

		Assert.assertEquals(
			5.0 / 3.0,
			MapUtil.getDouble(onPageValues, "averageInsightsPerAffectedPage"),
			0.001);

		JSONObject onPageCategoryBreakdownJSONObject =
			JSONFactoryUtil.createJSONObject(
				MapUtil.getString(onPageValues, "categoryBreakdown"));

		Assert.assertFalse(
			onPageCategoryBreakdownJSONObject.has("aeoReadiness"));
		Assert.assertEquals(
			1, onPageCategoryBreakdownJSONObject.getInt("contentStructure"));
		Assert.assertEquals(
			1, onPageCategoryBreakdownJSONObject.getInt("images"));
		Assert.assertEquals(
			3, onPageCategoryBreakdownJSONObject.getInt("metadata"));

		JSONObject onPageImpactMixJSONObject = JSONFactoryUtil.createJSONObject(
			MapUtil.getString(onPageValues, "impactMix"));

		JSONObject contentStructureImpactMixJSONObject =
			onPageImpactMixJSONObject.getJSONObject("contentStructure");

		Assert.assertEquals(1, contentStructureImpactMixJSONObject.getInt("2"));

		JSONObject imagesImpactMixJSONObject =
			onPageImpactMixJSONObject.getJSONObject("images");

		Assert.assertEquals(1, imagesImpactMixJSONObject.getInt("3"));

		JSONObject metadataImpactMixJSONObject =
			onPageImpactMixJSONObject.getJSONObject("metadata");

		Assert.assertEquals(1, metadataImpactMixJSONObject.getInt("1"));
		Assert.assertEquals(2, metadataImpactMixJSONObject.getInt("3"));

		Map<String, Serializable> technicalValues =
			seoStudioScanMetricValuesMap.get("technical");

		_assertSEOStudioScanMetricValues(1, 0, 1, technicalValues);

		Assert.assertEquals(
			1.0,
			MapUtil.getDouble(
				technicalValues, "averageInsightsPerAffectedPage"),
			0.001);

		JSONObject technicalCategoryBreakdownJSONObject =
			JSONFactoryUtil.createJSONObject(
				MapUtil.getString(technicalValues, "categoryBreakdown"));

		Assert.assertEquals(
			1, technicalCategoryBreakdownJSONObject.getInt("linksAndURLs"));

		JSONObject technicalImpactMixJSONObject =
			JSONFactoryUtil.createJSONObject(
				MapUtil.getString(technicalValues, "impactMix"));

		JSONObject linksAndURLsImpactMixJSONObject =
			technicalImpactMixJSONObject.getJSONObject("linksAndURLs");

		Assert.assertEquals(1, linksAndURLsImpactMixJSONObject.getInt("2"));
	}

	@Test
	public void testExecuteWithExistingScanMetric() throws Exception {
		_addSEOStudioScanRunObjectEntry();

		ObjectEntry completedScanObjectEntry = _addSEOStudioScanObjectEntry(
			"crawler", "completed");

		_addSEOStudioScanObjectEntry("pageSpeed", "completed");

		_executeObjectAction(completedScanObjectEntry);
		_executeObjectAction(completedScanObjectEntry);

		Assert.assertEquals(
			"completed", _getState(_seoStudioScanRunObjectEntry));

		List<ObjectEntry> seoStudioScanMetricObjectEntries =
			_getSEOStudioScanMetricObjectEntries(_seoStudioScanRunObjectEntry);

		Assert.assertEquals(
			seoStudioScanMetricObjectEntries.toString(), 2,
			seoStudioScanMetricObjectEntries.size());
	}

	@Test
	public void testExecuteWithFailedScan() throws Exception {
		_testExecute("failed", "cancelled");
		_testExecute("failed", "failed");
	}

	@Test
	public void testExecuteWithNoScanInsights() throws Exception {
		_addSEOStudioScanRunObjectEntry();

		ObjectEntry completedScanObjectEntry = _addSEOStudioScanObjectEntry(
			"crawler", "completed");

		_addSEOStudioScanObjectEntry("pageSpeed", "completed");

		_executeObjectAction(completedScanObjectEntry);

		Assert.assertEquals(
			"completed", _getState(_seoStudioScanRunObjectEntry));

		List<ObjectEntry> seoStudioScanMetricObjectEntries =
			_getSEOStudioScanMetricObjectEntries(_seoStudioScanRunObjectEntry);

		Assert.assertEquals(
			seoStudioScanMetricObjectEntries.toString(), 2,
			seoStudioScanMetricObjectEntries.size());

		Map<String, Map<String, Serializable>> seoStudioScanMetricValuesMap =
			_getSEOStudioScanMetricValuesMap(seoStudioScanMetricObjectEntries);

		_assertSEOStudioScanMetricValues(
			0, 0, 0, seoStudioScanMetricValuesMap.get("onPage"));
		_assertSEOStudioScanMetricValues(
			0, 0, 0, seoStudioScanMetricValuesMap.get("technical"));
	}

	@Test
	public void testExecuteWithRunningScan() throws Exception {
		_testExecute("running", "running");
	}

	private ObjectEntry _addSEOStudioInsightTypeObjectEntry(
			String category, ObjectEntry seoStudioScanObjectEntry,
			String severity)
		throws Exception {

		return addObjectEntry(
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_SEO_STUDIO_INSIGHT_TYPE",
					TestPropsValues.getCompanyId()),
			HashMapBuilder.<String, Serializable>put(
				"category", category
			).put(
				"name", "orphanPages"
			).put(
				"r_accountToSEOStudioInsightTypes_accountEntryId",
				accountEntry.getAccountEntryId()
			).put(
				"r_seoStudioScanToSEOStudioInsightTypes_seoStudioScanId",
				seoStudioScanObjectEntry.getObjectEntryId()
			).put(
				"severity", severity
			).build());
	}

	private ObjectEntry _addSEOStudioPageObjectEntry(
			ObjectEntry seoStudioScanObjectEntry)
		throws Exception {

		return addObjectEntry(
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_SEO_STUDIO_PAGE", TestPropsValues.getCompanyId()),
			HashMapBuilder.<String, Serializable>put(
				"pageURL", RandomTestUtil.randomString()
			).put(
				"r_accountToSEOStudioPages_accountEntryId",
				accountEntry.getAccountEntryId()
			).put(
				"r_seoStudioScanToSEOStudioPages_seoStudioScanId",
				seoStudioScanObjectEntry.getObjectEntryId()
			).build());
	}

	private ObjectEntry _addSEOStudioScanInsightObjectEntry(
			ObjectEntry seoStudioInsightTypeObjectEntry,
			ObjectEntry seoStudioPageObjectEntry,
			ObjectEntry seoStudioScanObjectEntry)
		throws Exception {

		return addObjectEntry(
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_SEO_STUDIO_SCAN_INSIGHT",
					TestPropsValues.getCompanyId()),
			HashMapBuilder.<String, Serializable>put(
				"classification", "problem"
			).put(
				"detectedDate", new Date()
			).put(
				"r_accountToSEOStudioScanInsights_accountEntryId",
				accountEntry.getAccountEntryId()
			).put(
				"r_seoStudioInsightTypeToScanInsights_seoStudioInsightTypeId",
				seoStudioInsightTypeObjectEntry.getObjectEntryId()
			).put(
				"r_seoStudioPageToSEOStudioScanInsights_seoStudioPageId",
				seoStudioPageObjectEntry.getObjectEntryId()
			).put(
				"r_seoStudioScanToSEOStudioScanInsights_seoStudioScanId",
				seoStudioScanObjectEntry.getObjectEntryId()
			).put(
				"state", RandomTestUtil.randomInt()
			).build());
	}

	private ObjectEntry _addSEOStudioScanObjectEntry(
			String scanType, String state)
		throws Exception {

		return addObjectEntry(
			_seoStudioScanObjectDefinition,
			HashMapBuilder.<String, Serializable>put(
				"r_accountToSEOStudioScans_accountEntryId",
				accountEntry.getAccountEntryId()
			).put(
				"r_seoStudioScanRunToSEOStudioScans_seoStudioScanRunId",
				_seoStudioScanRunObjectEntry.getObjectEntryId()
			).put(
				"scanRange", "full"
			).put(
				"scanScope", "entireDomain"
			).put(
				"scanType", scanType
			).put(
				"state", state
			).build());
	}

	private void _addSEOStudioScanRunObjectEntry() throws Exception {
		seoStudioDomainObjectEntry = addSEOStudioDomainObjectEntry(
			false, RandomTestUtil.randomString(), null);

		_seoStudioScanRunObjectEntry = addObjectEntry(
			_seoStudioScanRunObjectDefinition,
			HashMapBuilder.<String, Serializable>put(
				"name", RandomTestUtil.randomString()
			).put(
				"r_accountToSEOStudioScanRuns_accountEntryId",
				accountEntry.getAccountEntryId()
			).put(
				"r_seoStudioDomainToSEOStudioScanRuns_seoStudioDomainId",
				seoStudioDomainObjectEntry.getObjectEntryId()
			).put(
				"requestDate", new Date()
			).put(
				"state", "running"
			).put(
				"triggeredBy", "manual"
			).build());
	}

	private void _assertSEOStudioScanMetricValues(
		int expectedAffectedPagesCount, int expectedCriticalInsights,
		int expectedTotalInsights, Map<String, Serializable> values) {

		Assert.assertEquals(
			expectedAffectedPagesCount,
			MapUtil.getInteger(values, "affectedPagesCount"));
		Assert.assertEquals(
			expectedCriticalInsights,
			MapUtil.getInteger(values, "criticalInsights"));
		Assert.assertEquals(
			expectedTotalInsights, MapUtil.getInteger(values, "totalInsights"));
	}

	private void _executeObjectAction(ObjectEntry seoStudioScanObjectEntry)
		throws Exception {

		_objectActionEngine.executeObjectAction(
			"calculateScanMetrics",
			ObjectActionTriggerConstants.KEY_ON_AFTER_UPDATE,
			_seoStudioScanObjectDefinition.getObjectDefinitionId(),
			JSONUtil.put(
				"classPK", seoStudioScanObjectEntry.getObjectEntryId()
			).put(
				"objectEntry",
				HashMapBuilder.<String, Object>putAll(
					seoStudioScanObjectEntry.getModelAttributes()
				).put(
					"values", seoStudioScanObjectEntry.getValues()
				).build()
			),
			TestPropsValues.getUserId());
	}

	private List<ObjectEntry> _getSEOStudioScanMetricObjectEntries(
			ObjectEntry seoStudioScanRunObjectEntry)
		throws Exception {

		return getRelatedObjectEntries(
			seoStudioScanRunObjectEntry,
			"seoStudioScanRunToSEOStudioScanMetrics");
	}

	private Map<String, Map<String, Serializable>>
			_getSEOStudioScanMetricValuesMap(
				List<ObjectEntry> seoStudioScanMetricObjectEntries)
		throws Exception {

		Map<String, Map<String, Serializable>> seoStudioScanMetricValuesMap =
			new LinkedHashMap<>();

		for (ObjectEntry seoStudioScanMetricObjectEntry :
				seoStudioScanMetricObjectEntries) {

			Map<String, Serializable> values =
				objectEntryLocalService.getValues(
					seoStudioScanMetricObjectEntry.getObjectEntryId());

			seoStudioScanMetricValuesMap.put(
				MapUtil.getString(values, "scope"), values);
		}

		return seoStudioScanMetricValuesMap;
	}

	private String _getState(ObjectEntry objectEntry) throws Exception {
		return MapUtil.getString(
			objectEntryLocalService.getValues(objectEntry.getObjectEntryId()),
			"state");
	}

	private void _testExecute(
			String expectedSEOStudioScanRunState, String seoStudioScanState)
		throws Exception {

		_addSEOStudioScanRunObjectEntry();

		ObjectEntry completedScanObjectEntry = _addSEOStudioScanObjectEntry(
			"crawler", "completed");

		_addSEOStudioScanObjectEntry("pageSpeed", seoStudioScanState);

		_executeObjectAction(completedScanObjectEntry);

		Assert.assertEquals(
			expectedSEOStudioScanRunState,
			_getState(_seoStudioScanRunObjectEntry));
		Assert.assertTrue(
			ListUtil.isEmpty(
				_getSEOStudioScanMetricObjectEntries(
					_seoStudioScanRunObjectEntry)));
	}

	@Inject
	private ObjectActionEngine _objectActionEngine;

	@Inject
	private ObjectActionLocalService _objectActionLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	private ObjectDefinition _seoStudioScanObjectDefinition;
	private ObjectDefinition _seoStudioScanRunObjectDefinition;
	private ObjectEntry _seoStudioScanRunObjectEntry;

}