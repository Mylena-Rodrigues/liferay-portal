/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.seo.studio.service;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.seo.studio.model.CrawlHit;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONObject;

import org.springframework.stereotype.Component;

/**
 * @author Brooke Dalton
 */
@Component
public class SEOStudioMetadataInsightService {

	public List<JSONObject> getInsightJSONObjects(List<CrawlHit> crawlHits) {
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

}