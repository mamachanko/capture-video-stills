#!/usr/bin/env bash

set -euxo pipefail

cd "$(dirname "$0")"

cd ../api

./gradlew clean bootrun
