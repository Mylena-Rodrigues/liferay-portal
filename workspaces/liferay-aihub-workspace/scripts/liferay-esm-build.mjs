/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

/**
 * Standalone replacement for `node-scripts build` (which requires the full
 * liferay-portal yarn workspace and cannot run in this Liferay Workspace).
 *
 * Replicates the output contract of the portal CI build, as reverse
 * engineered from the CI built com.liferay.ai.hub.cell.js.components.web.jar
 * shipped inside the liferay/release-candidates ai-hub docker image:
 *
 *   META-INF/resources/__liferay__/index.js   ESM bundle; portal provided
 *                                             packages are rewritten to
 *                                             relative web context URLs
 *   META-INF/resources/index.js               AMD bridge (Liferay.Loader)
 *   META-INF/resources/package.json           {main, name, version}
 *   META-INF/resources/manifest.json          NPMRegistry manifest
 *   META-INF/resources/language.json          Liferay.Language.get keys
 *
 * SCSS imported from .tsx is emitted as a runtime <link> injection pointing
 * at the CSS compiled by the Gradle buildCSS task (Liferay CSS Builder).
 *
 * Run from the module directory (yarn run build):
 *
 *   import {runBuild} from '<relative>/scripts/liferay-esm-build.mjs';
 *
 *   await runBuild({webContext: '<Web-ContextPath without slash>'});
 */

import {build} from 'esbuild';
import crypto from 'node:crypto';
import fs from 'node:fs';
import path from 'node:path';

// Packages served as single files under
// /o/<provider>/__liferay__/exports/<specifier with "/" replaced by "$">.js

const EXPORTS_PROVIDERS = [
	{
		provider: 'frontend-js-react-web',
		specifiers: /^(react|react-dom|react-dom\/client|classnames)$/,
	},
	{
		provider: 'frontend-js-clay-web',
		specifiers: /^@clayui\/[^/]+$/,
	},
	{
		provider: 'frontend-editor-ckeditor-web',
		specifiers: /^(@ckeditor\/.+|eventsource)$/,
	},
];

// Packages served as the providing module's own ESM index at
// /o/<provider>/__liferay__/index.js

const INDEX_PROVIDERS = {
	'@liferay/frontend-data-set-web': 'frontend-data-set-web',
	'@liferay/frontend-js-react-web': 'frontend-js-react-web',
	'@liferay/object-js-components-web': 'object-js-components-web',
	'dynamic-data-mapping-form-field-type':
		'dynamic-data-mapping-form-field-type',
	'frontend-js-components-web': 'frontend-js-components-web',
	'frontend-js-web': 'frontend-js-web',
};

function resolveExternal(specifier) {

	// The bare "ckeditor5" package is not exported by the portal. Its
	// symbols used in this codebase (ModelText, ModelTextProxy, ModelWriter)
	// are re-exports from @ckeditor/ckeditor5-engine, which is.

	if (specifier === 'ckeditor5') {
		specifier = '@ckeditor/ckeditor5-engine/dist/index.js';
	}

	const indexProvider = INDEX_PROVIDERS[specifier];

	if (indexProvider) {
		return '../../' + indexProvider + '/__liferay__/index.js';
	}

	for (const {provider, specifiers} of EXPORTS_PROVIDERS) {
		if (specifiers.test(specifier)) {
			return (
				'../../' +
				provider +
				'/__liferay__/exports/' +
				specifier.replaceAll('/', '$') +
				'.js'
			);
		}
	}

	return null;
}

// Same algorithm as portal-kernel's HashedFilesUtil.computeHash: Base64 of
// the first 8 bytes of the MD5 digest, with "+" -> "$", "/" -> "@" and "="
// removed. A file named <name>.(<hash>).<ext> registers the module's servlet
// context in the portal's HashedFilesRegistry; without at least one hashed
// file the context is not registered and /o/js/language/<locale>/<context>/
// all.js returns 404 (LanguageFrontendResourceRequestHandler has no
// fallback outside the registry).

function computeLiferayHash(content) {
	const digest = crypto.createHash('md5').update(content).digest();

	return digest
		.subarray(0, 8)
		.toString('base64')
		.replaceAll('+', '$')
		.replaceAll('/', '@')
		.replaceAll('=', '');
}

export async function runBuild({virtualModules = {}, webContext}) {
	const moduleDir = process.cwd();

	const resourcesDir = path.join(
		moduleDir,
		'src/main/resources/META-INF/resources'
	);
	const outputDir = path.join(
		moduleDir,
		'build/node/packageRunBuild/resources/META-INF/resources'
	);

	const packageJSON = JSON.parse(
		fs.readFileSync(path.join(moduleDir, 'package.json'), 'utf8')
	);

	const liferayExternalsPlugin = {
		name: 'liferay-externals',
		setup(pluginBuild) {
			pluginBuild.onResolve({filter: /^[^.]/}, (args) => {
				if (virtualModules[args.path]) {
					return {namespace: 'liferay-virtual', path: args.path};
				}

				const external = resolveExternal(args.path);

				if (external) {

					// A bundled CommonJS dependency reaching an external
					// through require() cannot be left external: in ESM output
					// there is no import statement for esbuild to hang it on,
					// so it emits a dynamic __require() that throws in the
					// browser ("Dynamic require of ... is not supported").
					// recharts pulls in such dependencies; @clayui and the
					// portal packages, being ESM, never take this path.
					// Resolve those to an in-graph shim that statically
					// re-exports the external instead.

					if (args.kind === 'require-call') {
						return {
							namespace: 'liferay-external-shim',
							path: external,
						};
					}

					return {external: true, path: external};
				}

				// Unmapped bare specifiers (e.g. formik, uuid) fall through
				// to the default resolver and are bundled from node_modules.

				return null;
			});

			// The shim reaches the external by URL, which is relative, so the
			// bare specifier resolver above never sees it.

			pluginBuild.onResolve(
				{filter: /.*/, namespace: 'liferay-external-shim'},
				(args) => {
					return {external: true, path: args.path};
				}
			);

			pluginBuild.onLoad(
				{filter: /.*/, namespace: 'liferay-external-shim'},
				(args) => {
					const url = JSON.stringify(args.path);

					// `export *` carries the named exports, which is what a
					// require() caller reads off the returned object; the
					// default covers callers that use the module itself.

					return {
						contents: `
import * as liferayExternal from ${url};

export * from ${url};

export default liferayExternal.default ?? liferayExternal;
`,
						loader: 'js',
						resolveDir: moduleDir,
					};
				}
			);

			pluginBuild.onLoad(
				{filter: /.*/, namespace: 'liferay-virtual'},
				(args) => {
					return {
						contents: virtualModules[args.path],
						loader: 'js',
						resolveDir: moduleDir,
					};
				}
			);
		},
	};

	const liferayScssPlugin = {
		name: 'liferay-scss',
		setup(pluginBuild) {
			pluginBuild.onResolve({filter: /\.scss$/}, (args) => {
				return {
					namespace: 'liferay-scss',
					path: path.resolve(args.resolveDir, args.path),
				};
			});

			pluginBuild.onLoad(
				{filter: /.*/, namespace: 'liferay-scss'},
				(args) => {
					const cssRelativePath = path
						.relative(resourcesDir, args.path)
						.replace(/\.scss$/, '.css');

					const cssRtlRelativePath = cssRelativePath.replace(
						/\.css$/,
						'_rtl.css'
					);

					const contents = `
var cssHref = "../../${webContext}/${cssRelativePath}";

if (
	window.getComputedStyle(document.documentElement).direction === "rtl"
) {
	cssHref = "../../${webContext}/${cssRtlRelativePath}";
}

if (import.meta.url.includes("/js/-/")) {
	cssHref = "../../" + cssHref;
}

cssHref = import.meta.resolve(cssHref);

var linkElement = document.createElement("link");

linkElement.setAttribute("rel", "stylesheet");
linkElement.setAttribute("type", "text/css");
linkElement.setAttribute("href", cssHref);

if (Liferay.CSP) {
	linkElement.setAttribute("nonce", Liferay.CSP.nonce);
}

document.querySelector("head").appendChild(linkElement);
`;

					return {contents, loader: 'js'};
				}
			);
		},
	};

	// Collect Liferay.Language.get('...') keys for language.json

	const languageKeys = new Set();

	(function collectLanguageKeys(dir) {
		for (const entry of fs.readdirSync(dir, {withFileTypes: true})) {
			const entryPath = path.join(dir, entry.name);

			if (entry.isDirectory()) {
				collectLanguageKeys(entryPath);
			}
			else if (/\.(ts|tsx)$/.test(entry.name)) {
				const source = fs.readFileSync(entryPath, 'utf8');

				for (const match of source.matchAll(
					/Liferay\.Language\.get\(\s*'([^']+)'/g
				)) {
					languageKeys.add(match[1]);
				}
			}
		}
	})(path.join(resourcesDir, 'js'));

	fs.rmSync(path.join(moduleDir, 'build/node/packageRunBuild'), {
		force: true,
		recursive: true,
	});
	fs.mkdirSync(path.join(outputDir, '__liferay__'), {recursive: true});

	const banner = languageKeys.size
		? `import "@liferay/language/${webContext}/all.js";`
		: '';

	await build({
		banner: {js: banner},
		bundle: true,
		entryPoints: [path.join(resourcesDir, 'js/index.ts')],
		format: 'esm',
		jsx: 'transform',
		minify: false,
		outfile: path.join(outputDir, '__liferay__/index.js'),
		plugins: [liferayExternalsPlugin, liferayScssPlugin],
		target: 'es2020',
	});

	// Rename the bundle to its hashed form (index.(<hash>).js) so that the
	// HashedFilesRegistry registers this servlet context. Consumers keep
	// requesting the unhashed name; the portal's FrontendResourceFilter
	// resolves it through the registry.

	const esmBundlePath = path.join(outputDir, '__liferay__/index.js');

	const esmBundleHash = computeLiferayHash(fs.readFileSync(esmBundlePath));

	fs.renameSync(
		esmBundlePath,
		path.join(outputDir, `__liferay__/index.(${esmBundleHash}).js`)
	);

	// AMD bridge so Liferay.Loader (AMD) consumers can reach the ESM module

	fs.writeFileSync(
		path.join(outputDir, 'index.js'),
		`
import * as esModule from "../../../../${webContext}/__liferay__/index.js";

Liferay.Loader.define(
	"${packageJSON.name}@${packageJSON.version}/index",
	['module'],
	function (module) {
		module.exports = esModule;
	}
);
`
	);

	fs.writeFileSync(
		path.join(outputDir, 'package.json'),
		JSON.stringify(
			{
				main: 'index.js',
				name: packageJSON.name,
				version: packageJSON.version,
			},
			null,
			'\t'
		)
	);

	fs.writeFileSync(
		path.join(outputDir, 'manifest.json'),
		JSON.stringify(
			{
				packages: {
					'/': {
						dest: {
							dir: '.',
							id: '/',
							name: packageJSON.name,
							version: packageJSON.version,
						},
						modules: {
							'index.js': {
								flags: {
									esModule: true,
									useESM: true,
								},
							},
						},
						src: {
							id: '/',
							name: packageJSON.name,
							version: packageJSON.version,
						},
					},
				},
			},
			null,
			'\t'
		)
	);

	fs.writeFileSync(
		path.join(outputDir, 'language.json'),
		JSON.stringify({keys: [...languageKeys].sort()}, null, '\t')
	);

	console.log('liferay-esm-build: wrote', outputDir);
}
