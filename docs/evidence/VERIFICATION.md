# Verification

This project was packaged from the Codex Android implementation workspace on 2026-07-01 and updated through version `0.4.4` on 2026-07-02.

## Local verification command

```powershell
.\scripts\verify.ps1
```

The script uses `.tooling\jdk`, `.tooling\android-sdk`, and `.tooling\gradle` when those local toolchain folders are present. They are intentionally ignored by git.

## Current verified surface

- Full local verification completed on 2026-07-02 for version `0.4.4`.
- Command shape: `:app:compileDebugKotlin :app:testDebugUnitTest :app:lintDebug :app:assembleDebug`
- Debug Kotlin compilation: `:app:compileDebugKotlin`
- JVM unit tests: `:app:testDebugUnitTest`
- Android manifest/resource processing: `:app:processDebugMainManifest`, `:app:processDebugResources`
- Debug APK assembly: `:app:assembleDebug`
- Android lint: `:app:lintDebug`
- Debug APK path: `app\build\outputs\apk\debug\app-debug.apk`

Current Android-device testing is intentionally deferred until the explicit approval gate.

## Earlier device evidence

- Gradle clean build: `:app:assembleDebug`
- Unit tests: `:app:testDebugUnitTest`
- Android lint: `:app:lintDebug`
- Debug APK path: `app\build\outputs\apk\debug\app-debug.apk`
- A previous build was launched on Samsung SM-S938B with Android package `com.example.snotes`.

`docs\evidence\launch-screen.png` captures that earlier launched app home screen and should not be treated as current `0.4.4` device proof.
