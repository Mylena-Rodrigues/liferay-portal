/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import classNames from 'classnames';
import React from 'react';

import {ChipOption} from '../types';

interface ChipPickerProps {
	multiSelect?: boolean;
	onChange: (value: string[]) => void;
	options: ChipOption[];
	value: string[];
}

/**
 * Selectable chip group used for style presets (single-select) and channel
 * format presets (multi-select). Options may declare a `group` so callers can
 * render grouped pickers on top of the same primitive.
 */
export default function ChipPicker({
	multiSelect = false,
	onChange,
	options,
	value,
}: ChipPickerProps) {
	function toggle(optionValue: string) {
		if (!multiSelect) {
			onChange([optionValue]);

			return;
		}

		if (value.includes(optionValue)) {
			onChange(value.filter((current) => current !== optionValue));
		}
		else {
			onChange([...value, optionValue]);
		}
	}

	return (
		<div className="ai-assistant-chat__chip-picker d-flex flex-wrap">
			{options.map((option) => {
				const selected = value.includes(option.value);

				return (
					<ClayButton
						aria-pressed={selected}
						className={classNames('mb-1 mr-1', {
							active: selected,
						})}
						displayType={selected ? 'primary' : 'secondary'}
						key={option.value}
						onClick={() => toggle(option.value)}
						size="sm"
					>
						{option.label}
					</ClayButton>
				);
			})}
		</div>
	);
}
