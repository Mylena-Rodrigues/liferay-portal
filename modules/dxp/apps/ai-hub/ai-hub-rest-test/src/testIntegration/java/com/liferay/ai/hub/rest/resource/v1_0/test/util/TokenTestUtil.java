package com.liferay.ai.hub.rest.resource.v1_0.test.util;

import com.liferay.ai.hub.configuration.AIHubConfiguration;
import com.liferay.oauth2.provider.constants.ClientProfile;
import com.liferay.oauth2.provider.constants.GrantType;
import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.service.OAuth2ApplicationLocalService;
import com.liferay.oauth2.provider.util.OAuth2SecureRandomGenerator;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.Http;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class TokenTestUtil {
	public static JSONObject generate() throws Exception {
		User user = TestPropsValues.getUser();

		OAuth2Application oAuth2Application =
			_oAuth2ApplicationLocalService.addOAuth2Application(
				user.getCompanyId(), user.getUserId(), user.getFullName(),
				List.of(GrantType.CLIENT_CREDENTIALS), "client_secret_post",
				user.getUserId(),
				OAuth2SecureRandomGenerator.generateClientId(),
				ClientProfile.WEB_APPLICATION.id(),
				OAuth2SecureRandomGenerator.generateClientSecret(), "",
				List.of(), "http://localhost:8080", 0, null, "AI Hub", "",
				List.of("http://localhost:8080"), false,
				Arrays.asList("Liferay.AI.Hub.REST.everything"), false,
				new ServiceContext());

		ConfigurationTestUtil.saveConfiguration(
			AIHubConfiguration.class.getName(),
			HashMapDictionaryBuilder.<String, Object>put(
				"clientId", oAuth2Application.getClientId()
			).put(
				"clientSecret", oAuth2Application.getClientSecret()
			).put(
				"serviceURL", "http://localhost:8080"
			).build());

		return HTTPTestUtil.invokeToJSONObject(
			null, "ai-hub/v1.0/tokens", Http.Method.POST);
	}

	public static HashMap<String, String> getAuthorizationTokens() throws Exception {
		JSONObject token = generate();

		if(!token.has("accessToken") && !token.has("userToken") ){
			return null;
		}

		return HashMapBuilder.put(
			"Authorization", "Bearer " + token.getString("accessToken")
		).put(
			"Liferay-AI-Hub-On-Behalf-Of", token.getString("userToken")
		).build();
	}

	private static OAuth2ApplicationLocalService _oAuth2ApplicationLocalService;
}
