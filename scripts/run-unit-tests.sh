#!/usr/bin/env bash
# Runs JVM unit tests with an auto-detected JDK (see scripts/find-java-home.sh).
set -euo pipefail

root_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "${root_dir}"

if ! java_home="$(bash "${root_dir}/scripts/find-java-home.sh")"; then
    echo "No JDK found. Install one, then retry:" >&2
    echo "  sudo apt install openjdk-17-jdk" >&2
    echo "  export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64" >&2
    echo "" >&2
    echo "Or set JAVA_HOME to Android Studio's JBR, e.g.:" >&2
    echo '  export JAVA_HOME="$HOME/.local/share/JetBrains/Toolbox/apps/AndroidStudio/<version>/jbr"' >&2
    exit 1
fi

export JAVA_HOME="${java_home}"
export PATH="${JAVA_HOME}/bin:${PATH}"

test_filter="${1:-}"
gradle_args=(":app:testDebugUnitTest")
if [[ -n "${test_filter}" ]]; then
    gradle_args+=("--tests" "${test_filter}")
fi

echo "Using JAVA_HOME=${JAVA_HOME}"
./gradlew "${gradle_args[@]}"
