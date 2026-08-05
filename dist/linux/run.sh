#!/usr/bin/env bash
set -euo pipefail
DIR="$(cd "$(dirname "$0")" && pwd)"
java -jar "$DIR/../../target/swing-notepad-1.0.0.jar"
