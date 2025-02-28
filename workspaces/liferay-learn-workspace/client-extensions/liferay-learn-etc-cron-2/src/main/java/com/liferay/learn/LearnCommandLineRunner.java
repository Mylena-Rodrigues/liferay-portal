/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.learn;

import com.liferay.client.extension.util.spring.boot.BaseRestController;
import com.liferay.client.extension.util.spring.boot.LiferayOAuth2AccessTokenManager;

import java.nio.charset.Charset;

import java.time.Duration;

import java.util.Arrays;
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
		JSONObject jsonObject = new JSONObject(
			_get(
				_liferayOAuth2AccessTokenManager.getAuthorization(
					"a7855d10-6a20-1f0e-7e21-53d8654a09d2"),
				"https://www-dev.liferay.com/o/headless-delivery/v1.0" +
					"/content-structures/2354659" +
						"/structured-contents?" +
							"&filter=id eq '232319784'" +
								"&sort=dateModified:asc&pageSize=-1"));

		JSONArray jsonArray = jsonObject.getJSONArray("items");

		int q = 0;

		for (int i = 0; i < jsonArray.length(); i++) {
			q++;
			JSONObject jsonObject1 = jsonArray.getJSONObject(i);

			System.out.println("ID: " + jsonObject1.getLong("id"));

			JSONArray contentFieldsJSONArray = jsonObject1.getJSONArray(
				"contentFields");

			String content = "";
			String additionalAboutContent = "";

			for (int j = 0; j < contentFieldsJSONArray.length(); j++) {
				JSONObject jsonObject2 = contentFieldsJSONArray.getJSONObject(
					j);

				JSONObject contentFieldValueJSONObject =
					jsonObject2.getJSONObject("contentFieldValue");

				String name = jsonObject2.getString("name");

				if (name.equals("content")) {
					content = contentFieldValueJSONObject.getString("data");
				}

				if (name.equals("additional_about_content")) {
					additionalAboutContent =
						contentFieldValueJSONObject.getString("data");
				}
			}

			System.out.println("Content: " + content);
			System.out.println(
				"AdditionalAboutContent: " + additionalAboutContent);

			_updateWebContent(
				jsonObject1.getLong("id"), content, additionalAboutContent);
		}

		System.out.println("Total: " + q);

		//		String prd = get(
		//			_getPRDOAuthAuthorization(),
		//			"https://www.liferay.com/o/headless-delivery/v1.0" +

		// 				"/content-structures/2354659" +

		//					"/structured-contents?sort=dateModified:asc&pageSize=1");
		//
		//		System.out.println("prd: " + prd);
	}

	@Override
	protected String getWebClientBaseURL() {
		return "";
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

	private String _getPRDOAuthAuthorization() throws Exception {
		HttpPost httpPost = new HttpPost(
			"https://www.liferay.com/o/oauth2/token");

		httpPost.setEntity(
			new UrlEncodedFormEntity(
				Arrays.asList(
					new BasicNameValuePair(
						"client_id", "XXXXX"),
					new BasicNameValuePair(
						"client_secret",
						"YYYYY"),
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

				_oauthExpirationMillis =
					jsonObject.getLong("expires_in") * 1000;

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

	private JSONObject _getWebContent(Long id) throws Exception {
		return new JSONObject(
			get(
				_getPRDOAuthAuthorization(),
				"https://www.liferay.com/o/headless-delivery/v1.0" +
					"/structured-contents/" + id));
	}

	private void _updateWebContent(
			Long id, String content, String additionalAboutContent)
		throws Exception {

		System.out.println("\n\n***PRD***\n\n");

		String prdAuthorization = _getPRDOAuthAuthorization();

		JSONObject jsonObject = new JSONObject(
			get(
				prdAuthorization,
				"https://www.liferay.com/o/headless-delivery/v1.0" +
					"/structured-contents/" + id));

		JSONArray jsonArray = jsonObject.getJSONArray("contentFields");

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject contentFieldJSONObject = jsonArray.getJSONObject(i);

			if (contentFieldJSONObject.getString(
					"name"
				).equals(
					"rich_content"
				)) {

				JSONObject contentFieldValueJSONObject =
					contentFieldJSONObject.getJSONObject("contentFieldValue");

				contentFieldValueJSONObject.put("data", content);
			}

			if (contentFieldJSONObject.getString(
					"name"
				).equals(
					"additional_about_rich_content"
				)) {

				JSONObject contentFieldValueJSONObject =
					contentFieldJSONObject.getJSONObject("contentFieldValue");

				contentFieldValueJSONObject.put("data", additionalAboutContent);
			}
		}

		put(
			prdAuthorization, jsonObject.toString(),
			"https://www.liferay.com/o/headless-delivery/v1.0/structured-contents/" +
				id);

		System.out.println(jsonObject);
	}

	@Autowired
	private LiferayOAuth2AccessTokenManager _liferayOAuth2AccessTokenManager;

	@Value("${liferay.oauth.application.external.reference.codes}")
	private String _liferayOAuthApplicationExternalReferenceCodes;

	private long _oauthExpirationMillis;

}