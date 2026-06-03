#!/usr/bin/env bash
# Resolves JAVA_HOME for Gradle when it is not set in the shell.
set -euo pipefail

if [[ -n "${JAVA_HOME:-}" && -x "${JAVA_HOME}/bin/java" ]]; then
    printf '%s\n' "${JAVA_HOME}"
    exit 0
fi

if command -v java >/dev/null 2>&1; then
    java_bin="$(command -v java)"
    java_home="$(dirname "$(dirname "$(readlink -f "${java_bin}")")")"
    printf '%s\n' "${java_home}"
    exit 0
fi

candidates=(
    "/usr/lib/jvm/java-21-openjdk-amd64"
    "/usr/lib/jvm/java-17-openjdk-amd64"
    "/usr/lib/jvm/java-17-openjdk"
    "/usr/lib/jvm/default-java"
    "${HOME}/.gradle/jdks"/*
    "${HOME}/.local/share/JetBrains/Toolbox/apps/AndroidStudio"/*/jbr
    "/opt/android-studio/jbr"
    "/usr/local/android-studio/jbr"
    "${HOME}/android-studio/jbr"
)

for candidate in "${candidates[@]}"; do
    if [[ -x "${candidate}/bin/java" ]]; then
        printf '%s\n' "${candidate}"
        exit 0
    fi
done

for studio_jbr in "${HOME}"/.local/share/JetBrains/Toolbox/apps/AndroidStudio/*/jbr; do
    if [[ -x "${studio_jbr}/bin/java" ]]; then
        printf '%s\n' "${studio_jbr}"
        exit 0
    fi
done

for snap_jbr in /snap/android-studio/*/jbr; do
    if [[ -x "${snap_jbr}/bin/java" ]]; then
        printf '%s\n' "${snap_jbr}"
        exit 0
    fi
done

exit 1
