<#
.SYNOPSIS
    Runs OpenJML on the CTruco project.
.DESCRIPTION
    This script builds the classpath using Maven and executes OpenJML on all source files
    found in the src/main/java directories of the modules.
.PARAMETER OpenJmlPath
    Path to the OpenJML jar file. Defaults to $env:OPENJML_HOME\openjml.jar or checks common locations.
.PARAMETER Options
    Additional options to pass to OpenJML (e.g. -esc, -rac).
#>
param(
    [string]$OpenJmlPath,
    [string[]]$Options = @("-check")
)

# --- Configuration ---

# Try to find OpenJML if not provided
if (-not $OpenJmlPath) {
    if ($env:OPENJML_HOME) {
        $OpenJmlPath = Join-Path $env:OPENJML_HOME "openjml.jar"
    } elseif (Test-Path ".\openjml.jar") {
        $OpenJmlPath = ".\openjml.jar"
    } else {
        Write-Error "Could not find openjml.jar. Please set OPENJML_HOME environment variable, place openjml.jar in this directory, or pass the -OpenJmlPath argument."
        exit 1
    }
}

if (-not (Test-Path $OpenJmlPath)) {
    Write-Error "OpenJML jar not found at: $OpenJmlPath"
    exit 1
}

Write-Host "Using OpenJML at: $OpenJmlPath" -ForegroundColor Cyan

# --- Maven Classpath ---

Write-Host "Building project classpath..." -ForegroundColor Cyan
# We use dependency:build-classpath to get the classpath. 
# outputFile helps us capture it cleanly without parsing logs.
$cpFile = "target/classpath.txt"
# Ensure target dir exists
New-Item -ItemType Directory -Force -Path "target" | Out-Null

$mvnArgs = "dependency:build-classpath -Dmdep.outputFile=$cpFile -q"
# Run maven. We use cmd /c to ensure it picks up mvn from path correctly in all shells
cmd /c "mvn $mvnArgs"

if ($LASTEXITCODE -ne 0) {
    Write-Error "Maven build classpath failed."
    exit $LASTEXITCODE
}

$classpath = Get-Content $cpFile
# Add the compiled classes directories to classpath so modules can see each other if needed, 
# though usually for source analysis we care about dependencies. 
# For multi-module, it's safer to include the target/classes of modules if we can, but let's start with dependencies.

# OpenJML often needs to know about the sub-modules classes if we are checking them.
# A simple strategy for multi-module: include all `target/classes` in the classpath.
$moduleClasses = Get-ChildItem -Path . -Recurse -Filter "classes" | Where-Object { $_.FullName -like "*\target\classes" } | Select-Object -ExpandProperty FullName
$fullClasspath = "$classpath;" + ($moduleClasses -join ";")

# --- Source Files ---

Write-Host "Finding source files..." -ForegroundColor Cyan
$srcFiles = Get-ChildItem -Path . -Recurse -Include "*.java" | 
            Where-Object { $_.FullName -notmatch "test" -and $_.FullName -match "src\\main\\java" } | 
            Select-Object -ExpandProperty FullName

if ($srcFiles.Count -eq 0) {
    Write-Warning "No source files found."
    exit 0
}

Write-Host "Found $($srcFiles.Count) source files." -ForegroundColor Green

# --- Execution ---

# Construct the command arguments
# Note: In PowerShell, we can pass the array of files. 
# However, if there are TOO many, command line length might be an issue.
# OpenJML supports @files arguments? Let's write sources to a file to be safe.

$sourcesFile = "target/sources_list.txt"
$srcFiles | Out-File -FilePath $sourcesFile -Encoding ASCII

Write-Host "Running OpenJML..." -ForegroundColor Cyan
# java -jar openjml.jar -cp ... <options> @sources
# We use specific java version if needed, but 'java' is usually default.

$javaArgs = @(
    "-jar", $OpenJmlPath,
    "-cp", $fullClasspath
) + $Options + @("@$sourcesFile")

# Print the command for debugging (optional)
# Write-Host "java $javaArgs"

& java $javaArgs

Write-Host "Done." -ForegroundColor Cyan
