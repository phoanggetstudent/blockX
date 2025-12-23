$ErrorActionPreference = "Continue" # Relax check to avoid crashing on minor warnings

Write-Host "=== SmartGuardian Builder (Manual Mode) ===" -ForegroundColor Cyan

$ScriptDir = $PSScriptRoot
Set-Location -Path $ScriptDir
$SdkDir = "$ScriptDir\android_sdk"
$CmdLineToolsZip = "$ScriptDir\commandlinetools.zip"

# 0. Check Java (Relaxed)
Write-Host "[1/5] Checking Java..."
if (Get-Command "java" -ErrorAction SilentlyContinue) {
    Write-Host "Java OK." -ForegroundColor Green
} else {
    Write-Warning "Java not found in PATH. Ensure JDK 11+ is installed."
    # We continue anyway, maybe gradle can find it.
}

# 1. Check for Manual Download
$ManualPath = "$SdkDir\cmdline-tools"

if (-not (Test-Path $ManualPath)) {
    Write-Host "`n[ACTION REQUIRED] Manual SDK Setup" -ForegroundColor Yellow
    Write-Host "1. Download this file: https://dl.google.com/android/repository/commandlinetools-win-10406996_latest.zip"
    Write-Host "2. Create a folder named 'android_sdk' in this project."
    Write-Host "3. Extract the downloaded zip into it."
    Write-Host "   Structure should be: $SdkDir\cmdline-tools\..."
    Write-Host "4. Run this script again."
    exit 1
}

# Fix folder structure if needed (cmdline-tools/bin -> cmdline-tools/latest/bin)
$LatestDir = "$SdkDir\cmdline-tools\latest"
if (-not (Test-Path $LatestDir)) {
    Write-Host "Adjusting folder structure..."
    $RootExtract = "$SdkDir\cmdline-tools"
    # User likely extracted 'cmdline-tools' folder directly.
    # We need to move its content to 'latest'.
    
    # Check if 'bin' exists in root
    if (Test-Path "$RootExtract\bin") {
        Rename-Item -Path $RootExtract -NewName "latest"
        New-Item -Path $RootExtract -ItemType Directory -Force | Out-Null
        Move-Item -Path "$SdkDir\latest" -Destination $RootExtract
    }
}

# 2. Local Properties
Write-Host "[2/5] Configuring local.properties..."
$EscapedSdkDir = $SdkDir.Replace("\", "\\")
Set-Content -Path "local.properties" -Value "sdk.dir=$EscapedSdkDir"

# 3. Licenses
Write-Host "[3/5] Accepting Licenses..."
$SdkManager = "$SdkDir\cmdline-tools\latest\bin\sdkmanager.bat"
if (Test-Path $SdkManager) {
    try {
        $ProcessInfo = New-Object System.Diagnostics.ProcessStartInfo
        $ProcessInfo.FileName = "cmd.exe"
        $ProcessInfo.Arguments = "/c echo y | `"$SdkManager`" --licenses --sdk_root=`"$SdkDir`""
        $ProcessInfo.RedirectStandardOutput = $true
        $ProcessInfo.UseShellExecute = $false
        $ProcessInfo.CreateNoWindow = $true
        [System.Diagnostics.Process]::Start($ProcessInfo).WaitForExit()
    } catch {
        Write-Warning "License accept skipped."
    }
}

# 4. Gradle Wrapper
Write-Host "[4/5] Checking Gradle..."
$WrapperJar = "$ScriptDir\gradle\wrapper\gradle-wrapper.jar"
if (-not (Test-Path $WrapperJar)) {
    Write-Host "Downloading Gradle Wrapper..."
    Invoke-WebRequest "https://raw.githubusercontent.com/gradle/gradle/master/gradle/wrapper/gradle-wrapper.jar" -OutFile $WrapperJar
}

# 5. Build
Write-Host "[5/5] Building APK..." -ForegroundColor Cyan
Invoke-Expression ".\gradlew.bat assembleDebug"

if ($?) {
    Write-Host "`n=== SUCCESS! ===" -ForegroundColor Green
    Invoke-Item "$ScriptDir\app\build\outputs\apk\debug"
} else {
    Write-Host "Build failed." -ForegroundColor Red
}

