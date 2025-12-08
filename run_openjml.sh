#!/bin/bash

# Default OpenJML path (checks env var or local file)
OPENJML_PATH="${OPENJML_HOME}/openjml"

# Function to print usage
usage() {
    echo "Usage: $0 [-j path_to_openjml.jar] [openjml_options]"
    echo "  -j path    Path to openjml.jar (overrides OPENJML_HOME)"
    exit 1
}

# Parse arguments
while getopts "j:" opt; do
  case ${opt} in
    j)
      OPENJML_PATH="$OPTARG"
      ;;
    \?)
      usage
      ;;
  esac
done
shift $((OPTIND -1))

# Check OpenJML path
if [ -z "$OPENJML_PATH" ] && [ -f "./openjml.jar" ]; then
    OPENJML_PATH="./openjml.jar"
fi

if [ ! -f "$OPENJML_PATH" ]; then
    echo "Error: openjml.jar not found at '$OPENJML_PATH'"
    echo "Please set OPENJML_HOME, place openjml.jar in this directory, or use -j option."
    exit 1
fi

echo "Using OpenJML: $OPENJML_PATH"

# Build Classpath
echo "Building classpath..."
mkdir -p target
mvn dependency:build-classpath -Dmdep.outputFile=target/classpath.txt -q
if [ $? -ne 0 ]; then
    echo "Error: Maven build classpath failed."
    exit 1
fi

CLASSPATH=$(cat target/classpath.txt)

# Add module classes to classpath
MODULE_CLASSES=$(find . -type d -path "*/target/classes" | tr '\n' ':')
FULL_CLASSPATH="${CLASSPATH}:${MODULE_CLASSES}"

# Find source files
echo "Finding source files..."
# Construct list of files, excluding tests
find . -name "*.java" -not -path "*/test/*" | grep "/src/main/java/" > target/sources_list.txt
SOURCE_COUNT=$(wc -l < target/sources_list.txt)

if [ "$SOURCE_COUNT" -eq 0 ]; then
    echo "No source files found."
    exit 0
fi

echo "Found $SOURCE_COUNT source files."

# Run OpenJML
echo "Running OpenJML..."
# Use "$@" to pass remaining arguments to OpenJML
"$OPENJML_PATH" -cp "$FULL_CLASSPATH" -check "$@" @target/sources_list.txt

echo "Done."
