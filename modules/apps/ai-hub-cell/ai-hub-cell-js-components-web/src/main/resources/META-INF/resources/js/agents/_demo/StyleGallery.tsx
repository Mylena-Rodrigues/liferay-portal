/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {ClayCheckbox} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import {openToast} from 'frontend-js-components-web';
import React, {useState} from 'react';

import AIAssistantMessageBalloon from '../../AIAssistantChat/components/AIAssistantMessageBalloon';
import UserMessageBalloon from '../../AIAssistantChat/components/UserMessageBalloon';
import AgentResultCard from '../../shared/components/AgentResultCard';
import BatchSummary from '../../shared/components/BatchSummary';
import ChipPicker from '../../shared/components/ChipPicker';
import ContextChip from '../../shared/components/ContextChip';
import DraftLinkList from '../../shared/components/DraftLinkList';
import OptionsList from '../../shared/components/OptionsList';
import {AgentResultItem} from '../../shared/types';
import {generateContent, generateInEditor} from '../content/triggers';
import GapMatrix from '../gap/GapMatrix';
import {
	findMatchingAssets,
	generateForGaps,
	getGapInsights,
} from '../gap/triggers';
import FormatPicker from '../image/FormatPicker';
import {adaptForChannels, generateImageFromFolder} from '../image/triggers';
import {DEFAULT_CHANNEL_FORMAT_OPTIONS} from '../image/types';

const IMAGE_SRC =
	"data:image/svg+xml;utf8,<svg xmlns='http://www.w3.org/2000/svg' width='160' height='120'><rect width='160' height='120' fill='%23c9d3e0'/><circle cx='80' cy='60' r='34' fill='%23f08a3c'/></svg>";

const DRAFTS: AgentResultItem[] = [
	{href: '#draft-1', id: 'd1', title: 'Marketing trends'},
	{href: '#draft-2', id: 'd2', title: 'Best practices for onboarding'},
];

const BATCH_ITEMS: AgentResultItem[] = [
	{id: 'b1', status: 'success', title: 'madrid_instagram-post.jpg'},
	{id: 'b2', status: 'success', title: 'madrid_linkedin-banner.jpg'},
	{
		id: 'b3',
		reason: 'not_image',
		status: 'skipped',
		title: 'report.xlsx',
	},
	{
		id: 'b4',
		reason: 'incompatible_source',
		status: 'failed',
		title: 'logo.png',
	},
];

const MATRIX_ROWS = ['Operations Leadership', 'Procurement', 'Finance'];

const MATRIX_COLUMNS = ['Awareness', 'Consideration', 'Decision'];

const MATRIX_CELLS = MATRIX_ROWS.flatMap((row, rowIndex) =>
	MATRIX_COLUMNS.map((column, columnIndex) => ({
		cellId: `${rowIndex}-${columnIndex}`,
		column,
		count: (rowIndex + columnIndex) % 2,
		row,
		threshold: 1,
	}))
);

function Section({children, title}: {children: React.ReactNode; title: string}) {
	return (
		<div className="mb-4">
			<div className="font-weight-semi-bold mb-2 text-uppercase small text-secondary">
				{title}
			</div>

			{children}
		</div>
	);
}

/**
 * Dev-only gallery that renders every styled agent component with mock data so
 * the styles and interactions can be reviewed in the running chat without the
 * backend. Not shipped on the approach branches.
 */
export default function StyleGallery() {
	const [styleValue, setStyleValue] = useState<string[]>(['photorealistic']);
	const [formats, setFormats] = useState<string[]>(['ig-post']);
	const [selectedImages, setSelectedImages] = useState<Record<string, boolean>>(
		{img1: true, img2: true}
	);

	const launchers = [
		{
			label: 'Generate Image',
			onClick: () => generateImageFromFolder('demo-folder'),
		},
		{
			label: 'Adapt for Channels (single)',
			onClick: () =>
				adaptForChannels({fileEntryId: '1', sourceName: 'Madrid.jpg'}),
		},
		{
			label: 'Adapt for Channels (bulk)',
			onClick: () =>
				adaptForChannels({
					fileEntryIds: ['1', '2', '3'],
					sourceName: '3 items',
				}),
		},
		{
			label: 'Generate Content',
			onClick: () => generateContent({requiresContentType: true}),
		},
		{
			label: 'Generate multiple content',
			onClick: () =>
				generateContent({
					brief: 'trips around Japan',
					count: 3,
					structureId: 2,
				}),
		},
		{
			label: 'Generate Title (editor)',
			onClick: () => generateInEditor('title', {assetId: '1'}),
		},
		{
			label: 'Get AI-Insights',
			onClick: () =>
				getGapInsights({
					projectId: 'demo',
					projectName: 'EuroRoad Construction',
				}),
		},
		{
			label: 'Find Matching Assets',
			onClick: () => findMatchingAssets({projectId: 'demo'}),
		},
		{
			label: 'Generate for Gaps',
			onClick: () => generateForGaps({projectId: 'demo'}),
		},
	];

	return (
		<div className="ai-assistant-chat__ai-assistant-message-balloon mb-2 p-2 rounded">
			<Section title="Launch real flows (mocked backend)">
				<div className="d-flex flex-wrap">
					{launchers.map((launcher) => (
						<ClayButton
							className="mb-1 mr-1"
							displayType="primary"
							key={launcher.label}
							onClick={launcher.onClick}
							size="sm"
						>
							{launcher.label}
						</ClayButton>
					))}
				</div>
			</Section>

			<Section title="Message balloons">
				<AIAssistantMessageBalloon
					error={false}
					message="This is an assistant message balloon."
				/>

				<UserMessageBalloon message="This is a user message." />

				<AIAssistantMessageBalloon
					error
					message="This is an error balloon."
				/>
			</Section>

			<Section title="Context chip">
				<ContextChip label="Context: Madrid.jpg" onRemove={() => {}} />
			</Section>

			<Section title="Chip picker (single-select)">
				<ChipPicker
					onChange={setStyleValue}
					options={[
						{label: 'Photorealistic', value: 'photorealistic'},
						{label: 'Illustration', value: 'illustration'},
						{label: 'Digital Art', value: 'digital-art'},
					]}
					value={styleValue}
				/>
			</Section>

			<Section title="Format picker (grouped multi-select)">
				<FormatPicker
					onChange={setFormats}
					options={DEFAULT_CHANNEL_FORMAT_OPTIONS}
					value={formats}
				/>
			</Section>

			<Section title="Generated image grid">
				<div className="d-flex flex-wrap">
					{['img1', 'img2'].map((id) => (
						<div
							className="ai-assistant-chat__generated-image-wrapper mb-2 mr-2"
							key={id}
						>
							<ClayCheckbox
								checked={Boolean(selectedImages[id])}
								onChange={() =>
									setSelectedImages((previous) => ({
										...previous,
										[id]: !previous[id],
									}))
								}
							/>

							<img
								alt="Generated preview"
								className="ai-assistant-chat__generated-image rounded"
								src={IMAGE_SRC}
							/>
						</div>
					))}
				</div>
			</Section>

			<Section title="Result cards (content draft + matching asset)">
				<AgentResultCard
					href="#draft"
					labels={[{displayType: 'info', text: 'Draft'}]}
					title="Marketing trends"
				/>

				<AgentResultCard
					href="#asset"
					labels={[
						{displayType: 'success', text: 'Approved'},
						{
							displayType: 'secondary',
							text: 'Procurement x Awareness',
						},
					]}
					title="Vendor evaluation checklist for procurement"
				/>
			</Section>

			<Section title="Draft link list">
				<DraftLinkList items={DRAFTS} />
			</Section>

			<Section title="Options list">
				<OptionsList
					options={[
						{
							label: 'Find Matching Assets in CMS',
							onClick: () =>
								openToast({message: 'Find Matching Assets'}),
						},
						{
							label: 'Create tasks for gaps',
							onClick: () => openToast({message: 'Create tasks'}),
						},
						{
							label: 'Generate Content for Gaps',
							onClick: () =>
								openToast({message: 'Generate Content'}),
						},
					]}
					title="What would you like to do next?"
				/>
			</Section>

			<Section title="Batch summary">
				<BatchSummary
					items={BATCH_ITEMS}
					onResume={() => openToast({message: 'Resume'})}
				/>
			</Section>

			<Section title="Add-all confirmation">
				<p>Would you like me to add all suggested assets?</p>

				<div className="d-flex">
					<ClayButton
						className="mr-2"
						displayType="primary"
						onClick={() => openToast({message: 'Yes'})}
						size="sm"
					>
						Yes
					</ClayButton>

					<ClayButton
						displayType="secondary"
						onClick={() => openToast({message: 'No'})}
						size="sm"
					>
						No
					</ClayButton>
				</div>
			</Section>

			<Section title="AI-generated badge">
				<span className="ai-assistant-chat__ai-generated-badge">
					<ClayIcon
						className="mr-1"
						spritemap={Liferay.Icons.spritemap}
						symbol="stars"
					/>
					AI-Generated
				</span>
			</Section>

			<Section title="Content coverage matrix">
				<GapMatrix
					cells={MATRIX_CELLS}
					columns={MATRIX_COLUMNS}
					projectId="demo"
					projectName="EuroRoad Construction"
					rows={MATRIX_ROWS}
				/>
			</Section>
		</div>
	);
}
