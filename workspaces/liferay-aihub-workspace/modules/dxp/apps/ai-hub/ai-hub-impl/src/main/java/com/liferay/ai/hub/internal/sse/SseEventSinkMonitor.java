/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.internal.sse;

import com.liferay.ai.hub.internal.memory.ChatMemoryProviderUtil;
import com.liferay.ai.hub.rest.resource.v1_0.util.SseUtil;
import com.liferay.portal.kernel.cluster.ClusterExecutorUtil;
import com.liferay.portal.kernel.cluster.ClusterRequest;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author José Abelenda
 * @author Feliphe Marinho
 */
@Component(service = {})
public class SseEventSinkMonitor {

	@Activate
	protected void activate() {
		_scheduledExecutorService =
			Executors.newSingleThreadScheduledExecutor();

		_scheduledExecutorService.scheduleWithFixedDelay(
			() -> {
				try {
					SseUtil.broadcastHeartbeat(this::_deleteMessages);
				}
				catch (Exception exception) {
					_log.error("Unable to broadcast heartbeat", exception);
				}
			},
			15, 15, TimeUnit.SECONDS);
	}

	@Deactivate
	protected void deactivate() {
		_scheduledExecutorService.shutdownNow();
	}

	private void _deleteMessages(String sseEventSinkKey) {
		ChatMemoryProviderUtil.deleteMessages(sseEventSinkKey);

		if (!ClusterExecutorUtil.isEnabled()) {
			return;
		}

		try {
			ClusterRequest clusterRequest =
				ClusterRequest.createMulticastRequest(
					new MethodHandler(
						_deleteMessagesMethodKey, sseEventSinkKey),
					true);

			clusterRequest.setFireAndForget(true);

			ClusterExecutorUtil.execute(clusterRequest);
		}
		catch (Exception exception) {
			_log.error(
				"Unable to delete chat memory across the cluster " +
					sseEventSinkKey,
				exception);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SseEventSinkMonitor.class);

	private static final MethodKey _deleteMessagesMethodKey = new MethodKey(
		ChatMemoryProviderUtil.class, "deleteMessages", Object.class);

	private ScheduledExecutorService _scheduledExecutorService;

}