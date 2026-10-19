param([string]$Server = 'localhost,1433')
$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot
$testDatabase = 'PetStationIntegrationTest'
if (-not $env:JAVA_HOME -or -not (Test-Path -LiteralPath "$env:JAVA_HOME\bin\javac.exe")) {
    $env:JAVA_HOME = "$env:ProgramFiles\Java\jdk-17"
}
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
$previousUrl = $env:PETSTATION_DB_URL
$previousFlag = $env:PETSTATION_SQL_TESTS
Push-Location $projectRoot
try {
    & sqlcmd -S $Server -E -C -b -Q "IF DB_ID(N'PetStationIntegrationTest') IS NULL CREATE DATABASE PetStationIntegrationTest COLLATE Vietnamese_CI_AS;"
    if ($LASTEXITCODE -ne 0) { throw 'Cannot create the integration database.' }
    foreach ($file in @('database\001_schema.sql','database\002_sample_data.sql')) {
        & sqlcmd -S $Server -E -C -b -f 65001 -d $testDatabase -i $file
        if ($LASTEXITCODE -ne 0) { throw "SQL setup failed: $file" }
    }
    $env:PETSTATION_DB_URL = "jdbc:sqlserver://$($Server.Replace(',',':'));databaseName=$testDatabase;encrypt=true;trustServerCertificate=true"
    $env:PETSTATION_SQL_TESTS = 'true'
    & mvn.cmd -B -ntp test
    $testExitCode = $LASTEXITCODE
} finally {
    $env:PETSTATION_DB_URL = $previousUrl
    $env:PETSTATION_SQL_TESTS = $previousFlag
    Pop-Location
}
exit $testExitCode
