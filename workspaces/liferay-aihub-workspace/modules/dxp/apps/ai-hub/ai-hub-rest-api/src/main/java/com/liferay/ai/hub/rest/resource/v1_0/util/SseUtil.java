/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.rest.resource.v1_0.util;

import com.liferay.portal.kernel.cluster.ClusterExecutorUtil;
import com.liferay.portal.kernel.cluster.ClusterRequest;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;
import com.liferay.portal.kernel.util.PortalRunMode;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;

import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseEventSink;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * @author Feliphe Marinho
 */
public class SseUtil {

	public static void broadcastHeartbeat(Consumer<String> consumer) {
		for (Map.Entry<String, SseContext> entry : _sseContexts.entrySet()) {
			SseContext sseContext = entry.getValue();

			SseEventSink sseEventSink = sseContext.getSseEventSink();

			if (sseEventSink.isClosed()) {
				_close(consumer, sseEventSink, entry.getKey());

				continue;
			}

			try {
				Sse sse = sseContext.getSse();

				CompletionStage<?> completionStage = sseEventSink.send(
					sse.newEventBuilder(
					).comment(
						"heartbeat"
					).build());

				completionStage.whenComplete(
					(result, throwable) -> {
						if (throwable != null) {
							_close(consumer, sseEventSink, entry.getKey());
						}
					});
			}
			catch (RuntimeException runtimeException) {
				_close(consumer, sseEventSink, entry.getKey());

				_log.error(runtimeException);
			}
		}
	}

	public static void closeAll() {
		if (_sseContexts.isEmpty() || !PortalRunMode.isTestMode()) {
			return;
		}

		_sseContexts.forEach(
			(__, sseContext) -> {
				SseEventSink sseEventSink = sseContext.getSseEventSink();

				sseEventSink.close();
			});

		_sseContexts = new ConcurrentHashMap<>();
	}

	public static Set<String> getSSEEventSinksKeys() {
		if (!PortalRunMode.isTestMode()) {
			return null;
		}

		return _sseContexts.keySet();
	}

	public static void initialize(Sse sse, SseEventSink sseEventSink) {
		String sseEventSinkKey = PortalUUIDUtil.generate();

		_sseContexts.put(sseEventSinkKey, new SseContext(sse, sseEventSink));

		sseEventSink.send(
			sse.newEventBuilder(
			).data(
				String.class, sseEventSinkKey
			).name(
				"Subscribe"
			).build());
	}

	public static void send(
		String data, String name, String nodeName, String sseEventSinkKey) {

		send(null, data, name, nodeName, sseEventSinkKey);
	}

	public static void send(
		String[] agentDefinitionExternalReferenceCodes, String data,
		String name, String nodeName, JSONObject propertiesJSONObject,
		String sseEventSinkKey, String type) {

		if (Validator.isBlank(sseEventSinkKey)) {
			return;
		}

		JSONObject jsonObject = propertiesJSONObject;

		if (jsonObject == null) {
			jsonObject = JSONFactoryUtil.createJSONObject();
		}

		jsonObject.put(
			"agentDefinitionExternalReferenceCodes",
			() -> {
				if (agentDefinitionExternalReferenceCodes == null) {
					return null;
				}

				return JSONUtil.putAll(agentDefinitionExternalReferenceCodes);
			}
		).put(
			"data", data
		).put(
			"nodeName", nodeName
		).put(
			"type", type
		);

		if (_send(jsonObject.toString(), name, nodeName, sseEventSinkKey)) {
			return;
		}

		if (!ClusterExecutorUtil.isEnabled()) {
			_log.error("No SSE event sink found " + sseEventSinkKey);

			return;
		}

		ClusterRequest clusterRequest = ClusterRequest.createMulticastRequest(
			new MethodHandler(
				_methodKey, jsonObject.toString(), name, nodeName,
				sseEventSinkKey),
			true);

		clusterRequest.setFireAndForget(true);

		ClusterExecutorUtil.execute(clusterRequest);
	}

	public static void send(
		String[] agentDefinitionExternalReferenceCodes, String data,
		String name, String nodeName, String sseEventSinkKey) {

		send(
			agentDefinitionExternalReferenceCodes, data, name, nodeName, null,
			sseEventSinkKey, "text");
	}

	private static void _close(
		Consumer<String> consumer, SseEventSink sseEventSink,
		String sseEventSinkKey) {

		_sseContexts.remove(sseEventSinkKey);

		if (!sseEventSink.isClosed()) {
			sseEventSink.close();
		}

		if (consumer != null) {
			consumer.accept(sseEventSinkKey);
		}
	}

	private static boolean _send(
		String data, String name, String nodeName, String sseEventSinkKey) {

		SseContext sseContext = _sseContexts.get(sseEventSinkKey);

		if (sseContext == null) {
			return false;
		}

		SseEventSink sseEventSink = sseContext.getSseEventSink();

		if (sseEventSink.isClosed()) {
			_sseContexts.remove(sseEventSinkKey);

			_log.error("SSE Event Sink is closed " + sseEventSinkKey);

			return true;
		}

		try {
			Sse sse = sseContext.getSse();

			sseEventSink.send(
				sse.newEventBuilder(
				).data(
					String.class, data
				).name(
					Validator.isBlank(name) ? nodeName : name
				).build());
		}
		catch (RuntimeException runtimeException) {
			_close(null, sseEventSink, sseEventSinkKey);

			_log.error(runtimeException);
		}

		return true;
	}

	private static final Log _log = LogFactoryUtil.getLog(SseUtil.class);

	private static final MethodKey _methodKey = new MethodKey(
		SseUtil.class, "_send", String.class, String.class, String.class,
		String.class);
	private static Map<String, SseContext> _sseContexts =
		new ConcurrentHashMap<>();

	private static class SseContext {

		public SseContext(Sse sse, SseEventSink sseEventSink) {
			_sse = sse;
			_sseEventSink = sseEventSink;
		}

		public Sse getSse() {
			return _sse;
		}

		public SseEventSink getSseEventSink() {
			return _sseEventSink;
		}

		private final Sse _sse;
		private final SseEventSink _sseEventSink;

	}

}