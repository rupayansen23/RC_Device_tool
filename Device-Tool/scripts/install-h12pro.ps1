param(
    [string]$PackageName = "com.example.devicetoolv1.debug",
    [string]$ActivityName = "com.example.devicetoolv1.MainActivity"
)

$ErrorActionPreference = "Stop"
$repoRoot = Split-Path -Parent $PSScriptRoot
Set-Location $repoRoot

Write-Host "Checking connected Android devices..."
$devices = adb devices | Select-String -Pattern "device$"

if ($devices.Count -eq 0) {
    Write-Error "No authorized Android device found. Enable USB debugging on the H12 Pro and accept the RSA prompt."
}

Write-Host "Building and installing debug APK..."
.\gradlew.bat :app:installDebug

Write-Host "Launching Device Tool on H12 Pro..."
adb shell am start -n "$PackageName/$ActivityName"
