/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import ChipPicker from '../../shared/components/ChipPicker';
import {ChipOption} from '../../shared/types';
import {CHANNEL_FORMAT_OPTIONS} from './types';

interface FormatPickerProps {
	onChange: (value: string[]) => void;
	options?: ChipOption[];
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
 * flows. Presets are grouped by surface on top of the shared ChipPicker
 * primitive, so the grouping lives here and the selection behavior is shared.
 */
export default function FormatPicker({
	onChange,
	options = CHANNEL_FORMAT_OPTIONS,
	value,
}: FormatPickerProps) {
	const groups = groupOptions(options);

	return (
		<div className="ai-assistant-chat__format-picker">
			{Object.keys(groups).map((group) => (
				<div className="mb-2" key={group}>
					<div className="font-weight-semi-bold small">{group}</div>

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
