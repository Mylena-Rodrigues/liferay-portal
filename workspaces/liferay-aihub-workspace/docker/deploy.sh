#!/usr/bin/env bash

set -o errexit
set -o nounset
set -o pipefail

# Build a single AI Hub module and hot-deploy its jar into the running container.
#
# Usage:
#   ./deploy.sh ai-hub-impl
#   ./deploy.sh :modules:dxp:apps:ai-hub:ai-hub-impl

if [ "$#" -ne 1 ]; then
	echo "Usage: $0 <module-name-or-gradle-path>" >&2
	exit 1
fi

script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
workspace_dir="$(cd "${script_dir}/.." && pwd)"
deploy_dir="${script_dir}/liferay/deploy"

arg="$1"

if [ "${arg:0:1}" = ":" ]; then
	gradle_path="${arg}"
	module_dir="${workspace_dir}${arg//://}"
else
	module_dir="$(find "${workspace_dir}/modules" -type d -name "${arg}" | head -1)"

	if [ -z "${module_dir}" ]; then
		echo "Module \"${arg}\" was not found under modules/." >&2
		exit 1
	fi

	rel="${module_dir#"${workspace_dir}"/}"
	gradle_path=":${rel//\//:}"
fi

echo "Building ${gradle_path} ..."

(cd "${workspace_dir}" && ./gradlew "${gradle_path}:jar")

echo "Deploying to ${deploy_dir} ..."

cp "${module_dir}"/build/libs/*.jar "${deploy_dir}/"

echo "Done. Watch the deploy with: docker compose logs -f liferay"
