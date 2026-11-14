param(
    [string]$Server = 'localhost,1433',
    [string]$SqlUser = '',
    [string]$Database = 'PetStationIntegrationTest'
)
$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path $PSScriptRoot -Parent
$previousUrl = $env:PETSTATION_DB_URL
$previousUser = $env:PETSTATION_DB_USER
$previousPassword = $env:PETSTATION_DB_PASSWORD
$previousFlag = $env:PETSTATION_SQL_TESTS
Push-Location $projectRoot
try {
    $setupArgs = @('-NoProfile', '-ExecutionPolicy', 'Bypass', '-File', (Join-Path $PSScriptRoot 'setup-database.ps1'), '-Server', $Server, '-Database', $Database, '-Reset')
    if ($SqlUser) {
        $setupArgs += @('-SqlUser', $SqlUser)
        $env:PETSTATION_DB_USER = $SqlUser
    }
    if (-not $env:PETSTATION_DB_PASSWORD -and $env:SQLCMDPASSWORD) {
        $env:PETSTATION_DB_PASSWORD = $env:SQLCMDPASSWORD
    }
    & powershell @setupArgs
    if ($LASTEXITCODE -ne 0) { throw 'Cannot prepare the isolated test database.' }
    $env:PETSTATION_DB_URL = "jdbc:sqlserver://$($Server.Replace(',',':'));databaseName=$Database;encrypt=true;trustServerCertificate=true"
    $env:PETSTATION_SQL_TESTS = 'true'
    & (Join-Path $projectRoot 'mvnw.cmd') -B -ntp verify
    $testExitCode = $LASTEXITCODE
} finally {
    $env:PETSTATION_DB_URL = $previousUrl
    $env:PETSTATION_DB_USER = $previousUser
    $env:PETSTATION_DB_PASSWORD = $previousPassword
    $env:PETSTATION_SQL_TESTS = $previousFlag
    Pop-Location
}
exit $testExitCode
