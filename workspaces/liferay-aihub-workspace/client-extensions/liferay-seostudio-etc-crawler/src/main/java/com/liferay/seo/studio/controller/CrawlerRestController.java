/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.seo.studio.controller;

import com.liferay.client.extension.util.spring.boot3.BaseRestController;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.seo.studio.constants.SEOStudioScanConstants;
import com.liferay.seo.studio.model.CrawlHit;
import com.liferay.seo.studio.service.KubernetesJobService;
import com.liferay.seo.studio.service.SEOStudioService;

import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.api.model.batch.v1.JobCondition;
import io.fabric8.kubernetes.api.model.batch.v1.JobStatus;

import jakarta.annotation.PreDestroy;

import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.UnknownHostException;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.time.Duration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.json.JSONArray;
import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Brooke Dalton
 */
@RequestMapping("/crawler")
@RestController
public class CrawlerRestController extends BaseRestController {

	@PostMapping
	public ResponseEntity<String> post(@RequestBody String json) {
		if (_log.isDebugEnabled()) {
			_log.debug(json);
		}

		JSONObject objectEntryJSONObject = new JSONObject(
			json
		).getJSONObject(
			"objectEntry"
		);

		long seoStudioScanId = objectEntryJSONObject.getLong("objectEntryId");

		try {
			JSONObject valuesJSONObject = objectEntryJSONObject.getJSONObject(
				"values");

			long seoStudioDomainId = _seoStudioService.getSEOStudioDomainId(
				valuesJSONObject);

			JSONObject seoStudioDomainJSONObject =
				_seoStudioService.fetchSEOStudioDomainJSONObject(
					seoStudioDomainId);

			if (seoStudioDomainJSONObject == null) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Unable to get a domain for SEO Studio domain ID " +
							seoStudioDomainId);
				}

				return ResponseEntity.ok(
					_seoStudioService.patchSEOStudioScan(
						"Unable to get a domain for SEO Studio domain ID " +
							seoStudioDomainId,
						seoStudioScanId, SEOStudioScanConstants.STATE_FAILED));
			}

			String domainURL = _seoStudioService.toDomainURL(
				_seoStudioService.toCrawlURI(
					seoStudioDomainJSONObject.getString("hostname")));

			HttpResponse<Void> httpResponse = _httpClient.send(
				HttpRequest.newBuilder(
					URI.create(domainURL)
				).timeout(
					Duration.ofSeconds(60)
				).GET(
				).build(),
				HttpResponse.BodyHandlers.discarding());

			URI uri = httpResponse.uri();

			if ((uri != null) && Validator.isNotNull(uri.getHost())) {
				domainURL = _seoStudioService.toDomainURL(uri);
			}

			String sitemapURL = domainURL + "/sitemap.xml";

			if (!_isSitemapReachable(sitemapURL)) {
				if (_log.isWarnEnabled()) {
					_log.warn("Unable to reach the sitemap at " + sitemapURL);
				}

				return ResponseEntity.ok(
					_seoStudioService.patchSEOStudioScan(
						"Unable to reach the sitemap at " + sitemapURL,
						seoStudioScanId, SEOStudioScanConstants.STATE_FAILED));
			}

			_seoStudioService.patchSEOStudioScan(
				null, seoStudioScanId, SEOStudioScanConstants.STATE_RUNNING);

			JSONObject scopeConfigJSONObject = new JSONObject(
				valuesJSONObject.getString("scopeConfig"));

			Job job = _kubernetesJobService.createJob(
				valuesJSONObject.getLong(
					"r_accountToSEOStudioScans_accountEntryId"),
				domainURL, scopeConfigJSONObject.getInt("maxCrawlDepth"),
				scopeConfigJSONObject.getInt("maxDuration"),
				_seoStudioService.toIndexName(seoStudioDomainId), sitemapURL);

			ObjectMeta objectMeta = job.getMetadata();

			JSONObject seoStudioScanJSONObject = new JSONObject(
			).put(
				"executionId", objectMeta.getName()
			);

			_seoStudioService.patchSEOStudioScan(
				seoStudioScanJSONObject, seoStudioScanId);

			return ResponseEntity.ok(seoStudioScanJSONObject.toString());
		}
		catch (Exception exception) {
			_log.error("Unable to scan the domain", exception);

			return ResponseEntity.ok(
				_seoStudioService.patchSEOStudioScan(
					"Unable to scan the domain: " + exception.getMessage(),
					seoStudioScanId, SEOStudioScanConstants.STATE_FAILED));
		}
	}

	@PreDestroy
	public void preDestroy() {
		_executorService.shutdown();
	}

	@Scheduled(fixedDelay = 60000)
	public void scheduledPatchSEOStudioScans() {
		JSONArray itemsJSONArray = new JSONObject(
			_seoStudioService.getActiveSEOStudioScans()
		).optJSONArray(
			"items"
		);

		if (itemsJSONArray == null) {
			return;
		}

		for (Object object : itemsJSONArray) {
			JSONObject seoStudioScanJSONObject = (JSONObject)object;

			try {
				_patchSEOStudioScan(seoStudioScanJSONObject);
			}
			catch (Exception exception) {
				long seoStudioScanId = seoStudioScanJSONObject.getLong("id");

				_log.error(
					"Unable to patch SEO Studio scan ID " + seoStudioScanId,
					exception);

				_seoStudioService.patchSEOStudioScan(
					"Unable to patch scan: " + exception.getMessage(),
					seoStudioScanId, SEOStudioScanConstants.STATE_FAILED);
			}
		}
	}

	private List<JSONObject> _getBrokenInternalLinksInsightJSONObjects(
		Map<String, Set<String>> issueURLsMap,
		Map<String, Set<String>> linkedURLPageURLsMap) {

		Set<String> brokenInternalLinkURLs = issueURLsMap.getOrDefault(
			_ISSUE_BROKEN_INTERNAL_LINK, Collections.emptySet());

		return Arrays.asList(
			new JSONObject(
			).put(
				"category", "linksAndURLs"
			).put(
				"classification", "problem"
			).put(
				"description",
				StringBundler.concat(
					"This page has one or more internal links pointing to ",
					"URLs that cannot be retrieved because they are ",
					"unreachable, return 400, 404, 405, or 410, or redirect ",
					"without a destination. Broken internal links waste link ",
					"equity, frustrate users, and produce dead end paths ",
					"through your content.")
			).put(
				"fixHint",
				StringBundler.concat(
					"For each broken link, either update the href to point to ",
					"the correct destination, or remove the link if no ",
					"equivalent destination exists. If many links point to ",
					"one missing URL, restoring or redirecting that URL once ",
					"fixes them all.")
			).put(
				"name", "brokenInternalLinks"
			).put(
				"pageURLs",
				_getPageURLs(linkedURLPageURLsMap, brokenInternalLinkURLs)
			).put(
				"severity", "3"
			));
	}

	private HttpResponse<Void> _getHttpResponse(String url) throws Exception {
		HttpRequest httpRequest = HttpRequest.newBuilder(
			URI.create(url)
		).method(
			"HEAD", HttpRequest.BodyPublishers.noBody()
		).timeout(
			Duration.ofSeconds(10)
		).build();

		HttpResponse<Void> httpResponse = _noRedirectHttpClient.send(
			httpRequest, HttpResponse.BodyHandlers.discarding());

		int statusCode = httpResponse.statusCode();

		if ((statusCode != HttpURLConnection.HTTP_BAD_METHOD) &&
			(statusCode != HttpURLConnection.HTTP_NOT_IMPLEMENTED)) {

			return httpResponse;
		}

		httpRequest = HttpRequest.newBuilder(
			URI.create(url)
		).header(
			"Range", "bytes=0-0"
		).timeout(
			Duration.ofSeconds(10)
		).GET(
		).build();

		return _noRedirectHttpClient.send(
			httpRequest, HttpResponse.BodyHandlers.discarding());
	}

	private Map<String, Set<String>> _getIssueURLsMap(
			URI domainURI, Set<String> urls)
		throws Exception {

		Map<String, Set<String>> issueURLsMap = new ConcurrentHashMap<>();

		List<Future<?>> futures = TransformUtil.transform(
			urls,
			url -> _executorService.submit(
				() -> {
					String issue = _getRedirectIssue(domainURI, url);

					if (Validator.isNull(issue)) {
						return;
					}

					Set<String> issueURLs = issueURLsMap.computeIfAbsent(
						issue, key -> ConcurrentHashMap.newKeySet());

					issueURLs.add(url);
				}));

		for (Future<?> future : futures) {
			future.get();
		}

		return issueURLsMap;
	}

	private Map<String, Set<String>> _getLinkedURLPageURLsMap(
		List<CrawlHit> crawlHits, URI domainURI) {

		Map<String, Set<String>> linkedURLPageURLsMap = new HashMap<>();
		Set<String> canonicalURLs = new LinkedHashSet<>();

		for (CrawlHit crawlHit : crawlHits) {
			String canonicalURL = crawlHit.getCanonicalURL();

			if (Validator.isNull(canonicalURL)) {
				continue;
			}

			canonicalURLs.add(canonicalURL);

			for (String linkedURL : crawlHit.getLinks()) {
				if (!_isInternalURL(domainURI, linkedURL) ||
					linkedURL.equals(canonicalURL)) {

					continue;
				}

				Set<String> linkedURLPageURLs =
					linkedURLPageURLsMap.computeIfAbsent(
						linkedURL, key -> new LinkedHashSet<>());

				linkedURLPageURLs.add(canonicalURL);
			}
		}

		Set<String> linkedURLs = linkedURLPageURLsMap.keySet();

		linkedURLs.removeAll(canonicalURLs);

		return linkedURLPageURLsMap;
	}

	private List<JSONObject> _getMetadataInsightJSONObjects(
		List<CrawlHit> crawlHits) {

		Set<String> missingOrEmptyMetaDescriptionTagPageURLs =
			new LinkedHashSet<>();
		Set<String> missingOrEmptyTitleTagPageURLs = new LinkedHashSet<>();

		for (CrawlHit crawlHit : crawlHits) {
			String canonicalURL = crawlHit.getCanonicalURL();

			if (Validator.isNull(canonicalURL)) {
				continue;
			}

			if (Validator.isNull(crawlHit.getMetaDescription())) {
				missingOrEmptyMetaDescriptionTagPageURLs.add(canonicalURL);
			}

			if (Validator.isNull(crawlHit.getTitle())) {
				missingOrEmptyTitleTagPageURLs.add(canonicalURL);
			}
		}

		return Arrays.asList(
			new JSONObject(
			).put(
				"category", "metadata"
			).put(
				"classification", "opportunity"
			).put(
				"description",
				StringBundler.concat(
					"This page has no <meta name=\"description\"> tag, or its ",
					"content attribute is empty. Without one, Google ",
					"autogenerates a snippet from body text — typically ",
					"producing a less compelling preview than an authored ",
					"description.")
			).put(
				"fixHint",
				StringBundler.concat(
					"Add a unique meta description of roughly 150 to 160 ",
					"characters that summarizes the page and includes its ",
					"primary keywords, so search engines show it verbatim in ",
					"the results snippet.")
			).put(
				"name", "missingOrEmptyMetaDescriptionTag"
			).put(
				"pageURLs", missingOrEmptyMetaDescriptionTagPageURLs
			).put(
				"severity", "2"
			),
			new JSONObject(
			).put(
				"category", "metadata"
			).put(
				"classification", "problem"
			).put(
				"description",
				StringBundler.concat(
					"This page has no <title> tag, or the tag is present but ",
					"empty. The title is the first thing search engines and ",
					"users read in SERP listings and carries one of the ",
					"strongest on page ranking signals.")
			).put(
				"fixHint",
				StringBundler.concat(
					"Add a concise, unique <title> of roughly 50 to 60 ",
					"characters that leads with the page's primary keyword ",
					"and reflects its actual content.")
			).put(
				"name", "missingOrEmptyTitleTag"
			).put(
				"pageURLs", missingOrEmptyTitleTagPageURLs
			).put(
				"severity", "3"
			));
	}

	private List<JSONObject> _getOrphanPagesInsightJSONObjects(
		List<CrawlHit> crawlHits, String domainURL) {

		Set<String> canonicalURLs = new LinkedHashSet<>();
		Set<String> linkedURLs = new HashSet<>();

		for (CrawlHit crawlHit : crawlHits) {
			String canonicalURL = crawlHit.getCanonicalURL();

			if (Validator.isNull(canonicalURL)) {
				continue;
			}

			canonicalURLs.add(canonicalURL);

			for (String linkedURL : crawlHit.getLinks()) {
				if (Validator.isNotNull(linkedURL) &&
					!linkedURL.equals(canonicalURL)) {

					linkedURLs.add(linkedURL);
				}
			}
		}

		List<String> pageURLs = TransformUtil.transform(
			canonicalURLs,
			canonicalURL -> {
				if (canonicalURL.equals(domainURL) ||
					linkedURLs.contains(canonicalURL)) {

					return null;
				}

				return canonicalURL;
			});

		return Arrays.asList(
			new JSONObject(
			).put(
				"category", "linksAndURLs"
			).put(
				"classification", "problem"
			).put(
				"description",
				StringBundler.concat(
					"This page is published and indexable but has zero ",
					"internal links pointing to it. Orphan pages are nearly ",
					"invisible to both users browsing the site and crawlers ",
					"building the link graph. Even when they are listed in a ",
					"sitemap, they collect very little ranking authority.")
			).put(
				"fixHint",
				StringBundler.concat(
					"Identify 2-5 topically related pages and add contextual ",
					"internal links pointing to the orphan, with descriptive ",
					"anchor text. If no relevant linking context exists ",
					"anywhere on the site, that is a signal the page may not ",
					"belong in the public site at all.")
			).put(
				"name", "orphanPages"
			).put(
				"pageURLs", pageURLs
			).put(
				"severity", "2"
			));
	}

	private Set<String> _getPageURLs(
		Map<String, Set<String>> linkedURLPageURLsMap, Set<String> linkedURLs) {

		Set<String> pageURLs = new LinkedHashSet<>();

		for (String linkedURL : linkedURLs) {
			pageURLs.addAll(
				linkedURLPageURLsMap.getOrDefault(
					linkedURL, Collections.emptySet()));
		}

		return pageURLs;
	}

	private List<JSONObject> _getRedirectChainsInsightJSONObjects(
		Map<String, Set<String>> issueURLsMap,
		Map<String, Set<String>> linkedURLPageURLsMap) {

		Set<String> redirectChainURLs = issueURLsMap.getOrDefault(
			_ISSUE_REDIRECT_CHAIN, Collections.emptySet());

		return Arrays.asList(
			new JSONObject(
			).put(
				"category", "linksAndURLs"
			).put(
				"classification", "warning"
			).put(
				"description",
				StringBundler.concat(
					"This page has one or more internal links passing through ",
					"more than one redirect before reaching the final URL. ",
					"Each extra hop adds latency, dilutes link equity ",
					"slightly, and risks the chain breaking if any ",
					"intermediate redirect is later removed.")
			).put(
				"fixHint",
				StringBundler.concat(
					"Update the source link or redirect rule so it points ",
					"directly to the terminal URL in one hop. Flattening ",
					"chains is a one time hygiene fix that pays off in faster ",
					"navigation and more durable links.")
			).put(
				"name", "redirectChains"
			).put(
				"pageURLs",
				_getPageURLs(linkedURLPageURLsMap, redirectChainURLs)
			).put(
				"severity", "2"
			));
	}

	private String _getRedirectIssue(URI domainURI, String url) {
		try {
			String redirectURL = url;

			for (int hopCount = 0; hopCount <= 10; hopCount++) {
				HttpResponse<Void> httpResponse = _getHttpResponse(redirectURL);

				int statusCode = httpResponse.statusCode();

				boolean redirect = false;

				if ((statusCode >= HttpURLConnection.HTTP_MULT_CHOICE) &&
					(statusCode < HttpURLConnection.HTTP_BAD_REQUEST)) {

					redirect = true;
				}

				String locationURL = null;

				if (redirect) {
					HttpHeaders httpHeaders = httpResponse.headers();

					locationURL = httpHeaders.firstValue(
						"Location"
					).orElse(
						null
					);
				}

				if (Validator.isNull(locationURL)) {
					if (redirect ||
						(statusCode == HttpURLConnection.HTTP_BAD_METHOD) ||
						(statusCode == HttpURLConnection.HTTP_BAD_REQUEST) ||
						(statusCode == HttpURLConnection.HTTP_GONE) ||
						(statusCode == HttpURLConnection.HTTP_NOT_FOUND)) {

						return _ISSUE_BROKEN_INTERNAL_LINK;
					}

					if (hopCount > 1) {
						return _ISSUE_REDIRECT_CHAIN;
					}

					return null;
				}

				URI uri = URI.create(redirectURL);

				String resolvedURL = String.valueOf(uri.resolve(locationURL));

				if (!_isInternalURL(domainURI, resolvedURL)) {
					return null;
				}

				redirectURL = resolvedURL;
			}

			return _ISSUE_REDIRECT_CHAIN;
		}
		catch (Exception exception) {
			if (_isBrokenLinkException(exception)) {
				if (_log.isWarnEnabled()) {
					_log.warn("Unable to reach URL " + url, exception);
				}

				return _ISSUE_BROKEN_INTERNAL_LINK;
			}

			if (_log.isWarnEnabled()) {
				_log.warn("Unable to check URL " + url, exception);
			}

			return null;
		}
	}

	private boolean _isBrokenLinkException(Throwable throwable) {
		if (throwable == null) {
			return false;
		}

		if (throwable instanceof ConnectException ||
			throwable instanceof IllegalArgumentException ||
			throwable instanceof UnknownHostException) {

			return true;
		}

		return _isBrokenLinkException(throwable.getCause());
	}

	private boolean _isInternalURL(URI domainURI, String url) {
		if (Validator.isNull(url)) {
			return false;
		}

		try {
			URI uri = URI.create(url);

			String domainHost = domainURI.getHost();
			String host = uri.getHost();

			if (Validator.isNull(domainHost) || Validator.isNull(host)) {
				return false;
			}

			if (StringUtil.equalsIgnoreCase(host, domainHost) &&
				(uri.getPort() == domainURI.getPort())) {

				return true;
			}

			return false;
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug("Unable to parse URL " + url, exception);
			}

			return false;
		}
	}

	private boolean _isSitemapReachable(String sitemapURL) {
		try {
			HttpResponse<String> httpResponse = _httpClient.send(
				HttpRequest.newBuilder(
					URI.create(sitemapURL)
				).timeout(
					Duration.ofSeconds(60)
				).GET(
				).build(),
				HttpResponse.BodyHandlers.ofString());

			if (httpResponse.statusCode() != HttpURLConnection.HTTP_OK) {
				return false;
			}

			return Validator.isNotNull(httpResponse.body());
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to reach the sitemap at " + sitemapURL, exception);
			}

			return false;
		}
	}

	private void _patchSEOStudioScan(JSONObject seoStudioScanJSONObject)
		throws Exception {

		String executionId = seoStudioScanJSONObject.optString("executionId");

		if (Validator.isNull(executionId)) {
			return;
		}

		String state = null;

		Job job = _kubernetesJobService.getJob(executionId);

		if (job == null) {
			state = SEOStudioScanConstants.STATE_FAILED;
		}
		else {
			JobStatus jobStatus = job.getStatus();

			if (jobStatus != null) {
				if (GetterUtil.getInteger(jobStatus.getActive()) > 0) {
					state = SEOStudioScanConstants.STATE_RUNNING;
				}

				if (GetterUtil.getInteger(jobStatus.getFailed()) > 0) {
					state = SEOStudioScanConstants.STATE_FAILED;
				}

				if (GetterUtil.getInteger(jobStatus.getSucceeded()) > 0) {
					state = SEOStudioScanConstants.STATE_COMPLETED;
				}
			}
		}

		if (Validator.isNull(state) ||
			state.equals(seoStudioScanJSONObject.optString("state"))) {

			return;
		}

		long seoStudioScanId = seoStudioScanJSONObject.getLong("id");

		if (state.equals(SEOStudioScanConstants.STATE_COMPLETED)) {
			long seoStudioDomainId = _seoStudioService.getSEOStudioDomainId(
				seoStudioScanJSONObject);

			JSONObject seoStudioDomainJSONObject =
				_seoStudioService.fetchSEOStudioDomainJSONObject(
					seoStudioDomainId);

			if (seoStudioDomainJSONObject == null) {
				_seoStudioService.patchSEOStudioScan(
					"Unable to get a domain for SEO Studio domain ID " +
						seoStudioDomainId,
					seoStudioScanId, SEOStudioScanConstants.STATE_FAILED);

				return;
			}

			List<CrawlHit> crawlHits = _seoStudioService.getCrawlHits(
				seoStudioDomainId);

			if (ListUtil.isEmpty(crawlHits)) {
				_seoStudioService.patchSEOStudioScan(
					"Unable to get crawl hits for SEO Studio domain ID " +
						seoStudioDomainId,
					seoStudioScanId, SEOStudioScanConstants.STATE_FAILED);

				return;
			}

			String domainURL = _seoStudioService.toDomainURL(
				_seoStudioService.toCrawlURI(
					seoStudioDomainJSONObject.getString("hostname")));

			URI domainURI = URI.create(domainURL);

			Map<String, Set<String>> linkedURLPageURLsMap =
				_getLinkedURLPageURLsMap(crawlHits, domainURI);

			Map<String, Set<String>> issueURLsMap = _getIssueURLsMap(
				domainURI, linkedURLPageURLsMap.keySet());

			List<JSONObject> insightJSONObjects = new ArrayList<>();

			insightJSONObjects.addAll(
				_getBrokenInternalLinksInsightJSONObjects(
					issueURLsMap, linkedURLPageURLsMap));
			insightJSONObjects.addAll(
				_getMetadataInsightJSONObjects(crawlHits));
			insightJSONObjects.addAll(
				_getOrphanPagesInsightJSONObjects(crawlHits, domainURL));
			insightJSONObjects.addAll(
				_getRedirectChainsInsightJSONObjects(
					issueURLsMap, linkedURLPageURLsMap));

			long accountEntryId = seoStudioScanJSONObject.getLong(
				"r_accountToSEOStudioScans_accountEntryId");

			for (JSONObject insightJSONObject : insightJSONObjects) {
				_seoStudioService.postSEOStudioScanInsightsBatch(
					accountEntryId, insightJSONObject, seoStudioScanId);
			}

			_seoStudioService.patchSEOStudioScan(
				null, seoStudioScanId, SEOStudioScanConstants.STATE_COMPLETED);
		}
		else if (state.equals(SEOStudioScanConstants.STATE_FAILED)) {
			String errorMessage = null;

			if (job == null) {
				errorMessage = "Kubernetes job does not exist";
			}
			else {
				JobStatus jobStatus = job.getStatus();

				for (JobCondition jobCondition : jobStatus.getConditions()) {
					if (!Objects.equals(jobCondition.getType(), "Failed") ||
						!Objects.equals(jobCondition.getStatus(), "True")) {

						continue;
					}

					if (Validator.isNotNull(jobCondition.getMessage())) {
						errorMessage = jobCondition.getMessage();

						break;
					}
				}

				if (errorMessage == null) {
					errorMessage = "Kubernetes job failed";
				}
			}

			_seoStudioService.patchSEOStudioScan(
				errorMessage, seoStudioScanId,
				SEOStudioScanConstants.STATE_FAILED);
		}
	}

	private static final String _ISSUE_BROKEN_INTERNAL_LINK =
		"brokenInternalLink";

	private static final String _ISSUE_REDIRECT_CHAIN = "redirectChain";

	private static final Log _log = LogFactory.getLog(
		CrawlerRestController.class);

	private final ExecutorService _executorService =
		Executors.newFixedThreadPool(8);
	private final HttpClient _httpClient = HttpClient.newBuilder(
	).connectTimeout(
		Duration.ofSeconds(5)
	).followRedirects(
		HttpClient.Redirect.NORMAL
	).build();

	@Autowired
	private KubernetesJobService _kubernetesJobService;

	private final HttpClient _noRedirectHttpClient = HttpClient.newBuilder(
	).connectTimeout(
		Duration.ofSeconds(5)
	).followRedirects(
		HttpClient.Redirect.NEVER
	).build();

	@Autowired
	private SEOStudioService _seoStudioService;

}