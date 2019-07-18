#!/bin/bash

# Copyright 2019 Google LLC
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

readonly REPO_NAME="${1}"
readonly TAG_NAME="${2}"
readonly TOKEN="${ACCESS_TOKEN}"
# Get GitHub user and GitHub repo from REPO_NAME
IFS='_' read -ra array <<< "${REPO_NAME}"
github_user="${array[1]}"
github_repo="${array[2]}"
if [[ -z "${github_user}" ]]
then
  github_user="GoogleCloudPlatform"
  github_repo="${REPO_NAME}"
fi
# Create request.json with request parameters
echo "{\"tag_name\": \"${TAG_NAME}\",\"name\": \"${TAG_NAME}\"}" > request.json
# Create a request for creating a release on GitHub page
readonly resp_file="response.json"
response_code="$(curl -# -X POST \
-H "Content-Type:application/json" \
-H "Accept:application/json" \
-w "%{http_code}" \
--data-binary "@/workspace/request.json" \
"https://api.github.com/repos/${github_user}/${github_repo}/releases?access_token=${TOKEN}" \
-o "${resp_file}")"
# Check status code
if [[ "${response_code}" != 201 ]]; then
  cat "${resp_file}"
  exit 1
fi
# Get release id from response.json
release_id="$(grep -wm 1 "id" /workspace/response.json \
  | grep -Eo "[[:digit:]]+")"
# Get JAR version from pom.xml
jar_version="$(grep -m 1 "<version>" /workspace/pom.xml \
  | grep -Eo "[[:digit:]]+.[[:digit:]]+")"
jar_name="qupath-chcapi-extension-${jar_version}.jar"
# Upload JAR to GitHub releases page
response_code="$(curl -# -X POST -H "Authorization: token ${TOKEN}" \
-H "Content-Type:application/octet-stream" \
-w "%{http_code}" \
--data-binary "@/workspace/target/${jar_name}" \
"https://uploads.github.com/repos/${github_user}/${github_repo}/releases/${release_id}/assets?name=${jar_name}" \
-o "${resp_file}")"
# Check status code
if [[ "${response_code}" != 201 ]]; then
  cat "${resp_file}"
  exit 2
fi
