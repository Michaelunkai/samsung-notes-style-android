# Release Notes

## 0.4.1

- Added per-note pinned Home screen shortcut requests from note cards and the editor overflow menu.
- Shared attachments are copied into app-private storage so imported files survive after temporary share grants expire.
- Editor-picked attachments now use the same app-private import path instead of depending on long-lived external document grants.
- Removed app-private imported/captured files after their last note reference is permanently deleted or their block is removed.
- Added PDF-aware attachment metadata, including page counts in attachment cards, search labels, backup data, and TXT/HTML exports when Android can inspect the PDF.
- Improved large-library persistence by deleting permanently removed notes with targeted Room row deletes instead of replacing the whole note table.
- Enhanced the home-screen widget summary with reminder, checklist-progress, and media-count status while keeping locked-note body text private.

Verification for this release is local Kotlin compile, JVM unit tests, Android lint, and debug APK assembly only. Android-device testing is intentionally deferred until the explicit approval gate.

## 0.4.0

- Added Android alarm-backed note reminder notifications with notification-channel creation.
- Added reboot handling to reschedule saved future reminders after device restart.
- Added Android 13+ `POST_NOTIFICATIONS` permission handling when users set reminders.
- Added notification tap-through to open the reminded note.
- Added locked-note reminder privacy so notification titles and body text do not expose locked note content.
- Expanded the Reminders surface with reminder labels, reminder-aware sorting, card badges, quick card actions, backup persistence, widget summaries, and Room migration/schema support.
- Added rich HTML note export alongside plain text share/export and PDF export.
- Grouped editor share/export actions into a compact overflow menu to reduce toolbar crowding.
- Broadened Android share/import targets to text, image, audio, video, and application document MIME types.
- Added meeting-note launcher shortcuts and widget quick actions.

Verification for this release is local build, unit-test, manifest/resource, and debug APK assembly only. Android-device testing is intentionally deferred until the explicit approval gate.

## 0.3.0

- Added reminder metadata to notes, JSON backup/import, Room persistence, search, widgets, and note details.
- Added a dedicated Reminders navigation surface and reminder-soonest sorting.
- Added reminder presets in note metadata and quick reminder actions from note cards.
- Trimmed bottom navigation density while keeping Trash and Tags available through the filter rail.

Verification for this release is local build/test only. Android-device testing is intentionally deferred until the explicit approval gate.

## 0.2.0

- Added sticky note blocks with color, collapse, search, export, details, duplication, and quick creation support.
- Added FileProvider-backed camera capture for image attachments.
- Added audio timeline markers with persistence, search, export, details, duplication, and editor controls.
- Improved editor persistence with row-level Room upserts for normal note edits.
- Added capped note-level undo/redo for editor changes.
- Improved in-note search with active-match scrolling and highlighted matched blocks.
- Added contextual home-screen empty states for search, folders, tags, favorites, trash, and first launch.

Verification for this release is local build/test/lint only. Android-device testing is intentionally deferred until the explicit approval gate.
