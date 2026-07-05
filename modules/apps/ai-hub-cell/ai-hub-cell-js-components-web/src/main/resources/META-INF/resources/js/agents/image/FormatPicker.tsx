/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import ChipPicker from '../../shared/components/ChipPicker';
import {ChipOption} from '../../shared/types';

interface FormatPickerProps {
	onChange: (value: string[]) => void;
	options: ChipOption[];
	value: string[];
}

function groupOptions(options: ChipOption[]): Record<string, ChipOption[]> {
	return options.reduce<Record<string, ChipOption[]>>((groups, option) => {
		const group = option.group ?? Liferay.Language.get('other');

		groups[group] = groups[group] ?? [];
		groups[group].push(option);

		return groups;
	}, {});
}

/**
 * Channel format picker used by both the single (93959) and bulk (93961) Adapt
 * flows. Each surface is a bordered card grouping its format chips; the presets
 * are supplied by the caller (loaded from the Liferay environment) and the
 * selection behavior is the shared ChipPicker.
 */
export default function FormatPicker({
	onChange,
	options,
	value,
}: FormatPickerProps) {
	const groups = groupOptions(options);

	return (
		<div className="ai-assistant-chat__format-picker">
			{Object.keys(groups).map((group) => (
				<div
					className="ai-assistant-chat__format-group border mb-2 p-2 rounded"
					key={group}
				>
					<div className="font-weight-semi-bold mb-1">{group}</div>

					<ChipPicker
						multiSelect
						onChange={onChange}
						options={groups[group]}
						value={value}
					/>
				</div>
			))}
		</div>
	);
}
