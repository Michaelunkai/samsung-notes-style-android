# S Notes Style

Native Android prototype for a Samsung Notes-style note-taking app.

Current local build version: `0.2.0`.

## Implemented in this workspace

- Kotlin Android project scaffold using Gradle Kotlin DSL and Jetpack Compose.
- Notes home screen with list/grid modes, rich note-card metadata, expanded sorting, search scopes, contextual empty states, folders, tags, favorites, pinned notes, trash with restore-all/empty-trash actions, light/dark mode, and multi-select actions including batch duplicate.
- Folder and tag organization with filters, bulk move/tag actions, and rename actions for selected folders and tags.
- Editor screen with auto-persisted title, folder, tag, template, paper color, mixed block changes, and capped note-level undo/redo.
- In-note search panel with match counts, snippets, previous/next navigation, active-match scrolling, and highlighted matched blocks.
- Rich text block controls for bold, italic, underline, text color, highlight color, size presets, font family, and alignment.
- Sticky note blocks with color palette, minimize/expand, search/export/details integration, and duplication.
- Checklist blocks with add, edit, complete all, uncheck all, clear done, item reorder, and delete item actions.
- Handwriting/drawing blocks with pen, fountain pen, highlighter, eraser, stroke width, color palette, undo, clear, vector JSON persistence, and segment-preserving erasing.
- Mixed note block reorder and duplicate controls for text, sticky, checklist, handwriting, attachment, and audio blocks.
- Attachment blocks using Android's document picker, FileProvider-backed camera capture, image previews, file metadata, and open-with-Android viewer actions.
- Audio recording blocks using `MediaRecorder`, runtime microphone permission, playback, searchable timestamp markers, and shared audio import as playable audio blocks.
- Local offline-first Room persistence with a first-run migration path from the legacy JSON note store.
- Row-level Room upserts for normal note edits to avoid rewriting the whole note table during editor changes.
- Local Notes PIN support for locked notes, with locked previews hidden from search and widgets.
- JSON backup export/import with schema/app/note-count metadata plus per-note plain text export/share and PDF export.
- Android share targets for incoming text, images, PDFs, generic files, and audio.
- Home-screen widget provider with latest-note preview, latest-note open target, and quick text/checklist/sticky/handwriting actions.
- Static launcher shortcuts for quick text, checklist, sticky, and handwriting notes.

## Important implementation notes

- This is a functional local prototype, not a full production Samsung Notes replacement.
- The current persistence layer uses Room for indexed note metadata and stores mixed note blocks as JSON payloads. A production-scale version would split pages, ink strokes, attachments, and audio timeline events into their own Room tables.
- Cloud sync, OCR, PDF annotation/import editing, Samsung account integration, real-time collaboration, and server-side conflict resolution remain architectural phases outside this local prototype.
- Samsung-owned assets, names, formats, and account APIs are intentionally not used.

## Verification

Use the repository verification script:

```powershell
.\scripts\verify.ps1
```

The script runs clean build, debug APK assembly, JVM unit tests, lint, and supporting checks. The debug APK is generated at:

```text
app/build/outputs/apk/debug/app-debug.apk
```
