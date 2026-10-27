$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path $PSScriptRoot -Parent
Push-Location $projectRoot
try {
    & (Join-Path $projectRoot 'mvnw.cmd') -B -ntp compile exec:java '-Dexec.mainClass=com.petstation.App'
    $runExitCode = $LASTEXITCODE
} finally { Pop-Location }
exit $runExitCode
