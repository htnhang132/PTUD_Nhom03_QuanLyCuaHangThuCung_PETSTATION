param([string]$Server = '', [string]$SqlUser = '')
$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path $PSScriptRoot -Parent
$javaCommand = if ($env:JAVA_HOME) { Join-Path $env:JAVA_HOME 'bin/java.exe' } else { (Get-Command java -ErrorAction Stop).Source }
if (-not (Test-Path -LiteralPath $javaCommand)) { throw 'JAVA_HOME does not point to a working JDK.' }
$previousPreference = $ErrorActionPreference
try {
    # Java writes version information to stderr even when it exits successfully.
    $ErrorActionPreference = 'Continue'
    $properties = (& $javaCommand -XshowSettings:properties -version 2>&1 | Out-String)
    $javaExitCode = $LASTEXITCODE
} finally { $ErrorActionPreference = $previousPreference }
if ($javaExitCode -ne 0) { throw 'Java could not start.' }
if ($properties -notmatch 'java.specification.version\s*=\s*([0-9.]+)') { throw 'Cannot detect Java version.' }
$javaVersion = $Matches[1]
$major = if ($javaVersion.StartsWith('1.')) { [int]$javaVersion.Split('.')[1] } else { [int]$javaVersion.Split('.')[0] }
if ($major -lt 17) { throw "Java $javaVersion is too old. Install JDK 17 or newer and fix JAVA_HOME/IDE Maven JDK." }
$javac = Join-Path (Split-Path $javaCommand -Parent) 'javac.exe'
if (-not (Test-Path -LiteralPath $javac)) { throw 'A JDK is required, not a runtime-only JRE.' }
Write-Output "Java $javaVersion is compatible."
& (Join-Path $projectRoot 'mvnw.cmd') -v
if ($LASTEXITCODE -ne 0) { throw 'Maven Wrapper cannot start. Check JAVA_HOME, network access and proxy configuration.' }
if ($Server) {
    $sqlArgs = @('-S', $Server, '-C', '-b', '-h', '-1', '-W')
    if ($SqlUser) { $sqlArgs += @('-U', $SqlUser) } else { $sqlArgs += '-E' }
    & sqlcmd @sqlArgs -Q "SET NOCOUNT ON; SELECT CONVERT(varchar(30),SERVERPROPERTY('ProductVersion')) AS sql_server_version;"
    if ($LASTEXITCODE -ne 0) { throw 'Cannot connect with sqlcmd. Check SQL Server service, TCP port and authentication.' }
}
