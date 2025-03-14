/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.learn;

import com.liferay.client.extension.util.spring.boot.BaseRestController;
import com.liferay.client.extension.util.spring.boot.LiferayOAuth2AccessTokenManager;

import java.io.IOException;
import java.nio.charset.Charset;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import reactor.core.publisher.Mono;

import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

/**
 * @author Nilton Vieira
 */
@Component
public class LearnCommandLineRunner
	extends BaseRestController implements CommandLineRunner {

	@Override
	public void run(String... args) throws Exception {
		JSONObject devContent = new JSONObject(
			_get(_liferayOAuth2AccessTokenManager.getAuthorization(
							"external-reference-code-dev"),
				"https://www-dev.liferay.com/o/headless-delivery/v1.0" +
					"/content-structures/2354659/structured-contents?pageSize=-1"
					));

		JSONObject prodContent = new JSONObject(
				_get(_getProdAuth(),
						"https://www.liferay.com/o/headless-delivery/v1.0" +
								"/content-structures/2354659/structured-contents" +
								"?filter=dateModified le 2025-03-03T06:30:00Z&fields=id" +
								"&sort=dateModified:desc&pageSize=-1"
				));

		JSONArray devContentArray = devContent.getJSONArray("items");
		JSONArray prodAllowedContentArray = prodContent.getJSONArray("items");

		HashSet<Long> allowedids = new HashSet<>();

		for(int i = 0; i < prodAllowedContentArray.length(); i++) {
			JSONObject jsonObject = prodAllowedContentArray.getJSONObject(i);

			allowedids.add(jsonObject.getLong("id"));
		}

		JSONArray contentToBeUpdatedArray = new JSONArray();

		for(int i = 0; i < devContentArray.length(); i++) {
			JSONObject current_content = devContentArray.getJSONObject(i);
			if(allowedids.contains(current_content.getLong("id"))) {
				contentToBeUpdatedArray.put(current_content);
			}
		}

		int n = 0;

		System.out.println("\n\n***PRD***\n\n");
		for (int i = 0; i < contentToBeUpdatedArray.length(); i++) {
			n++;
			JSONObject jsonObject1 = contentToBeUpdatedArray.getJSONObject(i);

			_updateWebContent(
				jsonObject1.getLong("id"), jsonObject1);
		}

		System.out.println("\nTotal Count: " + n);
	}

	@Override
	protected String getWebClientBaseURL() {
		return "www-dev.liferay.com";
	}

	private String _get(String authorization, String path) {
		return _getWebClient(
		).get(
		).uri(
			path
		).header(
			HttpHeaders.AUTHORIZATION, authorization
		).header(
			"X-Accept-All-Languages", "true"
		).exchangeToMono(
			_getExchangeToMonoFunction()
		).block();
	}

	private Function<ClientResponse, Mono<String>>
		_getExchangeToMonoFunction() {

		return clientResponse -> {
			org.springframework.http.HttpStatus httpStatus =
				clientResponse.statusCode();

			if (Objects.equals(
					clientResponse.statusCode(),
					org.springframework.http.HttpStatus.NO_CONTENT)) {

				return Mono.just("{}");
			}
			else if (httpStatus.is2xxSuccessful()) {
				return clientResponse.bodyToMono(String.class);
			}
			else if (httpStatus.is4xxClientError() ||
					 httpStatus.is5xxServerError()) {

				return clientResponse.bodyToMono(
					String.class
				).flatMap(
					body -> Mono.error(
						new WebClientResponseException(
							httpStatus.value(), httpStatus.getReasonPhrase(),
							clientResponse.headers(
							).asHttpHeaders(),
							body.getBytes(), null))
				);
			}

			Mono<WebClientResponseException> mono =
				clientResponse.createException();

			return mono.flatMap(Mono::error);
		};
	}

	private String _getAuthAuthorization(
			String client_id, String client_secret, String url) throws Exception {

		HttpPost httpPost = new HttpPost(url);

		httpPost.setEntity(
			new UrlEncodedFormEntity(
				Arrays.asList(
					new BasicNameValuePair(
						"client_id", client_id),
					new BasicNameValuePair(
						"client_secret",
						client_secret),
					new BasicNameValuePair(
						"grant_type", "client_credentials"))));

		httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");

		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();

		try (CloseableHttpClient closeableHttpClient =
				httpClientBuilder.build();
			CloseableHttpResponse closeableHttpResponse =
				closeableHttpClient.execute(httpPost)) {

			StatusLine statusLine = closeableHttpResponse.getStatusLine();

			if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
				JSONObject jsonObject = new JSONObject(
					EntityUtils.toString(
						closeableHttpResponse.getEntity(),
						Charset.defaultCharset()));

				return jsonObject.getString("token_type") + " " +
					jsonObject.getString("access_token");
			}

			throw new Exception("Unable to get OAuth authorization");
		}
	}

	private WebClient _getWebClient() {
		ConnectionProvider connectionProvider = ConnectionProvider.builder(
			"fixed"
		).evictInBackground(
			Duration.ofSeconds(120)
		).maxConnections(
			500
		).maxIdleTime(
			Duration.ofSeconds(20)
		).maxLifeTime(
			Duration.ofSeconds(60)
		).pendingAcquireTimeout(
			Duration.ofSeconds(60)
		).build();

		return WebClient.builder(
		).clientConnector(
			new ReactorClientHttpConnector(
				HttpClient.create(connectionProvider))
		).baseUrl(
			getWebClientBaseURL()
		).defaultHeader(
			HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE
		).defaultHeader(
			HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE
		).exchangeStrategies(
			ExchangeStrategies.builder(
			).codecs(
				clientCodecConfigurer -> clientCodecConfigurer.defaultCodecs(
				).maxInMemorySize(
					16 * 1024 * 1024
				)
			).build()
		).filter(
			getWebClientExchangeFilterFunction()
		).build();
	}

	private String _getProdAuth() throws Exception {
		return 	_getAuthAuthorization(
				"id-credential",
				"secret-credential",
				"oauth-token-url");
	}

	private String _getUATAuth() throws Exception {
		return 	_getAuthAuthorization(
				"id-credential",
				"secret-credential",
				"oauth-token-url");
	}

	private void _updateWebContent(
			Long id, JSONObject devWebContent)
		throws Exception {

		devWebContent.remove("uuid");

		String contentTitle = devWebContent.getString("title");
		JSONObject contentTitle_i18n = devWebContent.getJSONObject("title_i18n");
		JSONArray availableLanguagesList = devWebContent.getJSONArray("availableLanguages");
		String defaultLanguage = getDefaultLanguage(contentTitle, contentTitle_i18n, availableLanguagesList);

		JSONArray customFieldsArray = devWebContent.getJSONArray("customFields");

		for (int i = 0; i < customFieldsArray.length(); i++) {
			JSONObject customFieldJSONObject = customFieldsArray.getJSONObject(i);
			JSONObject customFieldValue = customFieldJSONObject.getJSONObject("customValue");
			JSONObject customFieldValue_i18n = customFieldValue.getJSONObject("data_i18n");

			if (!customFieldJSONObject.has("data") && !customFieldValue_i18n.isEmpty()){
				String firstLanguageValue = null;

				for(int j = 0; j <= availableLanguagesList.length(); j++){
					String cur_availableLanguage = availableLanguagesList.getString(j);

					if(customFieldValue_i18n.has(cur_availableLanguage)){
						firstLanguageValue = customFieldValue_i18n.getString(cur_availableLanguage);

						break;
					};
				}

				customFieldValue.put("data", firstLanguageValue);
				customFieldValue_i18n.put(defaultLanguage, firstLanguageValue);
			}
		}

		JSONArray contentFieldsArray = devWebContent.getJSONArray("contentFields");

		for (int i = 0; i < contentFieldsArray.length(); i++) {
			JSONObject contentFieldJSONObject = contentFieldsArray.getJSONObject(i);

			String name = contentFieldJSONObject.getString("name");


			if (name.equals("content")) {
				contentFieldJSONObject.remove("inputControl");

				contentFieldJSONObject.put("name", "rich_content");
			}

			if (name.equals("additional_about_content")) {
				contentFieldJSONObject.put("name", "additional_about_rich_content");

				contentFieldJSONObject.remove("inputControl");
			}
		}

		try {
			put(
				_getProdAuth(), devWebContent.toString(),
				"https://www.liferay.com/o/headless-delivery/v1.0/structured-contents/" + id);

			System.out.printf("\nUpdated %d ", devWebContent.getLong("id"));
		} catch (Exception e) {
			System.out.printf("\nNOT Updated %d ", devWebContent.getLong("id"));
		}
	}

	private String getDefaultLanguage(String contentTitle, JSONObject contentTitle_i18n, JSONArray availableLanguagesList) {
		String defaultLanguage = null;
		for(int i = 0; i < availableLanguagesList.length(); i++) {
			String contentTitle_i18nValue = contentTitle_i18n.getString(availableLanguagesList.getString(i));
			if(contentTitle_i18nValue.equals(contentTitle)){
				defaultLanguage = availableLanguagesList.getString(i);
				break;
			}
		}
		return defaultLanguage;
	}

	@Autowired
	private LiferayOAuth2AccessTokenManager _liferayOAuth2AccessTokenManager;

	@Value("${liferay.oauth.application.external.reference.codes}")
	private String _liferayOAuthApplicationExternalReferenceCodes;

}