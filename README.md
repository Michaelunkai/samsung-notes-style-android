# S Notes Style

Native Android prototype for a Samsung Notes-style note-taking app.

Current local build version: `0.4.6`.

## Implemented in this workspace

- Kotlin Android project scaffold using Gradle Kotlin DSL and Jetpack Compose.
- Notes home screen with persisted list/grid, sort, search-scope, and default note-type preferences, rich note-card metadata including checklist progress, reminder labels, Trash-age/retention labels with local expired-Trash pruning, and media summaries, expanded sorting including checklist-progress, reminder-soonest, media-first, trash-newest, and trash-review-ending modes, scoped search with clear action and active-scope summaries, contextual empty states, folders, tags, favorites, pinned notes, reminders, a dedicated locked-notes view, trash with restore-all/empty-trash actions, light/dark mode, and multi-select actions including select-all-visible, batch duplicate, pin/unpin, favorite/unfavorite, and lock/unlock.
- Folder and tag organization with note-count chips, nested folder rollups, filters, contextual note creation, bulk move/tag/untag actions, rename actions, folder deletion that moves notes back to All notes, and tag deletion that removes a tag from notes without deleting notes.
- Editor screen with auto-persisted title, folder, tag, reminder, plain/ruled/grid/dotted/Cornell/planner templates, paper color, meeting-note starter template, polished note details, mixed block changes, and capped note-level undo/redo.
- Reminder scheduling with Android alarm-backed notifications, reboot rescheduling, Android 13+ notification permission handling, notification tap-through to the note, and locked-note notification privacy.
- In-note search panel with match counts, snippets, previous/next navigation, active-match scrolling, and highlighted matched blocks.
- Rich text block controls for bold, italic, underline, text color, highlight color, size presets, font family, and alignment.
- Sticky note blocks with color palette, minimize/expand, search/export/details integration, and duplication.
- Checklist blocks with add, edit, complete all, uncheck all, clear done, item reorder, and delete item actions.
- Handwriting/drawing blocks with pen, fountain pen, highlighter, eraser, stroke width, color palette, undo, clear, vector JSON persistence, and segment-preserving erasing.
- Mixed note block reorder and duplicate controls for text, sticky, checklist, handwriting, attachment, and audio blocks.
- Attachment blocks using Android's document picker, durable app-private copies for editor-picked and shared imports, FileProvider-backed camera capture/import exposure, image previews, searchable/exportable attachment captions, PDF-aware page-count metadata when Android can read the file, file metadata, open-with-Android viewer actions, and reference-aware cleanup of unlinked app-private imports/captures.
- Audio recording blocks using `MediaRecorder`, runtime microphone permission, playback, searchable timestamp markers, and shared audio import as playable audio blocks.
- Local offline-first Room persistence with a first-run migration path from the legacy JSON note store.
- Row-level Room upserts for normal note edits to avoid rewriting the whole note table during editor changes.
- Local Notes PIN support for locked notes, with locked previews hidden from search and widgets.
- JSON backup export/import with schema/app/note-count metadata, expired-Trash import pruning, source-aware import status, deterministic imported-note merging, plus per-note plain text export/share, rich HTML export, and PDF export.
- Android share targets for incoming text, images, audio, video, and application documents, with imported audio promoted to playable audio blocks.
- Home-screen widget provider with latest-note preview, locked-note privacy, library glance status for reminders/checklists/media, latest-note open target, and quick text/checklist/sticky/handwriting/meeting actions.
- Static launcher shortcuts for quick text, checklist, sticky, handwriting, and meeting notes, plus per-note pinned Home screen shortcut requests from note actions.

## Important implementation notes

- This is a functional local prototype, not a full production Samsung Notes replacement.
- The current persistence layer uses Room for indexed note metadata, targeted single-note upserts/deletes for common edit and trash flows, and mixed note blocks as JSON payloads. A production-scale version would split pages, ink strokes, attachments, and audio timeline events into their own Room tables.
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
