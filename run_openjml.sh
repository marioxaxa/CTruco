#!/bin/bash

# Default OpenJML path (checks env var or local file)
# Default OpenJML path (checks env var or local file)
OPENJML_PATH="${OPENJML_HOME}/openjml"

# Parse arguments to separate OpenJML args from Target path
# Usage: ./run_openjml.sh [-j path] [openjml-args] [target-path]
# Example: ./run_openjml.sh -esc domain
# Example: ./run_openjml.sh -check

OPENJML_ARGS=""
TARGET_PATH="."
USE_FILTER=false
SHOW_STATS=false

while [[ $# -gt 0 ]]; do
    case $1 in
        -j)
            OPENJML_PATH="$2"
            shift # past argument
            shift # past value
            ;;
        -filter)
            USE_FILTER=true
            shift # past argument
            ;;
        -stats)
            SHOW_STATS=true
            shift # past argument
            ;;
        -*)
            OPENJML_ARGS="$OPENJML_ARGS $1"
            shift # past argument
            ;;
        *)
            TARGET_PATH="$1"
            shift # past argument
            ;;
    esac
done

# Check if stats requested
if [ "$SHOW_STATS" = true ]; then
    echo "Calculating JML statistics in '$TARGET_PATH'..."
    JML_FILES=$(find "$TARGET_PATH" -type f -name "*.java" -not -path "*/test/*" -not -path "./.volume/*" -not -name "module-info.java" | xargs grep -l -E "//@|/\*@" | wc -l)
    TOTAL_FILES=$(find "$TARGET_PATH" -type f -name "*.java" -not -path "*/test/*" -not -path "./.volume/*" -not -name "module-info.java" | wc -l)
    
    echo "Files with JML annotations: $JML_FILES"
    echo "Total Java files: $TOTAL_FILES"
    exit 0
fi

# Check OpenJML path
if [ -z "$OPENJML_PATH" ] && [ -f "./openjml.jar" ]; then
    OPENJML_PATH="./openjml.jar"
fi

if [ ! -f "$OPENJML_PATH" ]; then
    echo "Error: openjml.jar not found at '$OPENJML_PATH'"
    # Try to find it in home dir if not set
    if [ -f "$HOME/openjml/openjml" ]; then
       OPENJML_PATH="$HOME/openjml/openjml"
       echo "Found at default location: $OPENJML_PATH"
    else 
       echo "Please set OPENJML_HOME, place openjml.jar in this directory, or use -j option."
       exit 1
    fi
fi
echo "Using OpenJML: $OPENJML_PATH"

# Default to -check if no OpenJML args
if [ -z "$OPENJML_ARGS" ]; then
    OPENJML_ARGS="-check"
fi

# Find source files in the target path
echo "Finding source files in '$TARGET_PATH'..."
# Construct list of files, excluding tests, module definitions, and .volume directories
find "$TARGET_PATH" -type f -name "*.java" -not -path "*/test/*" -not -path "./.volume/*" -not -name "module-info.java" | grep "/src/main/java/" > target/sources_list.txt
SOURCE_COUNT=$(wc -l < target/sources_list.txt)

if [ "$SOURCE_COUNT" -eq 0 ]; then
    echo "No source files found in $TARGET_PATH."
    exit 0
fi

echo "Found $SOURCE_COUNT source files."
echo "Running OpenJML with args: $OPENJML_ARGS"

# Check if OPENJML_PATH ends in .jar
CMD=""
if [[ "$OPENJML_PATH" == *.jar ]]; then
    CMD="java -jar \"$OPENJML_PATH\" -cp \"$FULL_CLASSPATH\" $OPENJML_ARGS @target/sources_list.txt"
else
    # Assume it is a script/executable
    CMD="\"$OPENJML_PATH\" -cp \"$FULL_CLASSPATH\" $OPENJML_ARGS @target/sources_list.txt"
fi

# Run the command
if [ "$USE_FILTER" = true ]; then
    echo "Output filtered for JML warnings/errors..."
    eval "$CMD" 2>&1 | grep -E "warning:|error:|assertion|invariant|requires|ensures|postcondition|precondition" | grep -v "module-info" | grep -v "cannot find symbol" | grep -v "package .* does not exist"
else
    eval "$CMD"
fi

echo "Done."
