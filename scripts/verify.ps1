$ErrorActionPreference = 'Stop'

$root = Split-Path -Parent $PSScriptRoot
$tooling = Join-Path $root '.tooling'

$jdk = Join-Path $tooling 'jdk'
$androidSdk = Join-Path $tooling 'android-sdk'
$gradleHome = Join-Path $tooling 'gradle'
$gradleBat = Join-Path $gradleHome 'bin\gradle.bat'

if (Test-Path -LiteralPath $jdk) {
    $env:JAVA_HOME = (Resolve-Path -LiteralPath $jdk).Path
}
if (-not $env:JAVA_HOME) {
    throw 'JAVA_HOME is not set and .tooling\jdk was not found.'
}

if (Test-Path -LiteralPath $androidSdk) {
    $env:ANDROID_HOME = (Resolve-Path -LiteralPath $androidSdk).Path
    $env:ANDROID_SDK_ROOT = $env:ANDROID_HOME
}
if (-not $env:ANDROID_HOME) {
    throw 'ANDROID_HOME is not set and .tooling\android-sdk was not found.'
}

if (Test-Path -LiteralPath $gradleBat) {
    $gradle = (Resolve-Path -LiteralPath $gradleBat).Path
}
else {
    $gradle = 'gradle'
}

$env:Path = "$env:JAVA_HOME\bin;$env:ANDROID_HOME\platform-tools;$gradleHome\bin;$env:Path"

Push-Location -LiteralPath $root
try {
    & $gradle --console=plain --stop | Out-Null
    & $gradle --console=plain clean :app:assembleDebug :app:testDebugUnitTest :app:lintDebug
    if ($LASTEXITCODE -ne 0) {
        throw "Gradle verification failed with exit code $LASTEXITCODE."
    }
}
finally {
    Pop-Location
}
