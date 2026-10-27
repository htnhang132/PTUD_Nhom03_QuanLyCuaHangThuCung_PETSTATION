param(
    [string]$Server = 'localhost,1433',
    [string]$Database = 'PetStationDemo2',
    [string]$SqlUser = '',
    [switch]$Reset
)
$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path $PSScriptRoot -Parent
if ($Database -notmatch '^[A-Za-z][A-Za-z0-9_]{0,100}$' -or
    $Database -in @('master', 'model', 'msdb', 'tempdb')) { throw 'Invalid application database name.' }
if (-not (Get-Command sqlcmd -ErrorAction SilentlyContinue)) { throw 'Install sqlcmd or run the two SQL files in SSMS. See SETUP.md.' }
$sqlArgs = @('-S', $Server, '-C', '-b', '-f', '65001')
if ($SqlUser) {
    if (-not $env:SQLCMDPASSWORD) { throw 'Set SQLCMDPASSWORD for SQL authentication; do not put the password in command arguments.' }
    $sqlArgs += @('-U', $SqlUser)
} else { $sqlArgs += '-E' }

function Invoke-Query([string]$Query, [string]$Db = 'master') {
    $result = @(& sqlcmd @sqlArgs -d $Db -h -1 -W -Q $Query)
    if ($LASTEXITCODE -ne 0) { throw "SQL operation failed in $Db. No later steps were run." }
    return $result
}

$major = ((Invoke-Query "SET NOCOUNT ON; SELECT CONVERT(int,SERVERPROPERTY('ProductMajorVersion'));" | Out-String).Trim())
if ([int]$major -lt 15) { throw 'SQL Server 2019 or newer is required. The SSMS version is not the SQL Server version.' }
Invoke-Query "IF DB_ID(N'$Database') IS NULL CREATE DATABASE [$Database] COLLATE Vietnamese_CI_AS;" | Out-Null
$tableCount = ((Invoke-Query "SET NOCOUNT ON; SELECT COUNT(*) FROM sys.tables WHERE is_ms_shipped=0 AND name<>'sysdiagrams';" $Database | Out-String).Trim())
if ([int]$tableCount -gt 0 -and -not $Reset) {
    $ready = ((Invoke-Query "SET NOCOUNT ON; IF OBJECT_ID('dbo.cai_dat') IS NOT NULL SELECT COUNT(*) FROM dbo.cai_dat WHERE khoa='APP_SCHEMA_VERSION' AND gia_tri='20261026'; ELSE SELECT 0;" $Database | Out-String).Trim())
    if ($ready -eq '1') { Write-Output "$Database is already initialized. Data was not changed."; exit 0 }
    throw 'An older database exists. Use -Reset only after reviewing the reset and backup instructions in SETUP.md.'
}

if ([int]$tableCount -gt 0) {
    # Only reset tables belonging to this application, never unrelated tables.
    $schemaPath = Join-Path $projectRoot 'database/001_schema.sql'
    $tableNames = [regex]::Matches([IO.File]::ReadAllText($schemaPath), 'CREATE TABLE dbo\.\[([^\]]+)\]') |
        ForEach-Object { $_.Groups[1].Value }
    $allowed = @($tableNames) + @('doi_chieu_ma_du_lieu', 'doi_chieu_unicode', 'phien_ban_du_lieu')
    $allowedSql = ($allowed | ForEach-Object { "N'$_'" }) -join ','
    $unknown = ((Invoke-Query "SET NOCOUNT ON; SELECT COUNT(*) FROM sys.tables t JOIN sys.schemas s ON s.schema_id=t.schema_id WHERE t.is_ms_shipped=0 AND t.name<>'sysdiagrams' AND (s.name<>'dbo' OR t.name NOT IN ($allowedSql));" $Database | Out-String).Trim())
    if ([int]$unknown -gt 0) { throw 'Unknown tables exist. Reset was stopped to protect unrelated data.' }

    $stamp = Get-Date -Format 'yyyyMMdd-HHmmss'
    $backupSql = @"
SET NOCOUNT ON;
DECLARE @folder nvarchar(4000)=CONVERT(nvarchar(4000),SERVERPROPERTY('InstanceDefaultBackupPath'));
IF @folder IS NULL THROW 50000,'SQL Server backup directory is not configured.',1;
IF RIGHT(@folder,1) NOT IN (NCHAR(92),N'/') SET @folder=@folder+NCHAR(92);
DECLARE @backup nvarchar(4000)=@folder+N'$Database-before-reset-$stamp.bak';
BACKUP DATABASE [$Database] TO DISK=@backup WITH COPY_ONLY,CHECKSUM;
RESTORE VERIFYONLY FROM DISK=@backup WITH CHECKSUM;
SELECT @backup;
"@
    $backupResult = Invoke-Query $backupSql
    $backupLog = Join-Path $projectRoot "target/database-backups/$Database-$stamp.txt"
    New-Item -ItemType Directory -Force -Path (Split-Path $backupLog -Parent) | Out-Null
    $backupResult | Set-Content -LiteralPath $backupLog -Encoding UTF8
    Write-Output "Backup verified. Backup location is recorded in $backupLog"

    Invoke-Query @"
SET XACT_ABORT ON;
BEGIN TRANSACTION;
DECLARE @sql nvarchar(max)=N'';
SELECT @sql=@sql+N'ALTER TABLE '+QUOTENAME(OBJECT_SCHEMA_NAME(parent_object_id))+N'.'+QUOTENAME(OBJECT_NAME(parent_object_id))+N' DROP CONSTRAINT '+QUOTENAME(name)+N';' FROM sys.foreign_keys;
EXEC sys.sp_executesql @sql;
SET @sql=N'';
SELECT @sql=@sql+N'DROP TABLE '+QUOTENAME(SCHEMA_NAME(schema_id))+N'.'+QUOTENAME(name)+N';' FROM sys.tables WHERE is_ms_shipped=0 AND name<>'sysdiagrams';
EXEC sys.sp_executesql @sql;
COMMIT;
"@ $Database | Out-Null
}

foreach ($file in @('001_schema.sql', '002_sample_data.sql')) {
    & sqlcmd @sqlArgs -d $Database -i (Join-Path $projectRoot "database/$file")
    if ($LASTEXITCODE -ne 0) { throw "Setup failed at $file. If this was a reset, the verified backup is listed under target/database-backups." }
}
Write-Output "$Database is ready. Demo application password: PetStation@123 (change it after signing in)."
