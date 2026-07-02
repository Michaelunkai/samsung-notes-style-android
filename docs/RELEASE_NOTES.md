# Release Notes

## 0.5.7

- Added character counts to the note details model and details dialog alongside existing word, page, block, checklist, ink, attachment, and audio summaries.
- Added regression coverage for populated and empty character-count labels.

Verification for this release is local Kotlin compile, JVM unit tests, Android lint, and debug APK assembly only. Android-device testing is intentionally deferred until the explicit approval gate.

## 0.5.6

- Added a Share action to the multi-select toolbar so selected notes can be exported through Android's plain-text share sheet.
- Added a reusable multi-note plain-text bundle formatter with readable separators between notes.
- Preserved locked-note privacy by skipping selected locked notes unless they are unlocked in the current session.
- Added regression coverage for multi-note plain-text bundle output.

Verification for this release is local Kotlin compile, JVM unit tests, Android lint, and debug APK assembly only. Android-device testing is intentionally deferred until the explicit approval gate.

## 0.5.5

- Made notes created from the Archive surface start archived immediately so the global create action preserves the current library context.
- Added a default-note empty-state action for an empty Archive view.
- Added regression coverage for Archive contextual note creation and Archive empty-state creation affordances.

Verification for this release is local Kotlin compile, JVM unit tests, Android lint, and debug APK assembly only. Android-device testing is intentionally deferred until the explicit approval gate.

## 0.5.4

- Made title sorting and folder-sort tie-breaks use the same privacy-safe display titles shown on note cards.
- Made title search match derived display titles for untitled public notes while avoiding locked-note body-derived title leakage.
- Added regression coverage for display-title title search and display-title-aware sorting.

Verification for this release is local Kotlin compile, JVM unit tests, Android lint, and debug APK assembly only. Android-device testing is intentionally deferred until the explicit approval gate.

## 0.5.3

- Made PDF export treat page-break blocks as real logical page boundaries instead of rendering them only as text markers.
- Added regression coverage for splitting PDF export lines into page groups at page-break markers.

Verification for this release is local Kotlin compile, JVM unit tests, Android lint, and debug APK assembly only. Android-device testing is intentionally deferred until the explicit approval gate.

## 0.5.2

- Rendered the selected paper color and page template behind the editor content so ruled, grid, dotted, Cornell, and planner styles are visible while editing.
- Reused the same template drawing logic from handwriting canvases for a consistent page and ink experience.

Verification for this release is local Kotlin compile, JVM unit tests, Android lint, and debug APK assembly only. Android-device testing is intentionally deferred until the explicit approval gate.

## 0.5.1

- Added smart display titles that fall back to the first meaningful text, checklist, sticky, attachment, audio, or handwriting content when a note is still untitled.
- Applied display titles to note cards, editor chrome, widgets, reminder notifications, pinned shortcuts, share/export subjects, export filenames, TXT/HTML/PDF exports, and PDF rendering.
- Preserved locked-note privacy by avoiding body-derived display titles on locked cards, widgets, shortcuts, and unlock prompts unless the note is already open.
- Added regression coverage for title derivation, locked-note privacy, widget display titles, reminder titles, and text export fallback titles.

Verification for this release is local Kotlin compile, JVM unit tests, Android lint, and debug APK assembly only. Android-device testing is intentionally deferred until the explicit approval gate.

## 0.5.0

- Added page-break blocks to the note editor so notes can be structured into lightweight pages while keeping existing text, checklist, sticky, handwriting, attachment, and audio blocks intact.
- Added a toolbar page-break action, editor page dividers with duplicate/remove controls, card page-count metadata, and Note Details page counts.
- Preserved page breaks through JSON backup/import, Room block payloads, note duplication, plain text export, rich HTML export, and PDF-line export.
- Added regression coverage for page-break serialization, duplication, details counts, and export output.

Verification for this release is local Kotlin compile, JVM unit tests, Android lint, and debug APK assembly only. Android-device testing is intentionally deferred until the explicit approval gate.

## 0.4.9

- Kept archived notes out of the home-screen widget's latest-note preview and library status counts so Archive behaves like a hidden main-library surface.
- Added widget regression coverage proving newer archived notes do not replace the latest visible note or leak into widget copy.

Verification for this release is local Kotlin compile, JVM unit tests, Android lint, and debug APK assembly only. Android-device testing is intentionally deferred until the explicit approval gate.

## 0.4.8

- Added an Archive surface so users can keep notes out of the main library without deleting them.
- Added card-level and batch archive/unarchive actions, with archived notes unpinned automatically and excluded from folders, tags, favorites, reminders, locked-note counts, and normal search surfaces.
- Persisted archived state through JSON backup/import, Room entities, and a Room v6 migration/index.
- Added regression coverage for archived-note visibility, duplication, JSON round trips, Room mapping, selection actions, and Trash moves clearing archived state.

Verification for this release is local Kotlin compile, JVM unit tests, Android lint, and debug APK assembly only. Android-device testing is intentionally deferred until the explicit approval gate.

## 0.4.7

- Added page template and paper color metadata to plain-text, rich HTML, and PDF-line exports so exported notes retain page-style context.
- Added export tests for Planner, Cornell, and Grid page-style metadata across text, HTML, and PDF export paths.

Verification for this release is local Kotlin compile, JVM unit tests, Android lint, and debug APK assembly only. Android-device testing is intentionally deferred until the explicit approval gate.

## 0.4.6

- Hardened backup import so expired Trash notes from backups are skipped instead of being resurrected.
- Centralized imported-note merging so imported IDs replace matching local notes while unrelated local notes are preserved.
- Added unit coverage for backup import pruning and merge ordering.

Verification for this release is local Kotlin compile, JVM unit tests, Android lint, and debug APK assembly only. Android-device testing is intentionally deferred until the explicit approval gate.

## 0.4.5

- Enforced the 30-day Trash review window locally by pruning expired deleted notes during startup before reminders and widgets are refreshed.
- Kept app-private attachment/audio cleanup reference-aware when expired Trash notes are pruned.
- Added unit coverage for Trash expiry timestamps, expired-note filtering, and expired-note pruning.

Verification for this release is local Kotlin compile, JVM unit tests, Android lint, and debug APK assembly only. Android-device testing is intentionally deferred until the explicit approval gate.

## 0.4.4

- Added Trash-oriented sort modes for newest-deleted notes and review-window-ending notes.
- Added unit coverage for Trash sorting so recycle-bin ordering remains stable.

Verification for this release is local Kotlin compile, JVM unit tests, Android lint, and debug APK assembly only. Android-device testing is intentionally deferred until the explicit approval gate.

## 0.4.3

- Added Trash retention labels to deleted note cards so the recycle-bin view shows both deletion age and the remaining 30-day review window.
- Added model tests for Trash retention wording at fresh, one-day-left, and expired review-window boundaries.

Verification for this release is local Kotlin compile, JVM unit tests, Android lint, and debug APK assembly only. Android-device testing is intentionally deferred until the explicit approval gate.

## 0.4.2

- Added Cornell and planner-style handwriting page templates alongside the existing plain, ruled, grid, and dotted paper styles.
- Preserved the new page templates through note JSON backup/import, Room entity mapping, and stored default-note preferences.

Verification for this release is local Kotlin compile, JVM unit tests, Android lint, and debug APK assembly only. Android-device testing is intentionally deferred until the explicit approval gate.

## 0.4.1

- Added per-note pinned Home screen shortcut requests from note cards and the editor overflow menu.
- Shared attachments are copied into app-private storage so imported files survive after temporary share grants expire.
- Editor-picked attachments now use the same app-private import path instead of depending on long-lived external document grants.
- Removed app-private imported/captured files after their last note reference is permanently deleted or their block is removed.
- Added editable attachment captions that persist through Room/backup data and appear in note search plus TXT/HTML/PDF exports.
- Added PDF-aware attachment metadata, including page counts in attachment cards, search labels, backup data, and TXT/HTML exports when Android can inspect the PDF.
- Added Trash deletion timestamps, Trash-age labels on deleted note cards, backup persistence for deletion time, and a Room v5 migration/index for deleted-note metadata.
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
