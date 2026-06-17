/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.internal.sse;

import com.liferay.ai.hub.internal.memory.ChatMemoryProviderUtil;
import com.liferay.ai.hub.rest.resource.v1_0.util.SseUtil;

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
			() -> SseUtil.broadcastHeartbeat(
				ChatMemoryProviderUtil::deleteMessages),
			15, 15, TimeUnit.SECONDS);
	}

	@Deactivate
	protected void deactivate() {
		_scheduledExecutorService.shutdownNow();
	}

	private ScheduledExecutorService _scheduledExecutorService;

}