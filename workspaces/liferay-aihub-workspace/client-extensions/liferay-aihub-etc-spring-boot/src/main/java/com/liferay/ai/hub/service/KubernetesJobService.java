/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.service;

import com.liferay.ai.hub.model.CrawlerConfiguration;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringUtil;

import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.api.model.EnvVarBuilder;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.api.model.batch.v1.JobBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.fabric8.kubernetes.client.dsl.PrettyLoggable;
import io.fabric8.kubernetes.client.dsl.ScalableResource;
import io.fabric8.kubernetes.client.utils.Serialization;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import java.io.IOException;
import java.io.InputStream;

import java.nio.charset.StandardCharsets;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.json.JSONArray;
import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

/**
 * @author José Abelenda
 */
@Service
public class KubernetesJobService {

	public Job createJob(
		long accountEntryId, CrawlerConfiguration crawlerConfiguration) {

		Job job = _kubernetesClient.batch(
		).v1(
		).jobs(
		).inNamespace(
			_namespace
		).resource(
			new JobBuilder(
				_job
			).editMetadata(
			).addToLabels(
				"account-entry-id", String.valueOf(accountEntryId)
			).endMetadata(
			).editSpec(
			).editTemplate(
			).editMetadata(
			).addToLabels(
				"account-entry-id", String.valueOf(accountEntryId)
			).endMetadata(
			).editSpec(
			).editFirstContainer(
			).withImage(
				_imageName
			).withEnv(
				_createEnvVar(
					"CRAWLER_CONFIG_YAML",
					_getCrawlerConfigurationYAML(crawlerConfiguration))
			).endContainer(
			).endSpec(
			).endTemplate(
			).endSpec(
			).build()
		).create();

		if (_log.isInfoEnabled()) {
			ObjectMeta objectMeta = job.getMetadata();

			_log.info("Kubernetes job dispatched: " + objectMeta.getName());
		}

		return job;
	}

	public Job getJob(String name) {
		ScalableResource<Job> scalableResource = _getJobScalableResource(name);

		return scalableResource.get();
	}

	public String getJobLog(String name, int tailLines) {
		ScalableResource<Job> scalableResource = _getJobScalableResource(name);

		PrettyLoggable prettyLoggable = scalableResource.tailingLines(
			tailLines);

		return prettyLoggable.getLog();
	}

	@PostConstruct
	public void postConstruct() {
		Class<?> clazz = getClass();

		try (InputStream inputStream = clazz.getResourceAsStream(
				"dependencies/crawler-configuration.yaml")) {

			_crawlerConfigurationTemplate = new String(
				inputStream.readAllBytes(), StandardCharsets.UTF_8);
		}
		catch (IOException ioException) {
			throw new IllegalStateException(
				"Unable to load \"crawler-configuration.yaml\"", ioException);
		}

		try (InputStream inputStream = clazz.getResourceAsStream(
				"dependencies/crawler-job.yaml")) {

			_job = Serialization.unmarshal(inputStream, Job.class);
		}
		catch (IOException ioException) {
			throw new IllegalStateException(
				"Unable to load \"crawler-job.yaml\"", ioException);
		}
	}

	@PreDestroy
	public void preDestroy() {
		_kubernetesClient.close();
	}

	private EnvVar _createEnvVar(String name, String value) {
		return new EnvVarBuilder(
		).withName(
			name
		).withValue(
			value
		).build();
	}

	private String _getCrawlerConfigurationYAML(
		CrawlerConfiguration crawlerConfiguration) {

		DumperOptions dumperOptions = new DumperOptions();

		dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

		Yaml yaml = new Yaml(dumperOptions);

		Map<String, Object> configuration = yaml.load(
			_crawlerConfigurationTemplate);

		List<Map<String, Object>> domains =
			(List<Map<String, Object>>)configuration.get("domains");

		Map<String, Object> domain = domains.get(0);

		configuration.put(
			"domains",
			_getDomains(
				crawlerConfiguration.getDomainsJSONArray(),
				(List<Map<String, Object>>)domain.get("crawl_rules")));

		configuration.put("log_level", _crawlerLogLevel);
		configuration.put(
			"max_crawl_depth", crawlerConfiguration.getMaxCrawlDepth());
		configuration.put(
			"max_duration", crawlerConfiguration.getMaxDuration());
		configuration.put(
			"max_extracted_links_count",
			crawlerConfiguration.getMaxLinksPerPage());
		configuration.put(
			"max_unique_url_count",
			crawlerConfiguration.getMaxUniqueURLsCount());
		configuration.put("output_index", crawlerConfiguration.getIndexName());

		Map<String, Object> elasticsearch =
			(Map<String, Object>)configuration.get("elasticsearch");

		elasticsearch.put("host", _elasticsearchHost);
		elasticsearch.put("port", _elasticsearchPort);

		return yaml.dump(configuration);
	}

	private List<Map<String, Object>> _getDomains(
		JSONArray jsonArray, List<Map<String, Object>> systemCrawlRules) {

		List<Map<String, Object>> domains = new ArrayList<>();

		for (int i = 0; i < jsonArray.length(); i++) {
			List<Map<String, Object>> crawlRules = new ArrayList<>();

			for (Map<String, Object> systemCrawlRule : systemCrawlRules) {
				crawlRules.add(new LinkedHashMap<>(systemCrawlRule));
			}

			JSONObject jsonObject = jsonArray.getJSONObject(i);

			for (String includePath :
					StringUtil.split(
						jsonObject.optString("includePaths"),
						CharPool.NEW_LINE)) {

				crawlRules.add(
					Map.of(
						"pattern", includePath.trim(), "policy", "allow",
						"type", "regex"));
			}

			for (String excludePath :
					StringUtil.split(
						jsonObject.optString("excludePaths"),
						CharPool.NEW_LINE)) {

				crawlRules.add(
					Map.of(
						"pattern", excludePath.trim(), "policy", "deny", "type",
						"regex"));
			}

			List<String> seedUrls = StringUtil.split(
				jsonObject.optString("seedUrls"), CharPool.NEW_LINE);

			seedUrls.replaceAll(String::trim);

			domains.add(
				Map.of(
					"crawl_rules", crawlRules, "seed_urls", seedUrls, "url",
					jsonObject.optString("domain")));
		}

		return domains;
	}

	private ScalableResource<Job> _getJobScalableResource(String name) {
		return _kubernetesClient.batch(
		).v1(
		).jobs(
		).inNamespace(
			_namespace
		).withName(
			name
		);
	}

	private static final Log _log = LogFactory.getLog(
		KubernetesJobService.class);

	private String _crawlerConfigurationTemplate;

	@Value("${liferay.ai.hub.crawler.log.level}")
	private String _crawlerLogLevel;

	@Value("${liferay.ai.hub.crawler.elasticsearch.host}")
	private String _elasticsearchHost;

	@Value("${liferay.ai.hub.crawler.elasticsearch.port}")
	private int _elasticsearchPort;

	@Value("${liferay.ai.hub.crawler.k8s.image.name}")
	private String _imageName;

	private Job _job;
	private final KubernetesClient _kubernetesClient =
		new KubernetesClientBuilder(
		).build();

	@Value("${liferay.ai.hub.crawler.k8s.namespace}")
	private String _namespace;

}