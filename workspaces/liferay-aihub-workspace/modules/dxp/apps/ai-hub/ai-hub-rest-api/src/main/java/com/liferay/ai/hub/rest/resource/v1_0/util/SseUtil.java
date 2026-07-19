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
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * @author Feliphe Marinho
 */
public class SseUtil {

	public static void broadcastHeartbeat(Consumer<String> consumer) {
		for (Map.Entry<String, SseEventSink> entry :
				_sseEventSinks.entrySet()) {

			SseEventSink sseEventSink = entry.getValue();

			if (sseEventSink.isClosed()) {
				_remove(entry.getKey());

				consumer.accept(entry.getKey());

				continue;
			}

			Sse sse = _sses.get(entry.getKey());

			sseEventSink.send(
				sse.newEventBuilder(
				).comment(
					"heartbeat"
				).build());
		}
	}

	public static void closeAll() {
		if (_sseEventSinks.isEmpty() || !PortalRunMode.isTestMode()) {
			return;
		}

		_sseEventSinks.forEach((__, sseEventSink) -> sseEventSink.close());

		_sseEventSinks = new ConcurrentHashMap<>();
		_sses = new ConcurrentHashMap<>();
	}

	public static Set<String> getSSEEventSinksKeys() {
		if (!PortalRunMode.isTestMode()) {
			return null;
		}

		return _sseEventSinks.keySet();
	}

	public static void initialize(Sse sse, SseEventSink sseEventSink) {
		String sseEventSinkKey = PortalUUIDUtil.generate();

		_sseEventSinks.put(sseEventSinkKey, sseEventSink);
		_sses.put(sseEventSinkKey, sse);

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

		if (_sendLocally(
				jsonObject.toString(), name, nodeName, sseEventSinkKey)) {

			return;
		}

		if (!ClusterExecutorUtil.isEnabled()) {
			if (_log.isErrorEnabled()) {
				_log.error("No SSE event sink found " + sseEventSinkKey);
			}

			return;
		}

		ClusterRequest clusterRequest = ClusterRequest.createMulticastRequest(
			new MethodHandler(
				_methodKey, data, name, nodeName, sseEventSinkKey),
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

	private static void _remove(String sseEventSinkKey) {
		_sseEventSinks.remove(sseEventSinkKey);
		_sses.remove(sseEventSinkKey);
	}

	private static boolean _sendLocally(
		String data, String name, String nodeName, String sseEventSinkKey) {

		SseEventSink sseEventSink = _sseEventSinks.get(sseEventSinkKey);

		if (sseEventSink == null) {
			return false;
		}

		if (sseEventSink.isClosed()) {
			_remove(sseEventSinkKey);

			_log.error("SSE Event Sink is closed " + sseEventSinkKey);

			return true;
		}

		Sse sse = _sses.get(sseEventSinkKey);

		sseEventSink.send(
			sse.newEventBuilder(
			).data(
				String.class, data
			).name(
				Validator.isBlank(name) ? nodeName : name
			).build());

		return true;
	}

	private static final Log _log = LogFactoryUtil.getLog(SseUtil.class);

	private static final MethodKey _methodKey = new MethodKey(
		SseUtil.class, "_sendLocally", String.class, String.class, String.class,
		String.class);
	private static Map<String, SseEventSink> _sseEventSinks =
		new ConcurrentHashMap<>();
	private static Map<String, Sse> _sses = new ConcurrentHashMap<>();

}