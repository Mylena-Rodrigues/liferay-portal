/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.model;

import org.json.JSONArray;

/**
 * @author Carolina Barbosa
 */
public class CrawlerConfiguration {

	public CrawlerConfiguration(
		JSONArray domainsJSONArray, String indexName, int maxCrawlDepth,
		int maxDuration, int maxLinksPerPage, int maxUniqueURLsCount) {

		_domainsJSONArray = domainsJSONArray;
		_indexName = indexName;
		_maxCrawlDepth = maxCrawlDepth;
		_maxDuration = maxDuration;
		_maxLinksPerPage = maxLinksPerPage;
		_maxUniqueURLsCount = maxUniqueURLsCount;
	}

	public JSONArray getDomainsJSONArray() {
		return _domainsJSONArray;
	}

	public String getIndexName() {
		return _indexName;
	}

	public int getMaxCrawlDepth() {
		return _maxCrawlDepth;
	}

	public int getMaxDuration() {
		return _maxDuration;
	}

	public int getMaxLinksPerPage() {
		return _maxLinksPerPage;
	}

	public int getMaxUniqueURLsCount() {
		return _maxUniqueURLsCount;
	}

	private final JSONArray _domainsJSONArray;
	private final String _indexName;
	private final int _maxCrawlDepth;
	private final int _maxDuration;
	private final int _maxLinksPerPage;
	private final int _maxUniqueURLsCount;

}