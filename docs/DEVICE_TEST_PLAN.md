# Android Device Test Plan

Date: 2026-07-02

This plan is the exact checklist for the next Android-device approval gate. Do not run ADB, install, launch, or control the user's Android device until the user explicitly approves that step.

## Preconditions

- Full local verification has passed for version `0.5.27`.
- Debug APK exists at `app\build\outputs\apk\debug\app-debug.apk`.
- Package name is `com.example.snotes`.
- If the device already has app data that should be preserved, export a backup from the installed app before uninstalling, clearing data, or replacing it.
- Do not remove existing device data unless the user explicitly approves that destructive step.

## Deployment Commands To Use After Approval

Run these only after the user approves Android-device testing:

```powershell
adb devices
adb install -r app\build\outputs\apk\debug\app-debug.apk
adb shell monkey -p com.example.snotes 1
adb shell dumpsys package com.example.snotes | findstr /i "versionName versionCode"
```

If install fails because of a signature mismatch, stop and ask before uninstalling the existing package.

## Smoke Test Checklist

### Launch And Home

- App launches to the notes home screen without crash.
- List/grid toggle works.
- Search, sort, Archive, Trash, locked notes, folders, and tags are reachable.
- Empty states and contextual create actions display correctly.
- Library insight chips update for active notes, folders, tags, tasks, reminders, media, locked notes, and backup status.

### Note Creation

- Create text note.
- Create checklist note.
- Create sticky note.
- Create handwriting note.
- Create meeting note.
- Create daily journal note.
- Create study note.
- Confirm every quick-create path opens an editor and persists content after leaving and relaunching.

### Rich Text And Page Tools

- Apply bold, italic, underline, text color, highlight color, size preset, font family, and alignment.
- Change paper template to plain, ruled, grid, dotted, Cornell, and planner.
- Change paper color.
- Add page breaks and verify page count/export behavior.
- Use note details and in-note search.

### Drawing

- Draw with pen, fountain pen, and highlighter.
- Change stroke width and color.
- Erase part of a stroke.
- Undo drawing changes.
- Leave and reopen note to verify vector persistence.

### Checklists And Sticky Blocks

- Add, edit, reorder, complete, uncheck, clear done, and delete checklist items.
- Add sticky blocks, change colors, minimize/expand, duplicate, and verify search/export inclusion.

### Attachments

- Pick an image or document with Android's document picker.
- Capture a camera image through the app path if available on the device.
- Add captions.
- Open attachment through Android viewer.
- Attach a PDF and verify metadata/page count when Android can read it.
- Confirm attachment remains available after app relaunch.

### Audio

- Grant microphone permission when prompted.
- Record audio.
- Play audio.
- Add searchable markers.
- Leave and reopen note to confirm playback remains available.

### Reminders And Notifications

- Grant notification permission on Android 13+ when prompted.
- Set reminder from editor.
- Set reminder from note-card preset.
- Set batch reminder from multi-select.
- Confirm notification appears at the scheduled time.
- Tap notification and verify it opens the correct note.
- Lock a note with a reminder and verify the notification hides private content.

### Privacy

- Create or set Notes PIN.
- Lock and unlock a note.
- Verify locked content is hidden from search, cards, widget, shortcuts, notifications, and exports unless unlocked.
- Confirm batch unlock/removal paths require privacy validation.

### Widgets And Shortcuts

- Add the home-screen widget.
- Confirm latest visible note preview excludes locked, archived, and trashed notes.
- Test quick widget actions for text, checklist, sticky, handwriting, and meeting notes.
- Test static launcher shortcuts.
- Create a pinned note shortcut and open it.

### Backup And Export

- Export full Backup JSON.
- Import the exported Backup JSON into the app and confirm non-destructive merge behavior.
- Restore latest automatic backup.
- Export latest automatic backup.
- Export/share one note as TXT, HTML, PDF, and Backup JSON.
- Multi-select export selected notes as TXT, HTML, PDF, and Backup JSON.
- Confirm locked notes are skipped or privacy-gated as expected.

### Lifecycle And Persistence

- Rotate device if supported.
- Background and foreground the app.
- Force stop and relaunch.
- Reboot if the user approves reboot testing.
- Confirm notes, reminders, attachments, audio, and locked-state behavior persist.

## Proof Artifacts To Capture

- APK version output from `dumpsys package`.
- Screenshot of home screen.
- Screenshot of editor with rich text and drawing.
- Screenshot of Settings backup section.
- Screenshot of widget on launcher.
- Logcat snippet for any crash or permission failure.
- Notes about any device-specific behavior, Android version, and device model.

## Rollback Rules

- Export a Backup JSON before destructive actions.
- Do not clear data or uninstall without user approval.
- If a signature mismatch blocks install, stop before uninstalling.
- If runtime behavior fails, preserve logs, screenshots, and the installed APK path before editing the code.
