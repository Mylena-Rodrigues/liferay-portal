/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
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
	rows: string[];
}

/**
 * The content population matrix rendered on the CMP project page. It is
 * mounted outside the chat and owns the coverage visualization, the standalone
 * Get GAP Insights button, and the live cell updates fired after assets are
 * attached (94219) or content is generated (94221).
 */
export default function GapMatrix({
	cells,
	columns,
	projectId,
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

	function toggleCell(cellId: string) {
		setSelected((previous) =>
			previous.includes(cellId)
				? previous.filter((current) => current !== cellId)
				: [...previous, cellId]
		);
	}

	return (
		<div className="gap-matrix">
			<div className="align-items-center d-flex justify-content-between mb-3">
				<h4 className="m-0">
					{Liferay.Language.get('amount-of-assets-per-persona')}
				</h4>

				<ClayButton
					displayType="primary"
					onClick={() =>
						getGapInsights({
							projectId,
							selectedCells: selected,
						})
					}
				>
					<ClayIcon
						className="mr-2"
						spritemap={Liferay.Icons.spritemap}
						symbol="stars"
					/>

					{Liferay.Language.get('get-gap-insights')}
				</ClayButton>
			</div>

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

								const count = counts[cell.cellId] ?? 0;
								const isGap = count < cell.threshold;

								return (
									<td
										className={classNames(
											'gap-matrix__cell',
											{
												'gap-matrix__cell--gap': isGap,
												'gap-matrix__cell--selected':
													selected.includes(
														cell.cellId
													),
											}
										)}
										key={column}
										onClick={() => toggleCell(cell.cellId)}
									>
										{count}
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
