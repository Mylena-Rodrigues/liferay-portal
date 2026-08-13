/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.rest.resource.v1_0.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import com.liferay.ai.hub.rest.client.dto.v1_0.ContentRetrieverConfiguration;
import com.liferay.ai.hub.rest.client.http.HttpInvoker;
import com.liferay.ai.hub.rest.client.pagination.Page;
import com.liferay.ai.hub.rest.client.pagination.Pagination;
import com.liferay.ai.hub.rest.client.resource.v1_0.ContentRetrieverConfigurationResource;
import com.liferay.ai.hub.rest.client.serdes.v1_0.ContentRetrieverConfigurationSerDes;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.vulcan.resource.EntityModelResource;

import jakarta.annotation.Generated;

import jakarta.ws.rs.core.MultivaluedHashMap;

import java.lang.reflect.Method;

import java.text.Format;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Feliphe Marinho
 * @generated
 */
@Generated("")
public abstract class BaseContentRetrieverConfigurationResourceTestCase {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		_format = FastDateFormatFactoryUtil.getSimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");
	}

	@Before
	public void setUp() throws Exception {
		irrelevantGroup = GroupTestUtil.addGroup();
		testGroup = GroupTestUtil.addGroup();

		testCompany = CompanyLocalServiceUtil.getCompany(
			testGroup.getCompanyId());

		_contentRetrieverConfigurationResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		contentRetrieverConfigurationResource =
			ContentRetrieverConfigurationResource.builder(
			).authentication(
				_testCompanyAdminUser.getEmailAddress(),
				PropsValues.DEFAULT_ADMIN_PASSWORD
			).endpoint(
				testCompany.getVirtualHostname(),
				PortalUtil.getPortalServerPort(false), "http"
			).locale(
				LocaleUtil.getDefault()
			).build();
	}

	@After
	public void tearDown() throws Exception {
		GroupTestUtil.deleteGroup(irrelevantGroup);
		GroupTestUtil.deleteGroup(testGroup);
	}

	@Test
	public void testClientSerDesToDTO() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		ContentRetrieverConfiguration contentRetrieverConfiguration1 =
			randomContentRetrieverConfiguration();

		String json = objectMapper.writeValueAsString(
			contentRetrieverConfiguration1);

		ContentRetrieverConfiguration contentRetrieverConfiguration2 =
			ContentRetrieverConfigurationSerDes.toDTO(json);

		Assert.assertTrue(
			equals(
				contentRetrieverConfiguration1,
				contentRetrieverConfiguration2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		ContentRetrieverConfiguration contentRetrieverConfiguration =
			randomContentRetrieverConfiguration();

		String json1 = objectMapper.writeValueAsString(
			contentRetrieverConfiguration);
		String json2 = ContentRetrieverConfigurationSerDes.toJSON(
			contentRetrieverConfiguration);

		Assert.assertEquals(
			objectMapper.readTree(json1), objectMapper.readTree(json2));
	}

	protected ObjectMapper getClientSerDesObjectMapper() {
		return new ObjectMapper() {
			{
				configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
				configure(
					SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
				enable(SerializationFeature.INDENT_OUTPUT);
				setDateFormat(new ISO8601DateFormat());
				setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
				setSerializationInclusion(JsonInclude.Include.NON_NULL);
				setVisibility(
					PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
				setVisibility(
					PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
			}
		};
	}

	@Test
	public void testEscapeRegexInStringFields() throws Exception {
		String regex = "^[0-9]+(\\.[0-9]{1,2})\"?";

		ContentRetrieverConfiguration contentRetrieverConfiguration =
			randomContentRetrieverConfiguration();

		contentRetrieverConfiguration.setDomain(regex);
		contentRetrieverConfiguration.setExcludePaths(regex);
		contentRetrieverConfiguration.setExternalReferenceCode(regex);
		contentRetrieverConfiguration.setIncludePaths(regex);
		contentRetrieverConfiguration.setSeedUrls(regex);

		String json = ContentRetrieverConfigurationSerDes.toJSON(
			contentRetrieverConfiguration);

		Assert.assertFalse(json.contains(regex));

		contentRetrieverConfiguration =
			ContentRetrieverConfigurationSerDes.toDTO(json);

		Assert.assertEquals(regex, contentRetrieverConfiguration.getDomain());
		Assert.assertEquals(
			regex, contentRetrieverConfiguration.getExcludePaths());
		Assert.assertEquals(
			regex, contentRetrieverConfiguration.getExternalReferenceCode());
		Assert.assertEquals(
			regex, contentRetrieverConfiguration.getIncludePaths());
		Assert.assertEquals(regex, contentRetrieverConfiguration.getSeedUrls());
	}

	@Test
	public void testDeleteContentRetrieverByExternalReferenceCodeContentRetrieverConfiguration()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		ContentRetrieverConfiguration contentRetrieverConfiguration =
			testDeleteContentRetrieverByExternalReferenceCodeContentRetrieverConfiguration_addContentRetrieverConfiguration();

		assertHttpResponseStatusCode(
			204,
			contentRetrieverConfigurationResource.
				deleteContentRetrieverByExternalReferenceCodeContentRetrieverConfigurationHttpResponse(
					testDeleteContentRetrieverByExternalReferenceCodeContentRetrieverConfiguration_getExternalReferenceCode(
						contentRetrieverConfiguration),
					contentRetrieverConfiguration.getExternalReferenceCode()));
	}

	protected ContentRetrieverConfiguration
			testDeleteContentRetrieverByExternalReferenceCodeContentRetrieverConfiguration_addContentRetrieverConfiguration()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testDeleteContentRetrieverByExternalReferenceCodeContentRetrieverConfiguration_getExternalReferenceCode(
				ContentRetrieverConfiguration contentRetrieverConfiguration)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGetContentRetrieverByExternalReferenceCodeContentRetrieverConfigurationsPage()
		throws Exception {

		String externalReferenceCode =
			testGetContentRetrieverByExternalReferenceCodeContentRetrieverConfigurationsPage_getExternalReferenceCode();
		String irrelevantExternalReferenceCode =
			testGetContentRetrieverByExternalReferenceCodeContentRetrieverConfigurationsPage_getIrrelevantExternalReferenceCode();

		Page<ContentRetrieverConfiguration> page =
			contentRetrieverConfigurationResource.
				getContentRetrieverByExternalReferenceCodeContentRetrieverConfigurationsPage(
					externalReferenceCode, null, null, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantExternalReferenceCode != null) {
			ContentRetrieverConfiguration
				irrelevantContentRetrieverConfiguration =
					testGetContentRetrieverByExternalReferenceCodeContentRetrieverConfigurationsPage_addContentRetrieverConfiguration(
						irrelevantExternalReferenceCode,
						randomIrrelevantContentRetrieverConfiguration());

			page =
				contentRetrieverConfigurationResource.
					getContentRetrieverByExternalReferenceCodeContentRetrieverConfigurationsPage(
						irrelevantExternalReferenceCode, null, null,
						Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantContentRetrieverConfiguration,
				(List<ContentRetrieverConfiguration>)page.getItems());
			assertValid(
				page,
				testGetContentRetrieverByExternalReferenceCodeContentRetrieverConfigurationsPage_getExpectedActions(
					irrelevantExternalReferenceCode));
		}

		ContentRetrieverConfiguration contentRetrieverConfiguration1 =
			testGetContentRetrieverByExternalReferenceCodeContentRetrieverConfigurationsPage_addContentRetrieverConfiguration(
				externalReferenceCode, randomContentRetrieverConfiguration());

		ContentRetrieverConfiguration contentRetrieverConfiguration2 =
			testGetContentRetrieverByExternalReferenceCodeContentRetrieverConfigurationsPage_addContentRetrieverConfiguration(
				externalReferenceCode, randomContentRetrieverConfiguration());

		page =
			contentRetrieverConfigurationResource.
				getContentRetrieverByExternalReferenceCodeContentRetrieverConfigurationsPage(
					externalReferenceCode, null, null, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			contentRetrieverConfiguration1,
			(List<ContentRetrieverConfiguration>)page.getItems());
		assertContains(
			contentRetrieverConfiguration2,
			(List<ContentRetrieverConfiguration>)page.getItems());
		assertValid(
			page,
			testGetContentRetrieverByExternalReferenceCodeContentRetrieverConfigurationsPage_getExpectedActions(
				externalReferenceCode));
	}

	protected Map<String, Map<String, String>>
			testGetContentRetrieverByExternalReferenceCodeContentRetrieverConfigurationsPage_getExpectedActions(
				String externalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetContentRetrieverByExternalReferenceCodeContentRetrieverConfigurationsPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		String externalReferenceCode =
			testGetContentRetrieverByExternalReferenceCodeContentRetrieverConfigurationsPage_getExternalReferenceCode();

		ContentRetrieverConfiguration contentRetrieverConfiguration1 =
			randomContentRetrieverConfiguration();

		contentRetrieverConfiguration1 =
			testGetContentRetrieverByExternalReferenceCodeContentRetrieverConfigurationsPage_addContentRetrieverConfiguration(
				externalReferenceCode, contentRetrieverConfiguration1);

		for (EntityField entityField : entityFields) {
			Page<ContentRetrieverConfiguration> page =
				contentRetrieverConfigurationResource.
					getContentRetrieverByExternalReferenceCodeContentRetrieverConfigurationsPage(
						externalReferenceCode, null,
						getFilterString(
							entityField, "between",
							contentRetrieverConfiguration1),
						Pagination.of(1, 2));

			assertEquals(
				Collections.singletonList(contentRetrieverConfiguration1),
				(List<ContentRetrieverConfiguration>)page.getItems());
		}
	}

	@Test
	public void testGetContentRetrieverByExternalReferenceCodeContentRetrieverConfigurationsPageWithFilterDoubleEquals()
		throws Exception {

		testGetContentRetrieverByExternalReferenceCodeContentRetrieverConfigurationsPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetContentRetrieverByExternalReferenceCodeContentRetrieverConfigurationsPageWithFilterStringContains()
		throws Exception {

		testGetContentRetrieverByExternalReferenceCodeContentRetrieverConfigurationsPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetContentRetrieverByExternalReferenceCodeContentRetrieverConfigurationsPageWithFilterStringEquals()
		throws Exception {

		testGetContentRetrieverByExternalReferenceCodeContentRetrieverConfigurationsPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetContentRetrieverByExternalReferenceCodeContentRetrieverConfigurationsPageWithFilterStringStartsWith()
		throws Exception {

		testGetContentRetrieverByExternalReferenceCodeContentRetrieverConfigurationsPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void
			testGetContentRetrieverByExternalReferenceCodeContentRetrieverConfigurationsPageWithFilter(
				String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		String externalReferenceCode =
			testGetContentRetrieverByExternalReferenceCodeContentRetrieverConfigurationsPage_getExternalReferenceCode();

		ContentRetrieverConfiguration contentRetrieverConfiguration1 =
			testGetContentRetrieverByExternalReferenceCodeContentRetrieverConfigurationsPage_addContentRetrieverConfiguration(
				externalReferenceCode, randomContentRetrieverConfiguration());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		ContentRetrieverConfiguration contentRetrieverConfiguration2 =
			testGetContentRetrieverByExternalReferenceCodeContentRetrieverConfigurationsPage_addContentRetrieverConfiguration(
				externalReferenceCode, randomContentRetrieverConfiguration());

		for (EntityField entityField : entityFields) {
			Page<ContentRetrieverConfiguration> page =
				contentRetrieverConfigurationResource.
					getContentRetrieverByExternalReferenceCodeContentRetrieverConfigurationsPage(
						externalReferenceCode, null,
						getFilterString(
							entityField, operator,
							contentRetrieverConfiguration1),
						Pagination.of(1, 2));

			assertEquals(
				Collections.singletonList(contentRetrieverConfiguration1),
				(List<ContentRetrieverConfiguration>)page.getItems());
		}
	}

	@Test
	public void testGetContentRetrieverByExternalReferenceCodeContentRetrieverConfigurationsPageWithPagination()
		throws Exception {

		String externalReferenceCode =
			testGetContentRetrieverByExternalReferenceCodeContentRetrieverConfigurationsPage_getExternalReferenceCode();

		Page<ContentRetrieverConfiguration> contentRetrieverConfigurationsPage =
			contentRetrieverConfigurationResource.
				getContentRetrieverByExternalReferenceCodeContentRetrieverConfigurationsPage(
					externalReferenceCode, null, null, null);

		int totalCount = GetterUtil.getInteger(
			contentRetrieverConfigurationsPage.getTotalCount());

		ContentRetrieverConfiguration contentRetrieverConfiguration1 =
			testGetContentRetrieverByExternalReferenceCodeContentRetrieverConfigurationsPage_addContentRetrieverConfiguration(
				externalReferenceCode, randomContentRetrieverConfiguration());

		ContentRetrieverConfiguration contentRetrieverConfiguration2 =
			testGetContentRetrieverByExternalReferenceCodeContentRetrieverConfigurationsPage_addContentRetrieverConfiguration(
				externalReferenceCode, randomContentRetrieverConfiguration());

		ContentRetrieverConfiguration contentRetrieverConfiguration3 =
			testGetContentRetrieverByExternalReferenceCodeContentRetrieverConfigurationsPage_addContentRetrieverConfiguration(
				externalReferenceCode, randomContentRetrieverConfiguration());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<ContentRetrieverConfiguration> page1 =
				contentRetrieverConfigurationResource.
					getContentRetrieverByExternalReferenceCodeContentRetrieverConfigurationsPage(
						externalReferenceCode, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				contentRetrieverConfiguration1,
				(List<ContentRetrieverConfiguration>)page1.getItems());

			Page<ContentRetrieverConfiguration> page2 =
				contentRetrieverConfigurationResource.
					getContentRetrieverByExternalReferenceCodeContentRetrieverConfigurationsPage(
						externalReferenceCode, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				contentRetrieverConfiguration2,
				(List<ContentRetrieverConfiguration>)page2.getItems());

			Page<ContentRetrieverConfiguration> page3 =
				contentRetrieverConfigurationResource.
					getContentRetrieverByExternalReferenceCodeContentRetrieverConfigurationsPage(
						externalReferenceCode, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				contentRetrieverConfiguration3,
				(List<ContentRetrieverConfiguration>)page3.getItems());
		}
		else {
			Page<ContentRetrieverConfiguration> page1 =
				contentRetrieverConfigurationResource.
					getContentRetrieverByExternalReferenceCodeContentRetrieverConfigurationsPage(
						externalReferenceCode, null, null,
						Pagination.of(1, totalCount + 2));

			List<ContentRetrieverConfiguration>
				contentRetrieverConfigurations1 =
					(List<ContentRetrieverConfiguration>)page1.getItems();

			Assert.assertEquals(
				contentRetrieverConfigurations1.toString(), totalCount + 2,
				contentRetrieverConfigurations1.size());

			Page<ContentRetrieverConfiguration> page2 =
				contentRetrieverConfigurationResource.
					getContentRetrieverByExternalReferenceCodeContentRetrieverConfigurationsPage(
						externalReferenceCode, null, null,
						Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<ContentRetrieverConfiguration>
				contentRetrieverConfigurations2 =
					(List<ContentRetrieverConfiguration>)page2.getItems();

			Assert.assertEquals(
				contentRetrieverConfigurations2.toString(), 1,
				contentRetrieverConfigurations2.size());

			Page<ContentRetrieverConfiguration> page3 =
				contentRetrieverConfigurationResource.
					getContentRetrieverByExternalReferenceCodeContentRetrieverConfigurationsPage(
						externalReferenceCode, null, null,
						Pagination.of(1, (int)totalCount + 3));

			assertContains(
				contentRetrieverConfiguration1,
				(List<ContentRetrieverConfiguration>)page3.getItems());
			assertContains(
				contentRetrieverConfiguration2,
				(List<ContentRetrieverConfiguration>)page3.getItems());
			assertContains(
				contentRetrieverConfiguration3,
				(List<ContentRetrieverConfiguration>)page3.getItems());
		}
	}

	protected ContentRetrieverConfiguration
			testGetContentRetrieverByExternalReferenceCodeContentRetrieverConfigurationsPage_addContentRetrieverConfiguration(
				String externalReferenceCode,
				ContentRetrieverConfiguration contentRetrieverConfiguration)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetContentRetrieverByExternalReferenceCodeContentRetrieverConfigurationsPage_getExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetContentRetrieverByExternalReferenceCodeContentRetrieverConfigurationsPage_getIrrelevantExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testPutContentRetrieverByExternalReferenceCodeContentRetrieverConfiguration()
		throws Exception {

		ContentRetrieverConfiguration postContentRetrieverConfiguration =
			testPutContentRetrieverByExternalReferenceCodeContentRetrieverConfiguration_addContentRetrieverConfiguration();

		ContentRetrieverConfiguration randomContentRetrieverConfiguration =
			randomContentRetrieverConfiguration();

		ContentRetrieverConfiguration putContentRetrieverConfiguration =
			contentRetrieverConfigurationResource.
				putContentRetrieverByExternalReferenceCodeContentRetrieverConfiguration(
					testPutContentRetrieverByExternalReferenceCodeContentRetrieverConfiguration_getExternalReferenceCode(
						postContentRetrieverConfiguration),
					postContentRetrieverConfiguration.
						getExternalReferenceCode(),
					randomContentRetrieverConfiguration);

		assertEquals(
			randomContentRetrieverConfiguration,
			putContentRetrieverConfiguration);
		assertValid(putContentRetrieverConfiguration);

		ContentRetrieverConfiguration getContentRetrieverConfiguration =
			testPutContentRetrieverByExternalReferenceCodeContentRetrieverConfiguration_getContentRetrieverConfiguration(
				testPutContentRetrieverByExternalReferenceCodeContentRetrieverConfiguration_getExternalReferenceCode(
					putContentRetrieverConfiguration),
				putContentRetrieverConfiguration.getExternalReferenceCode());

		assertEquals(
			randomContentRetrieverConfiguration,
			getContentRetrieverConfiguration);
		assertValid(getContentRetrieverConfiguration);
	}

	protected ContentRetrieverConfiguration
		testPutContentRetrieverByExternalReferenceCodeContentRetrieverConfiguration_getContentRetrieverConfiguration(
			String externalReferenceCode,
			String contentRetrieverConfigurationExternalReferenceCode) {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected ContentRetrieverConfiguration
			testPutContentRetrieverByExternalReferenceCodeContentRetrieverConfiguration_addContentRetrieverConfiguration()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testPutContentRetrieverByExternalReferenceCodeContentRetrieverConfiguration_getExternalReferenceCode(
				ContentRetrieverConfiguration contentRetrieverConfiguration)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		Assert.assertTrue(true);
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	protected void assertContains(
		ContentRetrieverConfiguration contentRetrieverConfiguration,
		List<ContentRetrieverConfiguration> contentRetrieverConfigurations) {

		boolean contains = false;

		for (ContentRetrieverConfiguration item :
				contentRetrieverConfigurations) {

			if (equals(contentRetrieverConfiguration, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			contentRetrieverConfigurations + " does not contain " +
				contentRetrieverConfiguration,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		ContentRetrieverConfiguration contentRetrieverConfiguration1,
		ContentRetrieverConfiguration contentRetrieverConfiguration2) {

		Assert.assertTrue(
			contentRetrieverConfiguration1 + " does not equal " +
				contentRetrieverConfiguration2,
			equals(
				contentRetrieverConfiguration1,
				contentRetrieverConfiguration2));
	}

	protected void assertEquals(
		List<ContentRetrieverConfiguration> contentRetrieverConfigurations1,
		List<ContentRetrieverConfiguration> contentRetrieverConfigurations2) {

		Assert.assertEquals(
			contentRetrieverConfigurations1.size(),
			contentRetrieverConfigurations2.size());

		for (int i = 0; i < contentRetrieverConfigurations1.size(); i++) {
			ContentRetrieverConfiguration contentRetrieverConfiguration1 =
				contentRetrieverConfigurations1.get(i);
			ContentRetrieverConfiguration contentRetrieverConfiguration2 =
				contentRetrieverConfigurations2.get(i);

			assertEquals(
				contentRetrieverConfiguration1, contentRetrieverConfiguration2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<ContentRetrieverConfiguration> contentRetrieverConfigurations1,
		List<ContentRetrieverConfiguration> contentRetrieverConfigurations2) {

		Assert.assertEquals(
			contentRetrieverConfigurations1.size(),
			contentRetrieverConfigurations2.size());

		for (ContentRetrieverConfiguration contentRetrieverConfiguration1 :
				contentRetrieverConfigurations1) {

			boolean contains = false;

			for (ContentRetrieverConfiguration contentRetrieverConfiguration2 :
					contentRetrieverConfigurations2) {

				if (equals(
						contentRetrieverConfiguration1,
						contentRetrieverConfiguration2)) {

					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				contentRetrieverConfigurations2 + " does not contain " +
					contentRetrieverConfiguration1,
				contains);
		}
	}

	protected void assertValid(
			ContentRetrieverConfiguration contentRetrieverConfiguration)
		throws Exception {

		boolean valid = true;

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("domain", additionalAssertFieldName)) {
				if (contentRetrieverConfiguration.getDomain() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("excludePaths", additionalAssertFieldName)) {
				if (contentRetrieverConfiguration.getExcludePaths() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (contentRetrieverConfiguration.getExternalReferenceCode() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("includePaths", additionalAssertFieldName)) {
				if (contentRetrieverConfiguration.getIncludePaths() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("seedUrls", additionalAssertFieldName)) {
				if (contentRetrieverConfiguration.getSeedUrls() == null) {
					valid = false;
				}

				continue;
			}

			throw new IllegalArgumentException(
				"Invalid additional assert field name " +
					additionalAssertFieldName);
		}

		Assert.assertTrue(valid);
	}

	protected void assertValid(Page<ContentRetrieverConfiguration> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<ContentRetrieverConfiguration> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<ContentRetrieverConfiguration>
			contentRetrieverConfigurations = page.getItems();

		int size = contentRetrieverConfigurations.size();

		if ((page.getLastPage() > 0) && (page.getPage() > 0) &&
			(page.getPageSize() > 0) && (page.getTotalCount() > 0) &&
			(size > 0)) {

			valid = true;
		}

		Assert.assertTrue(valid);

		assertValid(page.getActions(), expectedActions);
	}

	protected void assertValid(
		Map<String, Map<String, String>> actions1,
		Map<String, Map<String, String>> actions2) {

		for (String key : actions2.keySet()) {
			Map action = actions1.get(key);

			Assert.assertNotNull(key + " does not contain an action", action);

			Map<String, String> expectedAction = actions2.get(key);

			Assert.assertEquals(
				expectedAction.get("method"), action.get("method"));
			Assert.assertEquals(expectedAction.get("href"), action.get("href"));
		}
	}

	protected String[] getAdditionalAssertFieldNames() {
		return new String[0];
	}

	protected List<GraphQLField> getGraphQLFields() throws Exception {
		List<GraphQLField> graphQLFields = new ArrayList<>();

		graphQLFields.add(new GraphQLField("externalReferenceCode"));

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.ai.hub.rest.dto.v1_0.
						ContentRetrieverConfiguration.class)) {

			if (!ArrayUtil.contains(
					getAdditionalAssertFieldNames(), field.getName())) {

				continue;
			}

			graphQLFields.addAll(getGraphQLFields(field));
		}

		return graphQLFields;
	}

	protected List<GraphQLField> getGraphQLFields(
			java.lang.reflect.Field... fields)
		throws Exception {

		List<GraphQLField> graphQLFields = new ArrayList<>();

		for (java.lang.reflect.Field field : fields) {
			com.liferay.portal.vulcan.graphql.annotation.GraphQLField
				vulcanGraphQLField = field.getAnnotation(
					com.liferay.portal.vulcan.graphql.annotation.GraphQLField.
						class);

			if (vulcanGraphQLField != null) {
				Class<?> clazz = field.getType();

				if (clazz.isArray()) {
					clazz = clazz.getComponentType();
				}

				List<GraphQLField> childrenGraphQLFields = getGraphQLFields(
					getDeclaredFields(clazz));

				graphQLFields.add(
					new GraphQLField(field.getName(), childrenGraphQLFields));
			}
		}

		return graphQLFields;
	}

	protected String[] getIgnoredEntityFieldNames() {
		return new String[0];
	}

	protected boolean equals(
		ContentRetrieverConfiguration contentRetrieverConfiguration1,
		ContentRetrieverConfiguration contentRetrieverConfiguration2) {

		if (contentRetrieverConfiguration1 == contentRetrieverConfiguration2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("domain", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						contentRetrieverConfiguration1.getDomain(),
						contentRetrieverConfiguration2.getDomain())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("excludePaths", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						contentRetrieverConfiguration1.getExcludePaths(),
						contentRetrieverConfiguration2.getExcludePaths())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						contentRetrieverConfiguration1.
							getExternalReferenceCode(),
						contentRetrieverConfiguration2.
							getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("includePaths", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						contentRetrieverConfiguration1.getIncludePaths(),
						contentRetrieverConfiguration2.getIncludePaths())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("seedUrls", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						contentRetrieverConfiguration1.getSeedUrls(),
						contentRetrieverConfiguration2.getSeedUrls())) {

					return false;
				}

				continue;
			}

			throw new IllegalArgumentException(
				"Invalid additional assert field name " +
					additionalAssertFieldName);
		}

		return true;
	}

	protected boolean equals(
		Map<String, Object> map1, Map<String, Object> map2) {

		if (Objects.equals(map1.keySet(), map2.keySet())) {
			for (Map.Entry<String, Object> entry : map1.entrySet()) {
				if (entry.getValue() instanceof Map) {
					if (!equals(
							(Map)entry.getValue(),
							(Map)map2.get(entry.getKey()))) {

						return false;
					}
				}
				else if (!Objects.deepEquals(
							entry.getValue(), map2.get(entry.getKey()))) {

					return false;
				}
			}

			return true;
		}

		return false;
	}

	protected java.lang.reflect.Field[] getDeclaredFields(Class clazz)
		throws Exception {

		if (clazz.getClassLoader() == null) {
			return new java.lang.reflect.Field[0];
		}

		return TransformUtil.transform(
			ReflectionUtil.getDeclaredFields(clazz),
			field -> {
				if (field.isSynthetic()) {
					return null;
				}

				return field;
			},
			java.lang.reflect.Field.class);
	}

	protected java.util.Collection<EntityField> getEntityFields()
		throws Exception {

		if (!(_contentRetrieverConfigurationResource instanceof
				EntityModelResource)) {

			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_contentRetrieverConfigurationResource;

		EntityModel entityModel = entityModelResource.getEntityModel(
			new MultivaluedHashMap());

		if (entityModel == null) {
			return Collections.emptyList();
		}

		Map<String, EntityField> entityFieldsMap =
			entityModel.getEntityFieldsMap();

		return entityFieldsMap.values();
	}

	protected List<EntityField> getEntityFields(EntityField.Type type)
		throws Exception {

		return TransformUtil.transform(
			getEntityFields(),
			entityField -> {
				if (!Objects.equals(entityField.getType(), type) ||
					ArrayUtil.contains(
						getIgnoredEntityFieldNames(), entityField.getName())) {

					return null;
				}

				return entityField;
			});
	}

	protected String getFilterString(
		EntityField entityField, String operator,
		ContentRetrieverConfiguration contentRetrieverConfiguration) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("domain")) {
			Object object = contentRetrieverConfiguration.getDomain();

			String value = String.valueOf(object);

			if (operator.equals("contains")) {
				sb = new StringBundler();

				sb.append("contains(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 2)) {
					sb.append(value.substring(1, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else if (operator.equals("startswith")) {
				sb = new StringBundler();

				sb.append("startswith(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 1)) {
					sb.append(value.substring(0, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else {
				sb.append("'");
				sb.append(value);
				sb.append("'");
			}

			return sb.toString();
		}

		if (entityFieldName.equals("excludePaths")) {
			Object object = contentRetrieverConfiguration.getExcludePaths();

			String value = String.valueOf(object);

			if (operator.equals("contains")) {
				sb = new StringBundler();

				sb.append("contains(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 2)) {
					sb.append(value.substring(1, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else if (operator.equals("startswith")) {
				sb = new StringBundler();

				sb.append("startswith(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 1)) {
					sb.append(value.substring(0, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else {
				sb.append("'");
				sb.append(value);
				sb.append("'");
			}

			return sb.toString();
		}

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object =
				contentRetrieverConfiguration.getExternalReferenceCode();

			String value = String.valueOf(object);

			if (operator.equals("contains")) {
				sb = new StringBundler();

				sb.append("contains(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 2)) {
					sb.append(value.substring(1, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else if (operator.equals("startswith")) {
				sb = new StringBundler();

				sb.append("startswith(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 1)) {
					sb.append(value.substring(0, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else {
				sb.append("'");
				sb.append(value);
				sb.append("'");
			}

			return sb.toString();
		}

		if (entityFieldName.equals("includePaths")) {
			Object object = contentRetrieverConfiguration.getIncludePaths();

			String value = String.valueOf(object);

			if (operator.equals("contains")) {
				sb = new StringBundler();

				sb.append("contains(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 2)) {
					sb.append(value.substring(1, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else if (operator.equals("startswith")) {
				sb = new StringBundler();

				sb.append("startswith(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 1)) {
					sb.append(value.substring(0, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else {
				sb.append("'");
				sb.append(value);
				sb.append("'");
			}

			return sb.toString();
		}

		if (entityFieldName.equals("seedUrls")) {
			Object object = contentRetrieverConfiguration.getSeedUrls();

			String value = String.valueOf(object);

			if (operator.equals("contains")) {
				sb = new StringBundler();

				sb.append("contains(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 2)) {
					sb.append(value.substring(1, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else if (operator.equals("startswith")) {
				sb = new StringBundler();

				sb.append("startswith(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 1)) {
					sb.append(value.substring(0, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else {
				sb.append("'");
				sb.append(value);
				sb.append("'");
			}

			return sb.toString();
		}

		throw new IllegalArgumentException(
			"Invalid entity field " + entityFieldName);
	}

	protected String invoke(String query) throws Exception {
		HttpInvoker httpInvoker = HttpInvoker.newHttpInvoker();

		httpInvoker.body(
			JSONUtil.put(
				"query", query
			).toString(),
			"application/json");
		httpInvoker.httpMethod(HttpInvoker.HttpMethod.POST);
		httpInvoker.path(
			"http://localhost:" + PortalUtil.getPortalServerPort(false) +
				"/o/graphql");
		httpInvoker.userNameAndPassword(
			"test@liferay.com:" + PropsValues.DEFAULT_ADMIN_PASSWORD);

		HttpInvoker.HttpResponse httpResponse = httpInvoker.invoke();

		return httpResponse.getContent();
	}

	protected JSONObject invokeGraphQLMutation(GraphQLField graphQLField)
		throws Exception {

		GraphQLField mutationGraphQLField = new GraphQLField(
			"mutation", graphQLField);

		return JSONFactoryUtil.createJSONObject(
			invoke(mutationGraphQLField.toString()));
	}

	protected JSONObject invokeGraphQLQuery(GraphQLField graphQLField)
		throws Exception {

		GraphQLField queryGraphQLField = new GraphQLField(
			"query", graphQLField);

		return JSONFactoryUtil.createJSONObject(
			invoke(queryGraphQLField.toString()));
	}

	protected ContentRetrieverConfiguration
			randomContentRetrieverConfiguration()
		throws Exception {

		return new ContentRetrieverConfiguration() {
			{
				domain = StringUtil.toLowerCase(RandomTestUtil.randomString());
				excludePaths = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				includePaths = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				seedUrls = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
			}
		};
	}

	protected ContentRetrieverConfiguration
			randomIrrelevantContentRetrieverConfiguration()
		throws Exception {

		ContentRetrieverConfiguration
			randomIrrelevantContentRetrieverConfiguration =
				randomContentRetrieverConfiguration();

		return randomIrrelevantContentRetrieverConfiguration;
	}

	protected ContentRetrieverConfiguration
			randomPatchContentRetrieverConfiguration()
		throws Exception {

		return randomContentRetrieverConfiguration();
	}

	protected ContentRetrieverConfigurationResource
		contentRetrieverConfigurationResource;
	protected com.liferay.portal.kernel.model.Group irrelevantGroup;
	protected com.liferay.portal.kernel.model.Company testCompany;
	protected com.liferay.portal.kernel.model.Group testGroup;

	protected static class BeanTestUtil {

		public static void copyProperties(Object source, Object target)
			throws Exception {

			Class<?> sourceClass = source.getClass();

			Class<?> targetClass = target.getClass();

			for (java.lang.reflect.Field field :
					_getAllDeclaredFields(sourceClass)) {

				if (field.isSynthetic()) {
					continue;
				}

				Method getMethod = _getMethod(
					sourceClass, field.getName(), "get");

				try {
					Method setMethod = _getMethod(
						targetClass, field.getName(), "set",
						getMethod.getReturnType());

					setMethod.invoke(target, getMethod.invoke(source));
				}
				catch (Exception e) {
					continue;
				}
			}
		}

		public static boolean hasProperty(Object bean, String name) {
			Method setMethod = _getMethod(
				bean.getClass(), "set" + StringUtil.upperCaseFirstLetter(name));

			if (setMethod != null) {
				return true;
			}

			return false;
		}

		public static void setProperty(Object bean, String name, Object value)
			throws Exception {

			Class<?> clazz = bean.getClass();

			Method setMethod = _getMethod(
				clazz, "set" + StringUtil.upperCaseFirstLetter(name));

			if (setMethod == null) {
				throw new NoSuchMethodException();
			}

			Class<?>[] parameterTypes = setMethod.getParameterTypes();

			setMethod.invoke(bean, _translateValue(parameterTypes[0], value));
		}

		private static List<java.lang.reflect.Field> _getAllDeclaredFields(
			Class<?> clazz) {

			List<java.lang.reflect.Field> fields = new ArrayList<>();

			while ((clazz != null) && (clazz != Object.class)) {
				for (java.lang.reflect.Field field :
						clazz.getDeclaredFields()) {

					fields.add(field);
				}

				clazz = clazz.getSuperclass();
			}

			return fields;
		}

		private static Method _getMethod(Class<?> clazz, String name) {
			for (Method method : clazz.getMethods()) {
				if (name.equals(method.getName()) &&
					(method.getParameterCount() == 1) &&
					_parameterTypes.contains(method.getParameterTypes()[0])) {

					return method;
				}
			}

			return null;
		}

		private static Method _getMethod(
				Class<?> clazz, String fieldName, String prefix,
				Class<?>... parameterTypes)
			throws Exception {

			return clazz.getMethod(
				prefix + StringUtil.upperCaseFirstLetter(fieldName),
				parameterTypes);
		}

		private static Object _translateValue(
			Class<?> parameterType, Object value) {

			if ((value instanceof Integer) &&
				parameterType.equals(Long.class)) {

				Integer intValue = (Integer)value;

				return intValue.longValue();
			}

			return value;
		}

		private static final Set<Class<?>> _parameterTypes = new HashSet<>(
			Arrays.asList(
				Boolean.class, Date.class, Double.class, Integer.class,
				Long.class, Map.class, String.class));

	}

	protected class GraphQLField {

		public GraphQLField(String key, GraphQLField... graphQLFields) {
			this(key, new HashMap<>(), graphQLFields);
		}

		public GraphQLField(String key, List<GraphQLField> graphQLFields) {
			this(key, new HashMap<>(), graphQLFields);
		}

		public GraphQLField(
			String key, Map<String, Object> parameterMap,
			GraphQLField... graphQLFields) {

			_key = key;
			_parameterMap = parameterMap;
			_graphQLFields = Arrays.asList(graphQLFields);
		}

		public GraphQLField(
			String key, Map<String, Object> parameterMap,
			List<GraphQLField> graphQLFields) {

			_key = key;
			_parameterMap = parameterMap;
			_graphQLFields = graphQLFields;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder(_key);

			if (!_parameterMap.isEmpty()) {
				sb.append("(");

				for (Map.Entry<String, Object> entry :
						_parameterMap.entrySet()) {

					sb.append(entry.getKey());
					sb.append(": ");
					sb.append(entry.getValue());
					sb.append(", ");
				}

				sb.setLength(sb.length() - 2);

				sb.append(")");
			}

			if (!_graphQLFields.isEmpty()) {
				sb.append("{");

				for (GraphQLField graphQLField : _graphQLFields) {
					sb.append(graphQLField.toString());
					sb.append(", ");
				}

				sb.setLength(sb.length() - 2);

				sb.append("}");
			}

			return sb.toString();
		}

		private final List<GraphQLField> _graphQLFields;
		private final String _key;
		private final Map<String, Object> _parameterMap;

	}

	private static final com.liferay.portal.kernel.log.Log _log =
		LogFactoryUtil.getLog(
			BaseContentRetrieverConfigurationResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private
		com.liferay.ai.hub.rest.resource.v1_0.
			ContentRetrieverConfigurationResource
				_contentRetrieverConfigurationResource;

}
// LIFERAY-REST-BUILDER-HASH:1572386297