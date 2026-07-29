/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';
import ReactMarkdown, {Components} from 'react-markdown';
import remarkGfm from 'remark-gfm';

const MARKDOWN_COMPONENTS: Components = {
	a: ({node: _node, ...props}) => (
		<a {...props} rel="noopener noreferrer" target="_blank" />
	),
};

const REMARK_PLUGINS = [remarkGfm];

interface MarkdownProps {
	text: string;
}

export default function Markdown({text}: MarkdownProps) {
	return (
		<ReactMarkdown
			components={MARKDOWN_COMPONENTS}
			remarkPlugins={REMARK_PLUGINS}
		>
			{text}
		</ReactMarkdown>
	);
}
