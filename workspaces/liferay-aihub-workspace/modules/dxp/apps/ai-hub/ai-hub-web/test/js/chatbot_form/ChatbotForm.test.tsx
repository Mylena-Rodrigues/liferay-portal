/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	cleanup,
	fireEvent,
	render,
	screen,
	waitFor,
	within,
} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom';

import ChatbotForm from '../../../src/main/resources/META-INF/resources/js/chatbot_form/ChatbotForm';

const mockGetAgentDefinitions = jest.fn().mockResolvedValue({items: []});

jest.mock(
	'../../../src/main/resources/META-INF/resources/js/agent_definition_form/services/AgentDefinitionService',
	() => ({
		getAgentDefinitions: (...args: any[]) =>
			mockGetAgentDefinitions(...args),
	})
);

const mockGetChatbotDefinition = jest.fn();
const mockPostChatbotDefinition = jest.fn();
const mockPutChatbotDefinition = jest.fn();
const mockOpenToast = jest.fn();

jest.mock(
	'../../../src/main/resources/META-INF/resources/js/chatbot_form/services/ChatbotService',
	() => ({
		disassociateChatbotFromAgentDefinition: jest.fn().mockResolvedValue({}),
		getChatbotDefinition: (...args: any[]) =>
			mockGetChatbotDefinition(...args),
		postChatbotDefinition: (...args: any[]) =>
			mockPostChatbotDefinition(...args),
		putChatbotAgentDefinitionRelationship: jest.fn().mockResolvedValue({}),
		putChatbotDefinition: (...args: any[]) =>
			mockPutChatbotDefinition(...args),
	})
);

jest.mock('@liferay/object-js-components-web', () => ({
	openToast: (...args: any[]) => mockOpenToast(...args),
}));

jest.mock('frontend-js-components-web', () => ({
	InputLocalized: ({
		error,
		label,
		name,
		onChange,
		translations,
	}: {
		error?: string;
		label: string;
		name: string;
		onChange: (value: Record<string, string>) => void;
		translations?: Record<string, string>;
	}) => (
		<div>
			<label htmlFor={name}>{label}</label>

			<input
				data-testid={name}
				id={name}
				onChange={(event) =>
					onChange({
						...(translations ?? {}),
						en_US: event.target.value,
					})
				}
				value={translations?.en_US ?? ''}
			/>

			{error && <span data-testid={`${name}-error`}>{error}</span>}
		</div>
	),
}));

jest.mock('frontend-js-web', () => ({
	sub: (template: string, ...args: string[]) =>
		template.replace(/\{\d+\}/g, () => args.shift() ?? ''),
}));

jest.mock('@clayui/modal', () => {
	const ClayModal = ({children}: {children: React.ReactNode}) => (
		<div role="dialog">{children}</div>
	);

	ClayModal.Header = ({children}: {children: React.ReactNode}) => (
		<div>{children}</div>
	);
	ClayModal.Body = ({children}: {children: React.ReactNode}) => (
		<div>{children}</div>
	);
	ClayModal.Footer = ({last}: {last: React.ReactNode}) => <div>{last}</div>;

	return {
		__esModule: true,
		default: ClayModal,
		useModal: ({onClose}: {onClose: () => void}) => ({
			observer: {},
			onClose,
		}),
	};
});

jest.mock('@liferay/frontend-data-set-web', () => ({
	FrontendDataSet: ({
		creationMenu,
		items,
		itemsActions,
	}: {
		creationMenu?: {
			primaryItems?: {label: string; onClick: () => void}[];
		};
		items: {id: string; question: string}[];
		itemsActions?: {
			label: string;
			onClick: (event: {itemData: {id: string}}) => void;
		}[];
	}) => (
		<div>
			{(creationMenu?.primaryItems ?? []).map((primaryItem) => (
				<button
					key={primaryItem.label}
					onClick={primaryItem.onClick}
					type="button"
				>
					{primaryItem.label}
				</button>
			))}

			<ul>
				{(items ?? []).map((item) => (
					<li key={item.id}>
						<span>{item.question}</span>

						{(itemsActions ?? []).map((action) => (
							<button
								key={action.label}
								onClick={() => action.onClick({itemData: item})}
								type="button"
							>
								{`${action.label}-${item.question}`}
							</button>
						))}
					</li>
				))}
			</ul>
		</div>
	),
}));

(global as any).Liferay = {
	Icons: {spritemap: 'icons.svg'},
	Language: {
		get: (key: string) => key,
	},
	ThemeDisplay: {
		getDefaultLanguageId: () => 'en_US',
		getLanguageId: () => 'en_US',
	},
};

(global as any).ResizeObserver = class {
	disconnect() {}
	observe() {}
	unobserve() {}
};

const defaultProps = {
	accountEntryExternalReferenceCode: 'ACCOUNT',
	avatarAcceptedFileExtensions: 'jpeg, jpg, png',
	avatarMaximumFileSize: 1024,
	avatarMaximumFileSizeLabel: '1 KB',
	avatarUploadTip: 'Upload a jpeg, jpg, png no larger than 1 KB',
	backURL: '/back',
	externalReferenceCode: '',
	portalURL: 'http://localhost:8080',
	readOnly: false,
};

function getHiddenFileInput(): HTMLInputElement {
	return document.getElementById('avatar') as HTMLInputElement;
}

function makeFile(name: string, sizeInBytes: number, type = 'image/png'): File {
	const blob = new Blob([new Uint8Array(sizeInBytes)], {type});

	return new File([blob], name, {type});
}

describe('ChatbotForm assigned agents search', () => {
	beforeEach(() => {
		mockOpenToast.mockClear();
		mockGetChatbotDefinition.mockReset();
		mockGetAgentDefinitions.mockReset();
		mockGetAgentDefinitions.mockResolvedValue({items: []});
	});

	afterEach(() => {
		cleanup();
	});

	it('requests the agent definitions with a search parameter', async () => {
		render(<ChatbotForm {...defaultProps} />);

		fireEvent.change(screen.getByRole('combobox'), {
			target: {value: 'support'},
		});

		await waitFor(() =>
			expect(mockGetAgentDefinitions).toHaveBeenLastCalledWith({
				search: 'support',
			})
		);
	});

	it('requests the agent definitions without a search parameter', async () => {
		render(<ChatbotForm {...defaultProps} />);

		await waitFor(() => expect(mockGetAgentDefinitions).toHaveBeenCalled());

		expect(mockGetAgentDefinitions).toHaveBeenCalledWith({});
	});

});

describe('ChatbotForm company logo upload', () => {
	beforeEach(() => {
		mockOpenToast.mockClear();
		mockGetChatbotDefinition.mockReset();
		mockPostChatbotDefinition.mockReset();
		mockPutChatbotDefinition.mockReset();
	});

	afterEach(() => {
		cleanup();
	});

	it('rejects files larger than avatarMaximumFileSize when limit is greater than zero', async () => {
		render(<ChatbotForm {...defaultProps} />);

		const file = makeFile('big-logo.png', 4096);

		fireEvent.change(getHiddenFileInput(), {target: {files: [file]}});

		await waitFor(() => {
			expect(mockOpenToast).toHaveBeenCalledWith(
				expect.objectContaining({type: 'danger'})
			);
		});

		expect(screen.queryByText('big-logo.png')).not.toBeInTheDocument();
	});

	it('accepts files of any size when avatarMaximumFileSize is zero (unlimited)', async () => {
		render(<ChatbotForm {...defaultProps} avatarMaximumFileSize={0} />);

		const file = makeFile('huge-logo.png', 5_000_000);

		fireEvent.change(getHiddenFileInput(), {target: {files: [file]}});

		await screen.findByText('huge-logo.png');

		expect(mockOpenToast).not.toHaveBeenCalledWith(
			expect.objectContaining({type: 'danger'})
		);
	});

	it('hides the Clear button when no company logo is set', () => {
		render(<ChatbotForm {...defaultProps} />);

		expect(
			screen.queryByRole('button', {name: 'clear'})
		).not.toBeInTheDocument();
	});

	it('renders the Clear button after a valid file is selected', async () => {
		render(<ChatbotForm {...defaultProps} />);

		const file = makeFile('logo.png', 512);

		fireEvent.change(getHiddenFileInput(), {target: {files: [file]}});

		expect(
			await screen.findByRole('button', {name: 'clear'})
		).toBeInTheDocument();
	});

	it('disables the select button while reading the file', async () => {
		render(<ChatbotForm {...defaultProps} />);

		const file = makeFile('logo.png', 512);

		fireEvent.change(getHiddenFileInput(), {target: {files: [file]}});

		const selectButton = screen.getByLabelText('select-x');

		expect(selectButton).toBeDisabled();

		await waitFor(() => expect(selectButton).not.toBeDisabled());
	});
});

describe('ChatbotForm disclaimer message', () => {
	beforeEach(() => {
		mockOpenToast.mockClear();
		mockGetChatbotDefinition.mockReset();
		mockPostChatbotDefinition.mockReset();
		mockPutChatbotDefinition.mockReset();
	});

	afterEach(() => {
		cleanup();
	});

	it('submits with an empty disclaimer when the admin never fills it', async () => {
		mockPostChatbotDefinition.mockResolvedValue({
			externalReferenceCode: 'CHATBOT-ERC',
		});

		render(<ChatbotForm {...defaultProps} />);

		await screen.findByTestId('disclaimerMessage_i18n');

		fireEvent.click(screen.getByRole('button', {name: 'save'}));

		await waitFor(() =>
			expect(mockPostChatbotDefinition).toHaveBeenCalled()
		);

		expect(
			mockPostChatbotDefinition.mock.calls[0][0].disclaimerMessage_i18n
		).toEqual({});
	});

	it('sends the typed disclaimer through to the API payload', async () => {
		mockPostChatbotDefinition.mockResolvedValue({
			externalReferenceCode: 'CHATBOT-ERC',
		});

		render(<ChatbotForm {...defaultProps} />);

		fireEvent.change(await screen.findByTestId('disclaimerMessage_i18n'), {
			target: {value: 'Custom disclaimer'},
		});

		fireEvent.click(screen.getByRole('button', {name: 'save'}));

		await waitFor(() =>
			expect(mockPostChatbotDefinition).toHaveBeenCalled()
		);

		expect(
			mockPostChatbotDefinition.mock.calls[0][0].disclaimerMessage_i18n
		).toEqual({en_US: 'Custom disclaimer'});
	});
});

describe('ChatbotForm suggested questions', () => {
	beforeEach(() => {
		mockOpenToast.mockClear();
		mockGetChatbotDefinition.mockReset();
		mockPostChatbotDefinition.mockReset();
		mockPutChatbotDefinition.mockReset();
	});

	afterEach(() => {
		cleanup();
	});

	it('loads the stored questions and round-trips them on save', async () => {
		mockGetChatbotDefinition.mockResolvedValue({
			active: true,
			externalReferenceCode: 'CHATBOT-ERC',
			suggestedQuestions_i18n: {en_US: 'Q1\nQ2', pt_BR: 'P1'},
			title_i18n: {en_US: 'Bot'},
		});
		mockPutChatbotDefinition.mockResolvedValue({});

		render(
			<ChatbotForm
				{...defaultProps}
				externalReferenceCode="CHATBOT-ERC"
			/>
		);

		await screen.findByText('Q1');

		expect(screen.getByText('Q2')).toBeInTheDocument();

		fireEvent.click(screen.getByRole('button', {name: 'save'}));

		await waitFor(() =>
			expect(mockPutChatbotDefinition).toHaveBeenCalled()
		);

		expect(
			mockPutChatbotDefinition.mock.calls[0][1].suggestedQuestions_i18n
		).toEqual({en_US: 'Q1\nQ2', pt_BR: 'P1'});
	});

	it('adds a question through the modal and sends it on save', async () => {
		mockPostChatbotDefinition.mockResolvedValue({
			externalReferenceCode: 'CHATBOT-ERC',
		});

		render(<ChatbotForm {...defaultProps} />);

		fireEvent.click(
			await screen.findByRole('button', {name: 'add-question'})
		);

		fireEvent.change(screen.getByTestId('question_i18n'), {
			target: {value: 'How do I register to vote?'},
		});

		fireEvent.click(
			within(screen.getByRole('dialog')).getByRole('button', {
				name: 'save',
			})
		);

		await screen.findByText('How do I register to vote?');

		fireEvent.click(screen.getByRole('button', {name: 'save'}));

		await waitFor(() =>
			expect(mockPostChatbotDefinition).toHaveBeenCalled()
		);

		expect(
			mockPostChatbotDefinition.mock.calls[0][0].suggestedQuestions_i18n
		).toEqual({en_US: 'How do I register to vote?'});
	});

	it('edits a question through the modal and sends the new text on save', async () => {
		mockGetChatbotDefinition.mockResolvedValue({
			active: true,
			externalReferenceCode: 'CHATBOT-ERC',
			suggestedQuestions_i18n: {en_US: 'Q1\nQ2'},
			title_i18n: {en_US: 'Bot'},
		});
		mockPutChatbotDefinition.mockResolvedValue({});

		render(
			<ChatbotForm
				{...defaultProps}
				externalReferenceCode="CHATBOT-ERC"
			/>
		);

		fireEvent.click(await screen.findByRole('button', {name: 'edit-Q1'}));

		fireEvent.change(screen.getByTestId('question_i18n'), {
			target: {value: 'Q1 edited'},
		});

		fireEvent.click(
			within(screen.getByRole('dialog')).getByRole('button', {
				name: 'save',
			})
		);

		fireEvent.click(screen.getByRole('button', {name: 'save'}));

		await waitFor(() =>
			expect(mockPutChatbotDefinition).toHaveBeenCalled()
		);

		expect(
			mockPutChatbotDefinition.mock.calls[0][1].suggestedQuestions_i18n
		).toEqual({en_US: 'Q1 edited\nQ2'});
	});

	it('deletes a question and leaves it out of the payload', async () => {
		mockGetChatbotDefinition.mockResolvedValue({
			active: true,
			externalReferenceCode: 'CHATBOT-ERC',
			suggestedQuestions_i18n: {en_US: 'Q1\nQ2'},
			title_i18n: {en_US: 'Bot'},
		});
		mockPutChatbotDefinition.mockResolvedValue({});

		render(
			<ChatbotForm
				{...defaultProps}
				externalReferenceCode="CHATBOT-ERC"
			/>
		);

		fireEvent.click(
			await screen.findByRole('button', {name: 'delete-Q1'})
		);

		fireEvent.click(screen.getByRole('button', {name: 'save'}));

		await waitFor(() =>
			expect(mockPutChatbotDefinition).toHaveBeenCalled()
		);

		expect(
			mockPutChatbotDefinition.mock.calls[0][1].suggestedQuestions_i18n
		).toEqual({en_US: 'Q2'});
	});

	it('reorders the questions with move down', async () => {
		mockGetChatbotDefinition.mockResolvedValue({
			active: true,
			externalReferenceCode: 'CHATBOT-ERC',
			suggestedQuestions_i18n: {en_US: 'Q1\nQ2'},
			title_i18n: {en_US: 'Bot'},
		});
		mockPutChatbotDefinition.mockResolvedValue({});

		render(
			<ChatbotForm
				{...defaultProps}
				externalReferenceCode="CHATBOT-ERC"
			/>
		);

		fireEvent.click(
			await screen.findByRole('button', {name: 'move-down-Q1'})
		);

		fireEvent.click(screen.getByRole('button', {name: 'save'}));

		await waitFor(() =>
			expect(mockPutChatbotDefinition).toHaveBeenCalled()
		);

		expect(
			mockPutChatbotDefinition.mock.calls[0][1].suggestedQuestions_i18n
		).toEqual({en_US: 'Q2\nQ1'});
	});
});
>>>>>>> facf450bcc (LPD-96744 Manage suggested questions in the chatbot form)
