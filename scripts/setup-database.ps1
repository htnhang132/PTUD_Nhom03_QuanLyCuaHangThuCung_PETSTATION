param(
    [string]$Server = 'localhost,1433',
    [string]$Database = 'PetStationDemo2'
)
$ErrorActionPreference = 'Stop'
if ($Database -notmatch '^[A-Za-z][A-Za-z0-9_]{0,100}$') {
    throw 'Database name must contain only letters, digits and underscores.'
}
$projectRoot = Split-Path -Parent $PSScriptRoot
& sqlcmd -S $Server -E -C -b -Q "IF DB_ID(N'$Database') IS NULL CREATE DATABASE [$Database] COLLATE Vietnamese_CI_AS;"
if ($LASTEXITCODE -ne 0) { throw 'Cannot create or access the database.' }
foreach ($file in @('database\001_schema.sql','database\002_sample_data.sql')) {
    & sqlcmd -S $Server -E -C -b -f 65001 -d $Database -i (Join-Path $projectRoot $file)
    if ($LASTEXITCODE -ne 0) { throw "SQL setup failed: $file" }
}
Write-Output "Database $Database is ready. Existing rows and account passwords were preserved."
