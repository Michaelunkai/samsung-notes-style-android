# S Notes Style

Native Android prototype for a Samsung Notes-style note-taking app.

## Implemented in this workspace

- Kotlin Android project scaffold using Gradle Kotlin DSL and Jetpack Compose.
- Notes home screen with search, folders, tags, favorites, light/dark mode, and list cards.
- Editor screen with auto-persisted title, folder, tag, and block changes.
- Rich text block controls for bold, italic, color, and size-aware rendering.
- Checklist blocks with add, edit, complete, and delete item actions.
- Handwriting/drawing blocks with color palette, stroke capture, clear, and vector JSON persistence.
- Attachment blocks using Android's document picker.
- Audio recording blocks using `MediaRecorder` and runtime microphone permission.
- Local offline-first JSON persistence in app-private storage.
- Home-screen widget provider and static launcher shortcuts for quick access.
- Android share target for incoming text.

## Important implementation notes

- This is a functional local prototype, not a full production Samsung Notes replacement.
- The current persistence layer is file-backed JSON to keep the blank workspace buildable without code generation. The next production step is replacing it with Room entities and migrations.
- Cloud sync, OCR, PDF annotation, conflict resolution, encrypted backups, locked notes, and real-time collaboration remain architectural phases from the original plan.
- Samsung-owned assets, names, formats, and account APIs are intentionally not used.

## Expected build command

Use the portable local toolchain provisioned under `.tooling`:

```powershell
$env:JAVA_HOME = (Resolve-Path .tooling\jdk).Path
$env:ANDROID_HOME = (Resolve-Path .tooling\android-sdk).Path
$env:ANDROID_SDK_ROOT = $env:ANDROID_HOME
$env:Path = "$env:JAVA_HOME\bin;$env:ANDROID_HOME\platform-tools;$((Resolve-Path .tooling\gradle).Path)\bin;$env:Path"
gradle --console=plain :app:assembleDebug
```

Verified locally with `gradle --console=plain :app:assembleDebug`. The debug APK is generated at:

```text
app/build/outputs/apk/debug/app-debug.apk
```
