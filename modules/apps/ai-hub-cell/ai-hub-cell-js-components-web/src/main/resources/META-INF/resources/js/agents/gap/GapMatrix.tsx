/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import ClayLabel from '@clayui/label';
import classNames from 'classnames';
import React, {useEffect, useState} from 'react';

import {getGapInsights} from './triggers';
import {
	GAP_MATRIX_UPDATE_EVENT,
	MatrixCell,
	MatrixUpdatePayload,
} from './types';

interface GapMatrixProps {
	cells: MatrixCell[];
	columns: string[];
	projectId: number | string;
	projectName?: string;
	rows: string[];
}

/**
 * The content coverage matrix rendered on the CMP project page. It is mounted
 * outside the chat and owns the coverage visualization, the standalone
 * Get AI-Insights button, and the live cell updates fired after assets are
 * attached (94219) or content is generated (94221).
 */
export default function GapMatrix({
	cells,
	columns,
	projectId,
	projectName,
	rows,
}: GapMatrixProps) {
	const [counts, setCounts] = useState<Record<string, number>>(() =>
		cells.reduce<Record<string, number>>((current, cell) => {
			current[cell.cellId] = cell.count;

			return current;
		}, {})
	);
	const [selected, setSelected] = useState<string[]>([]);

	useEffect(() => {
		const handleUpdate = (payload: MatrixUpdatePayload) => {
			setCounts((previous) => ({
				...previous,
				[payload.cellId]:
					(previous[payload.cellId] ?? 0) + payload.delta,
			}));
		};

		Liferay.on(GAP_MATRIX_UPDATE_EVENT, handleUpdate);

		return () => {
			Liferay.detach(GAP_MATRIX_UPDATE_EVENT, handleUpdate);
		};
	}, []);

	function cellFor(row: string, column: string): MatrixCell | undefined {
		return cells.find((cell) => cell.row === row && cell.column === column);
	}

	function isCovered(cell: MatrixCell): boolean {
		return (counts[cell.cellId] ?? 0) >= cell.threshold;
	}

	function toggleCell(cellId: string) {
		setSelected((previous) =>
			previous.includes(cellId)
				? previous.filter((current) => current !== cellId)
				: [...previous, cellId]
		);
	}

	const coveredCount = cells.filter(isCovered).length;
	const coveragePercent = cells.length
		? Math.round((coveredCount / cells.length) * 100)
		: 0;

	return (
		<div className="gap-matrix">
			<div className="align-items-center d-flex justify-content-between mb-2">
				<div className="align-items-center d-flex">
					<ClayIcon
						className="mr-2"
						spritemap={Liferay.Icons.spritemap}
						symbol="grid"
					/>

					<span className="font-weight-semi-bold text-uppercase">
						{Liferay.Language.get('content-coverage-matrix')}
					</span>

					<ClayLabel className="ml-3" displayType="warning">
						{Liferay.Util.sub(
							Liferay.Language.get('x-covered'),
							`${coveragePercent}%`
						)}
					</ClayLabel>

					<ClayLabel displayType="danger">
						{coveredCount === 0
							? Liferay.Language.get('no-assets-found')
							: Liferay.Util.sub(
									Liferay.Language.get('x-critical-gaps'),
									`${cells.length - coveredCount}`
								)}
					</ClayLabel>
				</div>

				<ClayButton
					displayType="unstyled"
					onClick={() =>
						getGapInsights({
							projectId,
							projectName,
							selectedCells: selected,
						})
					}
				>
					<ClayIcon
						className="mr-2"
						spritemap={Liferay.Icons.spritemap}
						symbol="stars"
					/>

					{Liferay.Language.get('get-ai-insights')}
				</ClayButton>
			</div>

			<h4 className="mb-1">
				{Liferay.Language.get(
					'amount-of-assets-per-persona-x-funnel-stage'
				)}
			</h4>

			<p className="text-secondary">
				{Liferay.Language.get(
					'this-report-provides-a-breakdown-of-all-project-assets-by-persona-and-funnel-stage'
				)}
			</p>

			<table className="gap-matrix__table table">
				<thead>
					<tr>
						<th />

						{columns.map((column) => (
							<th key={column}>{column}</th>
						))}
					</tr>
				</thead>

				<tbody>
					{rows.map((row) => (
						<tr key={row}>
							<th>{row}</th>

							{columns.map((column) => {
								const cell = cellFor(row, column);

								if (!cell) {
									return <td key={column} />;
								}

								const covered = isCovered(cell);

								return (
									<td
										className={classNames(
											'gap-matrix__cell',
											{
												'gap-matrix__cell--covered':
													covered,
												'gap-matrix__cell--gap':
													!covered,
												'gap-matrix__cell--selected':
													selected.includes(
														cell.cellId
													),
											}
										)}
										key={column}
										onClick={() => toggleCell(cell.cellId)}
									>
										{counts[cell.cellId] ?? 0}
									</td>
								);
							})}
						</tr>
					))}
				</tbody>
			</table>
		</div>
	);
}
