/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.seo.studio.service;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.seo.studio.model.CrawlHit;

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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.json.JSONObject;

import org.springframework.stereotype.Component;

/**
 * @author Brooke Dalton
 */
@Component
public class SEOStudioLinksAndURLsInsightService {

	public List<JSONObject> getInsightJSONObjects(
			List<CrawlHit> crawlHits, String domainURL)
		throws Exception {

		URI domainURI = URI.create(domainURL);

		Map<String, Set<String>> linkedURLPageURLsMap =
			_getLinkedURLPageURLsMap(crawlHits, domainURI);

		Map<String, Set<String>> issueURLsMap = _getIssueURLsMap(
			domainURI, linkedURLPageURLsMap.keySet());

		return Arrays.asList(
			_getBrokenInternalLinksInsightJSONObject(
				issueURLsMap, linkedURLPageURLsMap),
			_getOrphanPagesInsightJSONObject(crawlHits, domainURL),
			_getRedirectChainsInsightJSONObject(
				issueURLsMap, linkedURLPageURLsMap),
			_getRedirectLoopsInsightJSONObject(
				issueURLsMap, linkedURLPageURLsMap),
			_getUnfriendlyURLsInsightJSONObject(crawlHits));
	}

	@PreDestroy
	public void preDestroy() {
		_executorService.shutdown();
	}

	private JSONObject _getBrokenInternalLinksInsightJSONObject(
		Map<String, Set<String>> issueURLsMap,
		Map<String, Set<String>> linkedURLPageURLsMap) {

		Set<String> brokenInternalLinkURLs = issueURLsMap.getOrDefault(
			_ISSUE_BROKEN_INTERNAL_LINK, Collections.emptySet());

		return new JSONObject(
		).put(
			"category", "linksAndURLs"
		).put(
			"classification", "problem"
		).put(
			"description",
			StringBundler.concat(
				"This page has one or more internal links pointing to URLs ",
				"that cannot be retrieved because they are unreachable, ",
				"return 400, 404, 405, or 410, or redirect without a ",
				"destination. Broken internal links waste link equity, ",
				"frustrate users, and produce dead end paths through your ",
				"content.")
		).put(
			"fixHint",
			StringBundler.concat(
				"For each broken link, either update the href to point to the ",
				"correct destination, or remove the link if no equivalent ",
				"destination exists. If many links point to one missing URL, ",
				"restoring or redirecting that URL once fixes them all.")
		).put(
			"name", "brokenInternalLinks"
		).put(
			"pageURLs",
			_getPageURLs(linkedURLPageURLsMap, brokenInternalLinkURLs)
		).put(
			"severity", "3"
		);
	}

	private HttpResponse<Void> _getHttpResponse(String url) throws Exception {
		HttpRequest httpRequest = HttpRequest.newBuilder(
			URI.create(url)
		).method(
			"HEAD", HttpRequest.BodyPublishers.noBody()
		).timeout(
			Duration.ofSeconds(10)
		).build();

		HttpResponse<Void> httpResponse = _httpClient.send(
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

		return _httpClient.send(
			httpRequest, HttpResponse.BodyHandlers.discarding());
	}

	private String _getIssue(URI domainURI, String url) {
		try {
			String redirectURL = url;

			Set<String> visitedURLs = new LinkedHashSet<>();

			for (int hopCount = 0; hopCount <= 10; hopCount++) {
				if (!visitedURLs.add(redirectURL)) {
					return _ISSUE_REDIRECT_LOOP;
				}

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

	private Map<String, Set<String>> _getIssueURLsMap(
			URI domainURI, Set<String> linkedURLs)
		throws Exception {

		Map<String, Set<String>> issueURLsMap = new ConcurrentHashMap<>();

		List<Future<?>> futures = TransformUtil.transform(
			linkedURLs,
			linkedURL -> _executorService.submit(
				() -> {
					String issue = _getIssue(domainURI, linkedURL);

					if (Validator.isNull(issue)) {
						return;
					}

					Set<String> issueURLs = issueURLsMap.computeIfAbsent(
						issue, key -> ConcurrentHashMap.newKeySet());

					issueURLs.add(linkedURL);
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

	private JSONObject _getOrphanPagesInsightJSONObject(
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

		return new JSONObject(
		).put(
			"category", "linksAndURLs"
		).put(
			"classification", "problem"
		).put(
			"description",
			StringBundler.concat(
				"This page is published and indexable but has zero internal ",
				"links pointing to it. Orphan pages are nearly invisible to ",
				"both users browsing the site and crawlers building the link ",
				"graph. Even when they are listed in a sitemap, they collect ",
				"very little ranking authority.")
		).put(
			"fixHint",
			StringBundler.concat(
				"Identify 2-5 topically related pages and add contextual ",
				"internal links pointing to the orphan, with descriptive ",
				"anchor text. If no relevant linking context exists anywhere ",
				"on the site, that is a signal the page may not belong in the ",
				"public site at all.")
		).put(
			"name", "orphanPages"
		).put(
			"pageURLs", pageURLs
		).put(
			"severity", "2"
		);
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

	private JSONObject _getRedirectChainsInsightJSONObject(
		Map<String, Set<String>> issueURLsMap,
		Map<String, Set<String>> linkedURLPageURLsMap) {

		Set<String> redirectChainURLs = issueURLsMap.getOrDefault(
			_ISSUE_REDIRECT_CHAIN, Collections.emptySet());

		return new JSONObject(
		).put(
			"category", "linksAndURLs"
		).put(
			"classification", "warning"
		).put(
			"description",
			StringBundler.concat(
				"This page has one or more internal links passing through ",
				"more than one redirect before reaching the final URL. Each ",
				"extra hop adds latency, dilutes link equity slightly, and ",
				"risks the chain breaking if any intermediate redirect is ",
				"later removed.")
		).put(
			"fixHint",
			StringBundler.concat(
				"Update the source link or redirect rule so it points ",
				"directly to the terminal URL in one hop. Flattening chains ",
				"is a one time hygiene fix that pays off in faster navigation ",
				"and more durable links.")
		).put(
			"name", "redirectChains"
		).put(
			"pageURLs", _getPageURLs(linkedURLPageURLsMap, redirectChainURLs)
		).put(
			"severity", "2"
		);
	}

	private JSONObject _getRedirectLoopsInsightJSONObject(
		Map<String, Set<String>> issueURLsMap,
		Map<String, Set<String>> linkedURLPageURLsMap) {

		Set<String> redirectLoopURLs = issueURLsMap.getOrDefault(
			_ISSUE_REDIRECT_LOOP, Collections.emptySet());

		return new JSONObject(
		).put(
			"category", "linksAndURLs"
		).put(
			"classification", "problem"
		).put(
			"description",
			StringBundler.concat(
				"This page has one or more internal links entering a redirect ",
				"cycle that points back to itself. Loops produce an immediate ",
				"browser error (\"too many redirects\") and block both users ",
				"and crawlers from reaching any page in the cycle.")
		).put(
			"fixHint",
			StringBundler.concat(
				"Trace the cycle, identify which redirect rule should be ",
				"removed or rewritten to break it, and confirm the final ",
				"destination resolves to a real 200 page. Redirect loops are ",
				"always editorial choices, so there is no safe automated fix.")
		).put(
			"name", "redirectLoops"
		).put(
			"pageURLs", _getPageURLs(linkedURLPageURLsMap, redirectLoopURLs)
		).put(
			"severity", "2"
		);
	}

	private JSONObject _getUnfriendlyURLsInsightJSONObject(
		List<CrawlHit> crawlHits) {

		Set<String> pageURLs = new LinkedHashSet<>();

		for (CrawlHit crawlHit : crawlHits) {
			String canonicalURL = crawlHit.getCanonicalURL();

			if (Validator.isNull(canonicalURL)) {
				continue;
			}

			try {
				URI uri = URI.create(canonicalURL);

				String path = uri.getPath();

				if (Validator.isNull(path)) {
					continue;
				}

				Matcher friendlyPathMatcher = _friendlyPathPattern.matcher(
					path);

				String[] pathSegments = ArrayUtil.filter(
					StringUtil.split(path, CharPool.SLASH),
					Validator::isNotNull);

				Matcher uppercaseMatcher = _uppercasePattern.matcher(path);

				if (Validator.isNotNull(uri.getQuery()) ||
					(path.length() > 100) || (pathSegments.length > 4) ||
					!friendlyPathMatcher.matches() || uppercaseMatcher.find()) {

					pageURLs.add(canonicalURL);

					continue;
				}

				for (String pathSegment : pathSegments) {
					Matcher numericMatcher = _numericPattern.matcher(
						pathSegment);
					Matcher uuidMatcher = _uuidPattern.matcher(pathSegment);

					if (numericMatcher.matches() || uuidMatcher.matches()) {
						pageURLs.add(canonicalURL);

						break;
					}
				}
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						"Unable to parse URL " + canonicalURL, exception);
				}

				pageURLs.add(canonicalURL);
			}
		}

		return new JSONObject(
		).put(
			"category", "linksAndURLs"
		).put(
			"classification", "opportunity"
		).put(
			"description",
			StringBundler.concat(
				"This page has a URL that is long, contains numeric IDs or ",
				"query parameters, or uses nondescriptive slugs. SEO friendly ",
				"URLs reinforce topical relevance for both users scanning ",
				"SERP results and search engines parsing link signals.")
		).put(
			"fixHint",
			StringBundler.concat(
				"Rewrite the URL to a short, lowercase, hyphen separated slug ",
				"that reflects the page topic, for example /enterprise-search ",
				"instead of /web/guest/article?id=12345. Set up a 301 ",
				"redirect from the old URL to preserve link equity and avoid ",
				"breaking inbound links.")
		).put(
			"name", "unfriendlyURLs"
		).put(
			"pageURLs", pageURLs
		).put(
			"severity", "2"
		);
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

	private static final String _ISSUE_BROKEN_INTERNAL_LINK =
		"brokenInternalLink";

	private static final String _ISSUE_REDIRECT_CHAIN = "redirectChain";

	private static final String _ISSUE_REDIRECT_LOOP = "redirectLoop";

	private static final Log _log = LogFactory.getLog(
		SEOStudioLinksAndURLsInsightService.class);

	private static final Pattern _friendlyPathPattern = Pattern.compile(
		"[\\p{L}\\p{N}\\-/.]*");
	private static final Pattern _numericPattern = Pattern.compile("[0-9]+");
	private static final Pattern _uppercasePattern = Pattern.compile("\\p{Lu}");
	private static final Pattern _uuidPattern = Pattern.compile(
		".*[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-" +
			"[0-9a-fA-F]{12}.*");

	private final ExecutorService _executorService =
		Executors.newFixedThreadPool(8);
	private final HttpClient _httpClient = HttpClient.newBuilder(
	).connectTimeout(
		Duration.ofSeconds(5)
	).followRedirects(
		HttpClient.Redirect.NEVER
	).build();

}