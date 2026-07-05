/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {EventSource} from 'eventsource';
import {useCallback, useEffect, useRef, useState} from 'react';

import {DEMO_ENABLED, getMockAgentData} from '../agents/_demo/mockAgent';
import {createAgentEventSource, postAgentInstance} from './agentApi';
import {AgentStatus, EAgent} from './types';

/**
 * Generalized version of useCategorizationAgent: opens an SSE connection,
 * invokes an agent instance by external reference code and exposes the raw
 * result payload. Each agent balloon interprets `data` for its own shape, so
 * this hook stays agent-agnostic.
 */
export default function useAgent<T = unknown>(agent: EAgent) {
	const [data, setData] = useState<T>();
	const [error, setError] = useState<string>();
	const [status, setStatus] = useState<AgentStatus>('idle');

	const connectingRef = useRef<boolean>(false);
	const eventSourceRef = useRef<EventSource | null>(null);
	const lastContextRef = useRef<Record<string, unknown> | null>(null);
	const mountedRef = useRef<boolean>(true);
	const pendingRef = useRef<boolean>(false);
	const sseEventSinkKeyRef = useRef<string | null>(null);

	const closeEventSource = useCallback(() => {
		eventSourceRef.current?.close();
		eventSourceRef.current = null;
		sseEventSinkKeyRef.current = null;
	}, []);

	const invoke = useCallback(
		async (context: Record<string, unknown>) => {
			try {
				await postAgentInstance({
					agent,
					context,
					sseEventSinkKey: sseEventSinkKeyRef.current as string,
				});
			}
			catch {
				setError(Liferay.Language.get('an-unexpected-error-occurred'));
				setStatus('error');

				closeEventSource();
			}
		},
		[agent, closeEventSource]
	);

	const connect = useCallback(() => {
		if (eventSourceRef.current || connectingRef.current) {
			return;
		}

		connectingRef.current = true;

		createAgentEventSource()
			.then((eventSource) => {
				connectingRef.current = false;

				if (!mountedRef.current) {
					eventSource?.close();

					return;
				}

				if (!eventSource) {
					pendingRef.current = false;

					setStatus('idle');

					return;
				}

				eventSourceRef.current = eventSource;

				eventSource.addEventListener('Subscribe', (event) => {
					sseEventSinkKeyRef.current = event.data;

					if (pendingRef.current && lastContextRef.current) {
						pendingRef.current = false;

						invoke(lastContextRef.current);
					}
				});

				eventSource.addEventListener(agent, (event) => {
					try {
						const dataJSON = JSON.parse(event.data);
						const parsed = JSON.parse(dataJSON.data ?? 'null') as T;

						setData(parsed);
						setStatus(parsed ? 'ready' : 'empty');
					}
					catch {
						setError(
							Liferay.Language.get('an-unexpected-error-occurred')
						);
						setStatus('error');
					}

					closeEventSource();
				});

				eventSource.addEventListener(
					'Agent Invocation Failed',
					(event) => {
						let text = '';

						try {
							text = JSON.parse(event.data).data;
						}
						catch {
							text = '';
						}

						setError(
							text ||
								Liferay.Language.get(
									'an-unexpected-error-occurred'
								)
						);
						setStatus('error');

						closeEventSource();
					}
				);
			})
			.catch(() => {
				connectingRef.current = false;
				pendingRef.current = false;

				if (!mountedRef.current) {
					return;
				}

				setError(Liferay.Language.get('an-unexpected-error-occurred'));
				setStatus('error');
			});
	}, [agent, closeEventSource, invoke]);

	const run = useCallback(
		(context: Record<string, unknown>) => {
			lastContextRef.current = context;

			setData(undefined);
			setError(undefined);
			setStatus('loading');

			if (DEMO_ENABLED) {
				setTimeout(() => {
					if (!mountedRef.current) {
						return;
					}

					const mock = getMockAgentData(agent) as T;

					setData(mock);
					setStatus(mock ? 'ready' : 'empty');
				}, 900);

				return;
			}

			if (sseEventSinkKeyRef.current) {
				invoke(context);
			}
			else {
				pendingRef.current = true;

				connect();
			}
		},
		[agent, connect, invoke]
	);

	const regenerate = useCallback(() => {
		if (lastContextRef.current) {
			run(lastContextRef.current);
		}
	}, [run]);

	const reset = useCallback(() => {
		setData(undefined);
		setError(undefined);
		setStatus('idle');
	}, []);

	useEffect(() => {
		mountedRef.current = true;

		return () => {
			mountedRef.current = false;
		};
	}, []);

	useEffect(() => {
		return () => {
			pendingRef.current = false;

			closeEventSource();
		};
	}, [agent, closeEventSource]);

	return {data, error, regenerate, reset, run, status};
}
