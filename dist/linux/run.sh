#!/usr/bin/env bash
DIR="$(cd "$(dirname "$0")" && pwd)"
java -jar "$DIR/../../target/noteshelf-1.0.0.jar"
