# Verification

This project was packaged from the Codex Android implementation workspace on 2026-07-01.

## Local verification command

```powershell
.\scripts\verify.ps1
```

The script uses `.tooling\jdk`, `.tooling\android-sdk`, and `.tooling\gradle` when those local toolchain folders are present. They are intentionally ignored by git.

## Verified surface

- Gradle clean build: `:app:assembleDebug`
- Unit tests: `:app:testDebugUnitTest`
- Android lint: `:app:lintDebug`
- Debug APK path: `app\build\outputs\apk\debug\app-debug.apk`
- Device launch was verified on Samsung SM-S938B with Android package `com.example.snotes`.

`docs\evidence\launch-screen.png` captures the launched app home screen.
