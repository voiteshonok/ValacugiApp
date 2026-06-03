#!/usr/bin/env bash
# Runs instrumented tests (Compose UI on device/emulator). Requires a connected device.
set -euo pipefail

root_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "${root_dir}"

if ! java_home="$(bash "${root_dir}/scripts/find-java-home.sh")"; then
    echo "No JDK found. See scripts/run-unit-tests.sh output for setup hints." >&2
    exit 1
fi

export JAVA_HOME="${java_home}"
export PATH="${JAVA_HOME}/bin:${PATH}"

test_filter="${1:-}"
gradle_args=(":app:connectedDebugAndroidTest")
if [[ -n "${test_filter}" ]]; then
    gradle_args+=("-Pandroid.testInstrumentationRunnerArguments.class=${test_filter}")
fi

adb_bin="adb"
if [[ -x "${HOME}/Android/Sdk/platform-tools/adb" ]]; then
    adb_bin="${HOME}/Android/Sdk/platform-tools/adb"
elif [[ -f "${root_dir}/local.properties" ]]; then
    sdk_dir="$(grep -E '^sdk\.dir=' "${root_dir}/local.properties" | cut -d= -f2-)"
    if [[ -x "${sdk_dir}/platform-tools/adb" ]]; then
        adb_bin="${sdk_dir}/platform-tools/adb"
    fi
fi

if ! "${adb_bin}" devices 2>/dev/null | grep -qE 'device$'; then
    echo "No Android device/emulator connected. Start one, then retry." >&2
    echo "  ${adb_bin} devices" >&2
    exit 1
fi

echo "Using JAVA_HOME=${JAVA_HOME}"
./gradlew "${gradle_args[@]}"
