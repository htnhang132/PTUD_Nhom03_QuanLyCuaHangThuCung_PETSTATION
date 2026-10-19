$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot

# Use an installed JDK even when VS Code still has an old PATH.
$jdkCandidates = @(
    $env:JAVA_HOME
    "$env:ProgramFiles\Java\jdk-17"
)
$jdkHome = $jdkCandidates | Where-Object {
    $_ -and (Test-Path -LiteralPath (Join-Path $_ 'bin\javac.exe'))
} | Select-Object -First 1
if (-not $jdkHome) {
    throw 'JDK 17 or newer is required. Set JAVA_HOME to the JDK installation folder.'
}
$env:JAVA_HOME = $jdkHome
$env:Path = "$(Join-Path $jdkHome 'bin');$env:Path"

$mavenCommand = Get-Command mvn.cmd -ErrorAction SilentlyContinue
if ($mavenCommand) {
    $mavenPath = $mavenCommand.Source
} else {
    # IntelliJ ships Maven; reuse it if standalone Maven is not on PATH.
    $mavenPath = Get-ChildItem -Path "$env:ProgramFiles\JetBrains\IntelliJ IDEA *\plugins\maven\lib\maven3\bin\mvn.cmd" -ErrorAction SilentlyContinue |
        Sort-Object FullName -Descending |
        Select-Object -First 1 -ExpandProperty FullName
}
if (-not $mavenPath) {
    throw 'Maven was not found. Add the Maven bin folder to PATH.'
}

# Compile the whole Maven project and include its dependencies and resources.
Push-Location $projectRoot
try {
    & $mavenPath -B -ntp -f (Join-Path $projectRoot 'pom.xml') compile exec:java '-Dexec.mainClass=com.petstation.App'
    $runExitCode = $LASTEXITCODE
} finally {
    Pop-Location
}
exit $runExitCode
