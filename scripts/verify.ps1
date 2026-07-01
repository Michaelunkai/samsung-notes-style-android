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
    $gradleArgs = @('--console=plain', '--no-daemon', '--max-workers=2')
    $verificationSteps = @(
        @('clean'),
        @(':app:compileDebugKotlin', ':app:compileDebugUnitTestKotlin', ':app:testDebugUnitTest'),
        @(':app:assembleDebug'),
        @(':app:lintDebug')
    )

    foreach ($step in $verificationSteps) {
        & $gradle @gradleArgs @step
        if ($LASTEXITCODE -ne 0) {
            throw "Gradle verification step '$($step -join ' ')' failed with exit code $LASTEXITCODE."
        }
    }
}
finally {
    Pop-Location
}
