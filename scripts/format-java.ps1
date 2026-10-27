param([ValidateSet('check', 'apply')][string]$Mode = 'check')
$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path $PSScriptRoot -Parent
$formatterVersion = '1.24.0'
$formatterPath = Join-Path $env:USERPROFILE ".m2/repository/com/google/googlejavaformat/google-java-format/$formatterVersion/google-java-format-$formatterVersion-all-deps.jar"
if (-not (Test-Path -LiteralPath $formatterPath)) {
    throw 'Google Java Format 1.24.0 is missing. Run: .\mvnw.cmd -B -ntp dependency:get -Dartifact=com.google.googlejavaformat:google-java-format:1.24.0:jar:all-deps'
}
$javaFiles = @(Get-ChildItem -LiteralPath (Join-Path $projectRoot 'src') -Recurse -Filter '*.java' | ForEach-Object { $_.FullName })
$formatterArgs = @('-jar', $formatterPath, '--aosp')
if ($Mode -eq 'apply') { $formatterArgs += '--replace' }
else { $formatterArgs += @('--dry-run', '--set-exit-if-changed') }
& java @formatterArgs @javaFiles
if ($LASTEXITCODE -ne 0) { throw 'Java formatting check failed. Use -Mode apply to format sources.' }
