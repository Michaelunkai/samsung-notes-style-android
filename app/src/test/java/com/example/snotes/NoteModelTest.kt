package com.example.snotes

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class NoteModelTest {
    @Test
    fun noteJsonRoundTripPreservesMixedBlocks() {
        val note = SNote(
            title = "Planning",
            folder = "Work",
            tags = listOf("meeting", "audio"),
            pinned = true,
            favorite = true,
            archived = true,
            reminderAt = 1_700_000_000_000,
            pageTemplate = PageTemplate.Cornell,
            paperColor = 0xFFEFF6FF,
            blocks = listOf(
                NoteBlock.Text(
                    text = "Discuss release",
                    bold = true,
                    italic = true,
                    underline = true,
                    color = 0xFF1D4ED8,
                    fontFamily = NoteFontFamily.Serif,
                    alignment = TextAlignment.Center
                ),
                NoteBlock.Checklist(
                    items = listOf(
                        CheckItem(text = "Ship debug build", checked = true),
                        CheckItem(text = "Collect feedback", checked = false)
                    )
                ),
                NoteBlock.Sticky(text = "Call out launch risk", color = 0xFFBBF7D0, collapsed = true),
                NoteBlock.Drawing(
                    activeTool = DrawTool.Highlighter,
                    strokes = listOf(
                        DrawStroke(
                            color = 0xFF111827,
                            width = 6f,
                            tool = DrawTool.Fountain,
                            points = listOf(DrawPoint(1f, 2f), DrawPoint(3f, 4f))
                        )
                    )
                ),
                NoteBlock.Attachment(
                    uri = "content://example/file",
                    name = "brief.pdf",
                    mimeHint = "application/pdf",
                    sizeBytes = 2048,
                    pageCount = 4,
                    caption = "Signed planning brief"
                ),
                NoteBlock.Audio(
                    path = "/recordings/one.m4a",
                    name = "one.m4a",
                    markers = listOf(AudioMarker(id = "marker", label = "Decision point", timestampMs = 12_000))
                )
            )
        )

        val restored = note.toJson().toNote()

        assertEquals(note.title, restored.title)
        assertEquals(note.folder, restored.folder)
        assertEquals(note.tags, restored.tags)
        assertTrue(restored.pinned)
        assertTrue(restored.favorite)
        assertTrue(restored.archived)
        assertEquals(1_700_000_000_000, restored.reminderAt)
        assertEquals(PageTemplate.Cornell, restored.pageTemplate)
        assertEquals(0xFFEFF6FF, restored.paperColor)
        assertEquals(6, restored.blocks.size)
        assertEquals("Discuss release", (restored.blocks[0] as NoteBlock.Text).text)
        assertTrue((restored.blocks[0] as NoteBlock.Text).bold)
        assertTrue((restored.blocks[0] as NoteBlock.Text).underline)
        assertEquals(NoteFontFamily.Serif, (restored.blocks[0] as NoteBlock.Text).fontFamily)
        assertEquals(TextAlignment.Center, (restored.blocks[0] as NoteBlock.Text).alignment)
        assertEquals("Ship debug build", (restored.blocks[1] as NoteBlock.Checklist).items[0].text)
        assertTrue((restored.blocks[1] as NoteBlock.Checklist).items[0].checked)
        assertEquals("Call out launch risk", (restored.blocks[2] as NoteBlock.Sticky).text)
        assertTrue((restored.blocks[2] as NoteBlock.Sticky).collapsed)
        assertEquals(2, (restored.blocks[3] as NoteBlock.Drawing).strokes.first().points.size)
        assertEquals(DrawTool.Highlighter, (restored.blocks[3] as NoteBlock.Drawing).activeTool)
        assertEquals(DrawTool.Fountain, (restored.blocks[3] as NoteBlock.Drawing).strokes.first().tool)
        assertEquals("brief.pdf", (restored.blocks[4] as NoteBlock.Attachment).name)
        assertEquals("2 KB", (restored.blocks[4] as NoteBlock.Attachment).sizeLabel)
        assertEquals("4 pages", (restored.blocks[4] as NoteBlock.Attachment).pageCountLabel)
        assertEquals("Signed planning brief", (restored.blocks[4] as NoteBlock.Attachment).caption)
        assertEquals("one.m4a", (restored.blocks[5] as NoteBlock.Audio).name)
        assertEquals("Decision point", (restored.blocks[5] as NoteBlock.Audio).markers.single().label)
        assertEquals(12_000, (restored.blocks[5] as NoteBlock.Audio).markers.single().timestampMs)
    }

    @Test
    fun visibleNotesApplySearchFolderTagAndDeletedFilters() {
        val state = NotesUiState(
            notes = listOf(
                SNote(title = "Meeting", folder = "Work", tags = listOf("team"), blocks = listOf(NoteBlock.Text(text = "Roadmap"))),
                SNote(title = "Groceries", folder = "Personal", tags = listOf("errand"), locked = true, blocks = listOf(NoteBlock.Checklist())),
                SNote(title = "Old", folder = "Work", tags = listOf("team"), deleted = true),
                SNote(title = "Archived", folder = "Work", tags = listOf("team"), archived = true)
            ),
            search = "road",
            folderFilter = "Work",
            tagFilter = null
        )

        assertEquals(listOf("Meeting"), state.visibleNotes.map { it.title })
        assertFalse(state.visibleNotes.any { it.deleted })

        val tagState = state.copy(search = "", folderFilter = null, tagFilter = "errand")
        assertEquals(listOf("Groceries"), tagState.visibleNotes.map { it.title })

        val lockedState = state.copy(search = "", folderFilter = null, tagFilter = null, surface = NotesSurface.Locked)
        assertEquals(listOf("Groceries"), lockedState.visibleNotes.map { it.title })
        assertEquals(1, lockedState.lockedCount)

        val archivedState = state.copy(search = "", folderFilter = null, tagFilter = null, surface = NotesSurface.Archived)
        assertEquals(listOf("Archived"), archivedState.visibleNotes.map { it.title })
        assertEquals(1, archivedState.archivedCount)
    }

    @Test
    fun emptyNotesCopyExplainsCurrentHomeSurface() {
        assertEquals("No matching notes", NotesUiState(search = "missing").emptyNotesCopy().title)
        assertEquals("Trash is empty", NotesUiState(surface = NotesSurface.Trash).emptyNotesCopy().title)
        assertEquals("No favorites yet", NotesUiState(surface = NotesSurface.Favorites).emptyNotesCopy().title)
        assertEquals("No reminders", NotesUiState(surface = NotesSurface.Reminders).emptyNotesCopy().title)
        assertEquals("No locked notes", NotesUiState(surface = NotesSurface.Locked).emptyNotesCopy().title)
        assertEquals("Archive is empty", NotesUiState(surface = NotesSurface.Archived).emptyNotesCopy().title)
        assertNull(NotesUiState(surface = NotesSurface.Locked).emptyNotesCopy().actionLabel)
        assertEquals(
            "New note",
            NotesUiState(surface = NotesSurface.Locked, notePinDigest = hashNotesPin("1234")).emptyNotesCopy().actionLabel
        )
        assertEquals(
            "No notes in Work",
            NotesUiState(surface = NotesSurface.Folders, folderFilter = "Work").emptyNotesCopy().title
        )
        assertEquals(
            "No notes tagged #project",
            NotesUiState(surface = NotesSurface.Tags, tagFilter = "project").emptyNotesCopy().title
        )
        assertEquals("New note", NotesUiState().emptyNotesCopy().actionLabel)
        assertEquals(
            "New sketch",
            NotesUiState(noteDefaults = NoteDefaults(newNoteKind = NewNoteKind.Drawing)).emptyNotesCopy().actionLabel
        )
    }

    @Test
    fun searchScopesMatchContentMetadataAndAttachments() {
        val note = SNote(
            title = "Launch plan",
            folder = "Work/Product",
            tags = listOf("release", "team"),
            blocks = listOf(
                NoteBlock.Text(text = "Discuss roadmap milestones and launch readiness"),
                NoteBlock.Checklist(items = listOf(CheckItem(text = "Collect launch assets"))),
                NoteBlock.Sticky(text = "Launch sticky reminder"),
                NoteBlock.Attachment(
                    uri = "content://example/deck",
                    name = "launch-deck.pdf",
                    mimeHint = "application/pdf",
                    caption = "Final stakeholder deck"
                ),
                NoteBlock.Audio(
                    path = "/audio/launch-briefing.m4a",
                    name = "launch-briefing.m4a",
                    markers = listOf(AudioMarker(label = "Launch decision", timestampMs = 18_000))
                )
            )
        )

        assertEquals(SearchScope.Title, note.searchMatches("launch", SearchScope.Title).single().scope)
        assertEquals(listOf("Folder: Work/Product"), note.searchMatches("product", SearchScope.Folders).map { it.label })
        assertEquals(listOf("Tag: #release"), note.searchMatches("release", SearchScope.Tags).map { it.label })
        assertTrue(note.searchMatches("roadmap", SearchScope.Content).single().label.startsWith("Text:"))
        assertTrue(note.searchMatches("assets", SearchScope.Content).single().label.startsWith("Checklist:"))
        assertTrue(note.searchMatches("sticky", SearchScope.Content).single().label.startsWith("Sticky:"))
        assertTrue(note.searchMatches("decision", SearchScope.Content).single().label.startsWith("Audio marker:"))
        assertEquals(
            listOf("PDF: launch-deck.pdf", "Audio: launch-briefing.m4a"),
            note.searchMatches("launch", SearchScope.Attachments).map { it.label }
        )
        assertEquals(
            listOf("PDF caption: Final stakeholder deck"),
            note.searchMatches("stakeholder", SearchScope.Attachments).map { it.label }
        )

        val locked = note.copy(locked = true)
        assertTrue(locked.searchMatches("roadmap", SearchScope.Content).isEmpty())
        assertTrue(locked.searchMatches("launch-deck", SearchScope.Attachments).isEmpty())
        assertEquals(SearchScope.Title, locked.searchMatches("Launch", SearchScope.Title).single().scope)
    }

    @Test
    fun editorSearchMatchesCurrentNoteContentInReadingOrder() {
        val note = SNote(
            title = "Launch plan",
            folder = "Work/Launch",
            tags = listOf("launch", "team"),
            blocks = listOf(
                NoteBlock.Text(id = "text", text = "Discuss launch readiness"),
                NoteBlock.Checklist(
                    id = "checklist",
                    items = listOf(
                        CheckItem(text = "Collect launch assets", checked = false),
                        CheckItem(text = "Approve budget", checked = true)
                    )
                ),
                NoteBlock.Sticky(id = "sticky", text = "Launch sticky reminder"),
                NoteBlock.Attachment(id = "file", uri = "content://example/file", name = "launch-deck.pdf", caption = "Stakeholder launch deck"),
                NoteBlock.Audio(
                    id = "audio",
                    path = "/audio/launch-briefing.m4a",
                    name = "launch-briefing.m4a",
                    markers = listOf(AudioMarker(label = "Launch decision", timestampMs = 18_000))
                )
            )
        )

        val matches = note.editorSearchMatches("launch")

        assertEquals(
            listOf(null, null, null, "text", "checklist", "sticky", "file", "audio", "audio"),
            matches.map { it.blockId }
        )
        assertEquals("Title", matches[0].label)
        assertEquals("Folder", matches[1].label)
        assertEquals("Tag", matches[2].label)
        assertEquals("Checklist item (open)", matches[4].label)
        assertEquals("Sticky note", matches[5].label)
        assertEquals("PDF attachment", matches[6].label)
        assertEquals("Audio marker 0:18", matches[8].label)
        assertTrue(matches.all { it.snippet.contains("launch", ignoreCase = true) })
        assertTrue(note.editorSearchMatches("   ").isEmpty())
    }

    @Test
    fun editorSearchTargetItemIndexAccountsForMetaSearchPanelAndBlockMatches() {
        val blocks = listOf(
            NoteBlock.Text(id = "text", text = "Body"),
            NoteBlock.Checklist(id = "list")
        )

        assertEquals(0, editorSearchTargetItemIndex(blocks, EditorSearchMatch(null, "Title", "Body"), searchPanelVisible = true))
        assertEquals(2, editorSearchTargetItemIndex(blocks, EditorSearchMatch("text", "Text block", "Body"), searchPanelVisible = true))
        assertEquals(2, editorSearchTargetItemIndex(blocks, EditorSearchMatch("list", "Checklist", "Body"), searchPanelVisible = false))
        assertEquals(0, editorSearchTargetItemIndex(blocks, EditorSearchMatch("missing", "Missing", "Body"), searchPanelVisible = true))
    }

    @Test
    fun newNoteKindsUseConfiguredDefaults() {
        val defaults = NoteDefaults(pageTemplate = PageTemplate.Dotted, paperColor = 0xFFEFF6FF)

        val text = NewNoteKind.Text.createNoteWithDefaults(defaults)
        val checklist = NewNoteKind.Checklist.createNoteWithDefaults(defaults)
        val sticky = NewNoteKind.Sticky.createNoteWithDefaults(defaults)
        val drawing = NewNoteKind.Drawing.createNoteWithDefaults(defaults)
        val meeting = NewNoteKind.Meeting.createNoteWithDefaults(defaults)

        assertEquals(PageTemplate.Dotted, text.pageTemplate)
        assertEquals(0xFFEFF6FF, text.paperColor)
        assertTrue(text.blocks.single() is NoteBlock.Text)
        assertTrue(checklist.blocks.single() is NoteBlock.Checklist)
        assertTrue(sticky.blocks.single() is NoteBlock.Sticky)
        assertTrue(drawing.blocks.single() is NoteBlock.Drawing)
        assertEquals("Meeting note", meeting.title)
        assertEquals(3, meeting.blocks.size)
        assertTrue(meeting.blocks[1] is NoteBlock.Checklist)
        assertEquals(PageTemplate.Dotted, checklist.pageTemplate)
        assertEquals(PageTemplate.Dotted, sticky.pageTemplate)
        assertEquals(0xFFEFF6FF, drawing.paperColor)
        assertEquals(PageTemplate.Dotted, meeting.pageTemplate)
    }

    @Test
    fun newNotesInheritActiveFolderOrTagContext() {
        val folderState = NotesUiState(
            surface = NotesSurface.Folders,
            folderFilter = "Work/Product",
            noteDefaults = NoteDefaults(pageTemplate = PageTemplate.Grid)
        )
        val tagState = NotesUiState(surface = NotesSurface.Tags, tagFilter = "launch")
        val reminderState = NotesUiState(surface = NotesSurface.Reminders)
        val lockedState = NotesUiState(surface = NotesSurface.Locked, notePinDigest = hashNotesPin("1234"))
        val unlockedState = NotesUiState(surface = NotesSurface.Locked)

        val folderNote = NewNoteKind.Text.createNoteForState(folderState)
        val tagNote = NewNoteKind.Checklist.createNoteForState(tagState)
        val reminderNote = NewNoteKind.Text.createNoteForState(reminderState)
        val meetingNote = NewNoteKind.Meeting.createNoteForState(folderState)
        val lockedNote = NewNoteKind.Sticky.createNoteForState(lockedState)
        val unlockedNote = NewNoteKind.Text.createNoteForState(unlockedState)

        assertEquals("Work/Product", folderNote.folder)
        assertEquals(PageTemplate.Grid, folderNote.pageTemplate)
        assertEquals(listOf("launch"), tagNote.tags)
        assertTrue(tagNote.blocks.single() is NoteBlock.Checklist)
        assertTrue(reminderNote.reminderAt != null)
        assertEquals("Work/Product", meetingNote.folder)
        assertTrue(meetingNote.blocks.any { it is NoteBlock.Checklist })
        assertTrue(lockedNote.locked)
        assertFalse(unlockedNote.locked)
    }

    @Test
    fun storedNoteDefaultsFallbackToSupportedValues() {
        val restored = noteDefaultsFromStoredValues("Meeting", "Cornell", 0xFFFFF8D6)
        val caseInsensitive = noteDefaultsFromStoredValues("Text", "planner", DEFAULT_PAPER_COLORS.first())
        val fallback = noteDefaultsFromStoredValues("LegacyKind", "LegacyTemplate", 0xFF123456)

        assertEquals(NewNoteKind.Meeting, restored.newNoteKind)
        assertEquals(PageTemplate.Cornell, restored.pageTemplate)
        assertEquals(0xFFFFF8D6, restored.paperColor)
        assertEquals(PageTemplate.Planner, caseInsensitive.pageTemplate)
        assertEquals(NewNoteKind.Text, fallback.newNoteKind)
        assertEquals(PageTemplate.Plain, fallback.pageTemplate)
        assertEquals(DEFAULT_PAPER_COLORS.first(), fallback.paperColor)
    }

    @Test
    fun storedLibraryPreferencesFallbackToSupportedValues() {
        assertEquals(NoteSortMode.MediaHeavy, sortModeFromStoredValue("MediaHeavy"))
        assertEquals(NoteSortMode.ChecklistProgress, sortModeFromStoredValue("checklistprogress"))
        assertEquals(NoteSortMode.ReminderSoonest, sortModeFromStoredValue("remindersoonest"))
        assertEquals(NoteSortMode.ModifiedNewest, sortModeFromStoredValue("LegacySort"))
        assertEquals(NoteViewMode.Grid, viewModeFromStoredValue("Grid"))
        assertEquals(NoteViewMode.List, viewModeFromStoredValue("LegacyView"))
        assertEquals(SearchScope.Attachments, searchScopeFromStoredValue("attachments"))
        assertEquals(SearchScope.All, searchScopeFromStoredValue("LegacyScope"))
    }

    @Test
    fun librarySummaryShowsSearchScopeOnlyWhileSearching() {
        val idle = NotesUiState(notes = emptyList())
        val searching = idle.copy(search = "release", searchScope = SearchScope.Tags)

        assertEquals("0 notes • Modified newest • List", idle.librarySummaryLabel)
        assertEquals("0 notes • Tags • Modified newest • List", searching.librarySummaryLabel)
    }

    @Test
    fun notesPinHelpersValidateAndVerifyDigest() {
        val digest = hashNotesPin("1234")

        assertTrue(isUsableNotesPin("1234"))
        assertTrue(isUsableNotesPin("123456789012"))
        assertFalse(isUsableNotesPin("123"))
        assertFalse(isUsableNotesPin("1234567890123"))
        assertFalse(isUsableNotesPin("12ab"))
        assertTrue(verifyNotesPin("1234", digest))
        assertFalse(verifyNotesPin("0000", digest))
        assertFalse(verifyNotesPin("1234", null))
        assertTrue(digest != "1234")
    }

    @Test
    fun uiStateTracksNotesPinAndSessionUnlocks() {
        val locked = SNote(id = "locked", title = "Private", locked = true)
        val state = NotesUiState(
            notes = listOf(locked),
            selectedNoteId = "locked",
            notePinDigest = hashNotesPin("5555"),
            unlockedNoteIds = setOf("locked")
        )

        assertTrue(state.hasNotePin)
        assertEquals(locked, state.selectedNote)
        assertTrue(locked.id in state.unlockedNoteIds)
    }

    @Test
    fun selectionStateTracksAvailablePinFavoriteAndLockActions() {
        val pinnedFavorite = SNote(id = "pinned", pinned = true, favorite = true, locked = true, archived = true)
        val normal = SNote(id = "normal")
        val state = NotesUiState(
            notes = listOf(pinnedFavorite, normal),
            selectedNoteIds = setOf("pinned", "normal")
        )

        assertTrue(state.selectedNotesIncludePinned)
        assertTrue(state.selectedNotesIncludeUnpinned)
        assertTrue(state.selectedNotesIncludeFavorite)
        assertTrue(state.selectedNotesIncludeNonFavorite)
        assertTrue(state.selectedNotesIncludeLocked)
        assertTrue(state.selectedNotesIncludeUnlocked)
        assertTrue(state.selectedNotesIncludeArchived)
        assertTrue(state.selectedNotesIncludeUnarchived)

        val pinnedOnly = state.copy(selectedNoteIds = setOf("pinned"))
        assertFalse(pinnedOnly.selectedNotesIncludeUnpinned)
        assertFalse(pinnedOnly.selectedNotesIncludeNonFavorite)
        assertFalse(pinnedOnly.selectedNotesIncludeUnlocked)
        assertFalse(pinnedOnly.selectedNotesIncludeUnarchived)
    }

    @Test
    fun uiStateKnowsWhenAllVisibleNotesAreSelected() {
        val work = SNote(id = "work", folder = "Work")
        val home = SNote(id = "home", folder = "Home")
        val state = NotesUiState(
            notes = listOf(work, home),
            folderFilter = "Work",
            selectedNoteIds = setOf("work")
        )

        assertEquals(listOf("work"), state.visibleNotes.map { it.id })
        assertTrue(state.allVisibleNotesSelected)
        assertFalse(state.copy(selectedNoteIds = setOf("home")).allVisibleNotesSelected)
    }

    @Test
    fun launchRequestParserRoutesSharedTextAndQuickNotes() {
        val shared = noteLaunchRequestFrom(
            action = "android.intent.action.SEND",
            mimeType = "text/plain",
            sharedText = "Shared meeting note",
            quickKindName = null
        )
        val quickDraw = noteLaunchRequestFrom(
            action = ACTION_QUICK_NOTE,
            mimeType = null,
            sharedText = null,
            quickKindName = "Drawing"
        )
        val quickSticky = noteLaunchRequestFrom(
            action = ACTION_QUICK_NOTE,
            mimeType = null,
            sharedText = null,
            quickKindName = "Sticky"
        )
        val quickMeeting = noteLaunchRequestFrom(
            action = ACTION_QUICK_NOTE,
            mimeType = null,
            sharedText = null,
            quickKindName = "Meeting"
        )
        val openNote = noteLaunchRequestFrom(
            action = null,
            mimeType = null,
            sharedText = null,
            quickKindName = null,
            openNoteId = "note-42"
        )
        val invalid = noteLaunchRequestFrom(
            action = ACTION_QUICK_NOTE,
            mimeType = null,
            sharedText = null,
            quickKindName = "Unknown"
        )

        assertEquals("Shared meeting note", shared.sharedText)
        assertEquals(NewNoteKind.Drawing, quickDraw.quickNoteKind)
        assertEquals(NewNoteKind.Sticky, quickSticky.quickNoteKind)
        assertEquals(NewNoteKind.Meeting, quickMeeting.quickNoteKind)
        assertEquals("note-42", openNote.openNoteId)
        assertEquals(null, invalid.quickNoteKind)
    }

    @Test
    fun launchRequestParserRoutesSharedAttachments() {
        val shared = noteLaunchRequestFrom(
            action = "android.intent.action.SEND_MULTIPLE",
            mimeType = "application/pdf",
            sharedText = null,
            quickKindName = null,
            sharedAttachments = listOf(
                SharedAttachmentRequest("content://example/file-1", "application/pdf"),
                SharedAttachmentRequest("content://example/file-1", "application/pdf"),
                SharedAttachmentRequest("content://example/file-2", "application/pdf"),
                SharedAttachmentRequest("", "application/pdf")
            )
        )
        val ignored = noteLaunchRequestFrom(
            action = ACTION_QUICK_NOTE,
            mimeType = "application/pdf",
            sharedText = null,
            quickKindName = null,
            sharedAttachments = listOf(SharedAttachmentRequest("content://example/file-3", "application/pdf"))
        )

        assertEquals(listOf("content://example/file-1", "content://example/file-2"), shared.sharedAttachments.map { it.uri })
        assertEquals("application/pdf", shared.sharedAttachments.first().mimeHint)
        assertTrue(ignored.sharedAttachments.isEmpty())
    }

    @Test
    fun pinnedNoteShortcutHelpersCreateStableLauncherLabels() {
        assertEquals("pinned_note_note-42", pinnedNoteShortcutId("note-42"))
        assertEquals("Untitled note", "   ".shortcutLabel(18))
        assertEquals("Launch plan", " Launch   plan ".shortcutLabel(18))
        assertEquals("Very long no...", "Very long note title for launcher".shortcutLabel(15))
    }

    @Test
    fun duplicateNoteCreatesIndependentCopyWithFreshMetadata() {
        val original = SNote(
            id = "source",
            title = "Design brief",
            folder = "Work",
            tags = listOf("copy"),
            pinned = true,
            favorite = true,
            archived = true,
            deleted = true,
            deletedAt = 12,
            createdAt = 1,
            updatedAt = 2,
            blocks = listOf(
                NoteBlock.Text(id = "text", text = "Body"),
                NoteBlock.Checklist(
                    id = "checklist",
                    items = listOf(CheckItem(id = "item", text = "Task", checked = true))
                ),
                NoteBlock.Drawing(
                    id = "drawing",
                    strokes = listOf(DrawStroke(id = "stroke", color = 0xFF111111, width = 4f, points = listOf(DrawPoint(1f, 2f))))
                ),
                NoteBlock.Audio(
                    id = "audio",
                    path = "/audio/brief.m4a",
                    name = "brief.m4a",
                    markers = listOf(AudioMarker(id = "marker", label = "Decision", timestampMs = 3_000))
                )
            )
        )

        val duplicate = original.duplicate(now = 42)

        assertEquals("Copy of Design brief", duplicate.title)
        assertEquals("Work", duplicate.folder)
        assertEquals(listOf("copy"), duplicate.tags)
        assertFalse(duplicate.pinned)
        assertFalse(duplicate.favorite)
        assertFalse(duplicate.archived)
        assertFalse(duplicate.deleted)
        assertNull(duplicate.deletedAt)
        assertEquals(42, duplicate.createdAt)
        assertEquals(42, duplicate.updatedAt)
        assertTrue(duplicate.id != original.id)
        assertTrue(duplicate.blocks[0].id != original.blocks[0].id)
        assertTrue((duplicate.blocks[1] as NoteBlock.Checklist).items.single().id != "item")
        assertTrue((duplicate.blocks[2] as NoteBlock.Drawing).strokes.single().id != "stroke")
        assertTrue((duplicate.blocks[3] as NoteBlock.Audio).markers.single().id != "marker")
    }

    @Test
    fun noteHistoryHelpersCompareEditableContentAndAvailableStacks() {
        val note = SNote(id = "note", title = "Original", updatedAt = 1)
        val timestampOnly = note.copy(updatedAt = 2)
        val edited = note.copy(title = "Edited")
        val stacks = mapOf(
            "note" to ArrayDeque(listOf(note)),
            "empty" to ArrayDeque<SNote>()
        )

        assertTrue(note.editableContentEquals(timestampOnly))
        assertFalse(note.editableContentEquals(edited))
        assertEquals(setOf("note"), stacks.availableNoteIds())
        assertEquals(note, stacks.getValue("note").popLastOrNull())
        assertEquals(null, stacks.getValue("note").popLastOrNull())
    }

    @Test
    fun noteBlocksCanMoveWithinMixedContentNote() {
        val note = SNote(
            blocks = listOf(
                NoteBlock.Text(id = "text", text = "Body"),
                NoteBlock.Checklist(id = "list"),
                NoteBlock.Drawing(id = "drawing")
            )
        )

        val movedUp = note.moveBlock("drawing", -1)
        val movedDown = movedUp.moveBlock("drawing", 1)

        assertEquals(listOf("text", "drawing", "list"), movedUp.blocks.map { it.id })
        assertEquals(listOf("text", "list", "drawing"), movedDown.blocks.map { it.id })
        assertEquals(note.blocks.map { it.id }, note.moveBlock("text", -1).blocks.map { it.id })
        assertEquals(note.blocks.map { it.id }, note.moveBlock("missing", 1).blocks.map { it.id })
    }

    @Test
    fun noteBlocksCanDuplicateAfterSourceWithFreshNestedIds() {
        val note = SNote(
            blocks = listOf(
                NoteBlock.Text(id = "text", text = "Body"),
                NoteBlock.Checklist(
                    id = "list",
                    items = listOf(CheckItem(id = "item", text = "Task"))
                ),
                NoteBlock.Sticky(id = "sticky", text = "Note", collapsed = true),
                NoteBlock.Drawing(
                    id = "drawing",
                    strokes = listOf(DrawStroke(id = "stroke", color = 0xFF111111, width = 4f, points = listOf(DrawPoint(1f, 1f))))
                ),
                NoteBlock.Audio(
                    id = "audio",
                    path = "/audio/block.m4a",
                    name = "block.m4a",
                    markers = listOf(AudioMarker(id = "marker", label = "Question", timestampMs = 7_000))
                )
            )
        )

        val withChecklistCopy = note.duplicateBlockAfter("list")
        val checklistCopy = withChecklistCopy.blocks[2] as NoteBlock.Checklist
        val withStickyCopy = note.duplicateBlockAfter("sticky")
        val stickyCopy = withStickyCopy.blocks[3] as NoteBlock.Sticky
        val withDrawingCopy = note.duplicateBlockAfter("drawing")
        val drawingCopy = withDrawingCopy.blocks[4] as NoteBlock.Drawing
        val withAudioCopy = note.duplicateBlockAfter("audio")
        val audioCopy = withAudioCopy.blocks[5] as NoteBlock.Audio

        assertEquals(listOf("text", "list", checklistCopy.id, "sticky", "drawing", "audio"), withChecklistCopy.blocks.map { it.id })
        assertTrue(checklistCopy.id != "list")
        assertEquals("Task", checklistCopy.items.single().text)
        assertTrue(checklistCopy.items.single().id != "item")
        assertEquals(listOf("text", "list", "sticky", stickyCopy.id, "drawing", "audio"), withStickyCopy.blocks.map { it.id })
        assertEquals("Note", stickyCopy.text)
        assertTrue(stickyCopy.collapsed)
        assertTrue(stickyCopy.id != "sticky")
        assertTrue(drawingCopy.id != "drawing")
        assertTrue(drawingCopy.strokes.single().id != "stroke")
        assertTrue(audioCopy.id != "audio")
        assertEquals("Question", audioCopy.markers.single().label)
        assertTrue(audioCopy.markers.single().id != "marker")
        assertEquals(note.blocks.map { it.id }, note.duplicateBlockAfter("missing").blocks.map { it.id })
    }

    @Test
    fun noteListCanDuplicateSelectedNotesAtTopWithFreshNestedIds() {
        val notes = listOf(
            SNote(
                id = "first",
                title = "Template",
                blocks = listOf(NoteBlock.Checklist(id = "list", items = listOf(CheckItem(id = "item", text = "Task"))))
            ),
            SNote(id = "second", title = "Keep")
        )

        val updated = notes.duplicateByIds(setOf("first"), now = 100)
        val duplicate = updated.first()

        assertEquals(listOf(duplicate.id, "first", "second"), updated.map { it.id })
        assertEquals("Copy of Template", duplicate.title)
        assertEquals(100, duplicate.createdAt)
        assertEquals(100, duplicate.updatedAt)
        assertTrue(duplicate.id != "first")
        assertTrue((duplicate.blocks.single() as NoteBlock.Checklist).items.single().id != "item")
        assertEquals(notes, notes.duplicateByIds(emptySet(), now = 100))
    }

    @Test
    fun notePlainTextExportIncludesMixedContent() {
        val note = SNote(
            title = "Sprint review",
            folder = "Work",
            tags = listOf("team", "export"),
            pageTemplate = PageTemplate.Planner,
            paperColor = 0xFFFFF8D6,
            blocks = listOf(
                NoteBlock.Text(text = "Release summary"),
                NoteBlock.Checklist(
                    items = listOf(
                        CheckItem(text = "Demo", checked = true),
                        CheckItem(text = "Follow up", checked = false)
                    )
                ),
                NoteBlock.Sticky(text = "Remember stakeholder questions"),
                NoteBlock.Drawing(strokes = listOf(DrawStroke(color = 0xFF111111, width = 4f, points = listOf(DrawPoint(1f, 1f))))),
                NoteBlock.Attachment(
                    uri = "content://example/file",
                    name = "brief.pdf",
                    mimeHint = "application/pdf",
                    sizeBytes = 2048,
                    pageCount = 3,
                    caption = "Board review packet"
                ),
                NoteBlock.Audio(
                    path = "/audio/review.m4a",
                    name = "review.m4a",
                    durationHintMs = 65_000,
                    markers = listOf(AudioMarker(label = "Stakeholder question", timestampMs = 12_000))
                )
            )
        )

        val text = note.toPlainText()

        assertTrue(text.contains("Sprint review"))
        assertTrue(text.contains("Folder: Work"))
        assertTrue(text.contains("Page style: Planner, paper #FFF8D6"))
        assertTrue(text.contains("Tags: #team, #export"))
        assertTrue(text.contains("Release summary"))
        assertTrue(text.contains("- [x] Demo"))
        assertTrue(text.contains("- [ ] Follow up"))
        assertTrue(text.contains("[Sticky note] Remember stakeholder questions"))
        assertTrue(text.contains("[Handwriting: 1 stroke]"))
        assertTrue(text.contains("[Attachment: brief.pdf, 3 pages, 2 KB]"))
        assertTrue(text.contains("Caption: Board review packet"))
        assertTrue(text.contains("[Audio: review.m4a, 1:05]"))
        assertTrue(text.contains("- 0:12 Stakeholder question"))
    }

    @Test
    fun noteHtmlExportPreservesRichStructureAndEscapesContent() {
        val note = SNote(
            title = "Sprint <review>",
            folder = "Work & Planning",
            tags = listOf("team", "export"),
            pageTemplate = PageTemplate.Cornell,
            paperColor = 0xFFFFFBF0,
            reminderAt = 1_704_067_200_000,
            blocks = listOf(
                NoteBlock.Text(
                    text = "Release <summary>",
                    bold = true,
                    italic = true,
                    underline = true,
                    color = 0xFF123456,
                    highlight = 0xFFFFEE99,
                    sizeSp = 22,
                    fontFamily = NoteFontFamily.Serif,
                    alignment = TextAlignment.Center
                ),
                NoteBlock.Checklist(
                    items = listOf(
                        CheckItem(text = "Demo & ship", checked = true),
                        CheckItem(text = "Follow up", checked = false)
                    )
                ),
                NoteBlock.Sticky(text = "Remember stakeholders", color = 0xFFFFF59D),
                NoteBlock.Attachment(
                    uri = "content://example/file",
                    name = "brief.pdf",
                    mimeHint = "application/pdf",
                    sizeBytes = 2048,
                    pageCount = 3,
                    caption = "Board <packet>"
                ),
                NoteBlock.Audio(
                    path = "/audio/review.m4a",
                    name = "review.m4a",
                    durationHintMs = 65_000,
                    markers = listOf(AudioMarker(label = "Question <risk>", timestampMs = 12_000))
                )
            )
        )

        val html = note.toHtmlDocument()

        assertTrue(html.contains("<!doctype html>"))
        assertTrue(html.contains("Sprint &lt;review&gt;"))
        assertTrue(html.contains("Folder: Work &amp; Planning"))
        assertTrue(html.contains("Page style: Cornell, paper #FFFBF0"))
        assertTrue(html.contains("#team"))
        assertTrue(html.contains("Overdue"))
        assertTrue(html.contains("Release &lt;summary&gt;"))
        assertTrue(html.contains("font-weight: 700"))
        assertTrue(html.contains("font-style: italic"))
        assertTrue(html.contains("text-decoration: underline"))
        assertTrue(html.contains("color: #123456"))
        assertTrue(html.contains("background: #FFEE99"))
        assertTrue(html.contains("font-family: Georgia, serif"))
        assertTrue(html.contains("text-align: center"))
        assertTrue(html.contains("☑ Demo &amp; ship"))
        assertTrue(html.contains("☐ Follow up"))
        assertTrue(html.contains("Remember stakeholders"))
        assertTrue(html.contains("Attachment: brief.pdf (3 pages, 2 KB)"))
        assertTrue(html.contains("Board &lt;packet&gt;"))
        assertTrue(html.contains("Audio: review.m4a (1:05)"))
        assertTrue(html.contains("0:12 Question &lt;risk&gt;"))
    }

    @Test
    fun reminderNotificationCopyHidesLockedNoteContent() {
        val unlocked = SNote(title = "Dentist appointment")
        val locked = SNote(title = "Private plan", locked = true)

        assertEquals("Dentist appointment", unlocked.reminderNotificationTitle())
        assertEquals("Tap to open this note", unlocked.reminderNotificationText())
        assertEquals("Locked note reminder", locked.reminderNotificationTitle())
        assertEquals("Unlock the note to view its contents", locked.reminderNotificationText())
    }

    @Test
    fun noteDetailsSummarizeMixedContentCounts() {
        val note = SNote(
            blocks = listOf(
                NoteBlock.Text(text = "alpha beta gamma"),
                NoteBlock.Checklist(
                    items = listOf(
                        CheckItem(text = "Done", checked = true),
                        CheckItem(text = "Later", checked = false)
                    )
                ),
                NoteBlock.Sticky(text = "alpha sticky note"),
                NoteBlock.Drawing(strokes = listOf(DrawStroke(color = 0xFF111111, width = 4f, points = listOf(DrawPoint(1f, 1f))))),
                NoteBlock.Attachment(uri = "content://example/file", name = "brief.pdf", sizeBytes = 2048),
                NoteBlock.Audio(
                    path = "/audio/review.m4a",
                    name = "review.m4a",
                    markers = listOf(AudioMarker(label = "Intro", timestampMs = 5_000))
                )
            )
        )

        val details = note.details()

        assertEquals(6, details.blockCount)
        assertEquals(6, details.wordCount)
        assertEquals(2, details.checklistItems)
        assertEquals(1, details.completedChecklistItems)
        assertEquals(1, details.stickyNotes)
        assertEquals(1, details.drawingStrokes)
        assertEquals(1, details.attachments)
        assertEquals(1, details.audioBlocks)
        assertEquals(1, details.audioMarkers)
        assertEquals("6 blocks", details.blockLabel)
        assertEquals("6 words", details.wordLabel)
        assertEquals("1/2 done", details.checklistLabel)
        assertEquals("1 stroke", details.inkLabel)
        assertEquals("1 file", details.attachmentLabel)
        assertEquals("1 recording • 1 marker", details.audioLabel)
    }

    @Test
    fun noteDetailsLabelsHandleEmptyContent() {
        val details = SNote(blocks = listOf(NoteBlock.Text(text = ""))).details()

        assertEquals("1 block", details.blockLabel)
        assertEquals("0 words", details.wordLabel)
        assertEquals("No checklist items", details.checklistLabel)
        assertEquals("No ink strokes", details.inkLabel)
        assertEquals("No attachments", details.attachmentLabel)
        assertEquals("No audio", details.audioLabel)
    }

    @Test
    fun noteCardMetaLabelIncludesModifiedTimeBlocksAndFolder() {
        val note = SNote(
            folder = "Work",
            updatedAt = 1_704_067_200_000,
            blocks = listOf(NoteBlock.Text(text = "Body"), NoteBlock.Checklist())
        )

        val label = note.cardMetaLabel()

        assertTrue(label.contains("2024") || label.contains("24"))
        assertTrue(label.contains("2 blocks"))
        assertTrue(label.endsWith("Work"))
    }

    @Test
    fun checklistProgressLabelSummarizesVisibleTasks() {
        val note = SNote(
            blocks = listOf(
                NoteBlock.Text(text = "Body"),
                NoteBlock.Checklist(
                    items = listOf(
                        CheckItem(text = "Done", checked = true),
                        CheckItem(text = "Later", checked = false)
                    )
                ),
                NoteBlock.Checklist(items = listOf(CheckItem(text = "Also done", checked = true)))
            )
        )

        assertEquals("2/3 tasks done", note.checklistProgressLabel())
        assertNull(SNote(blocks = listOf(NoteBlock.Text(text = "No tasks"))).checklistProgressLabel())
    }

    @Test
    fun mediaCardLabelSummarizesFilesAndAudio() {
        val mixed = SNote(
            blocks = listOf(
                NoteBlock.Attachment(uri = "content://example/file", name = "brief.pdf"),
                NoteBlock.Attachment(uri = "content://example/image", name = "photo.jpg"),
                NoteBlock.Audio(path = "/audio/one.m4a", name = "one.m4a")
            )
        )
        val audioOnly = SNote(blocks = listOf(NoteBlock.Audio(path = "/audio/one.m4a", name = "one.m4a")))

        assertEquals("2 files • 1 audio", mixed.mediaCardLabel())
        assertEquals("1 audio", audioOnly.mediaCardLabel())
        assertNull(SNote(blocks = listOf(NoteBlock.Text(text = "No media"))).mediaCardLabel())
    }

    @Test
    fun noteExportFileNamesAreSanitized() {
        assertEquals("Work-Plan-Q3-", "Work/Plan:Q3?".sanitizeFileName())
        assertEquals("Untitled note", "   ".sanitizeFileName())
        assertEquals(80, "a".repeat(120).sanitizeFileName().length)
    }

    @Test
    fun textAlignmentParserFallsBackForLegacyBlocks() {
        assertEquals(TextAlignment.End, "End".toTextAlignment(TextAlignment.Start))
        assertEquals(TextAlignment.Start, "legacy".toTextAlignment(TextAlignment.Start))
        assertEquals(NoteFontFamily.Mono, "Mono".toNoteFontFamily(NoteFontFamily.Default))
        assertEquals(NoteFontFamily.Default, "legacy".toNoteFontFamily(NoteFontFamily.Default))
    }

    @Test
    fun pdfLineWrappingPreservesWordsWithinLineLimit() {
        assertEquals(listOf("alpha beta", "gamma"), "alpha beta gamma".wrapLine(10))
        assertEquals(listOf("short"), "short".wrapLine(10))
        assertEquals(listOf("alpha beta gamma"), "alpha beta gamma".wrapLine(8))
    }

    @Test
    fun notePdfLinesPrepareExportTextForPdfPages() {
        val note = SNote(
            title = "PDF plan",
            folder = "Work",
            tags = listOf("export"),
            pageTemplate = PageTemplate.Grid,
            paperColor = 0xFFEFF6FF,
            blocks = listOf(
                NoteBlock.Text(text = "alpha beta gamma delta epsilon"),
                NoteBlock.Checklist(items = listOf(CheckItem(text = "send file", checked = true)))
            )
        )

        val lines = note.toPdfLines(maxLineLength = 14)

        assertEquals("PDF plan", lines.first())
        assertTrue(lines.contains("Page style:"))
        assertTrue(lines.contains("Grid, paper"))
        assertTrue(lines.contains("#EFF6FF"))
        assertTrue(lines.contains("Tags: #export"))
        assertTrue(lines.contains("alpha beta"))
        assertTrue(lines.contains("gamma delta"))
        assertTrue(lines.contains("epsilon"))
        assertTrue(lines.contains("- [x] send"))
        assertTrue(lines.contains("file"))
        assertTrue(lines.filter { it.isNotBlank() && !it.contains("#") }.all { it.length <= 14 })
    }

    @Test
    fun folderAndTagHelpersNormalizeOrganizationInput() {
        assertEquals("All notes", normalizeFolder("   "))
        assertEquals("Work/Product", normalizeFolder(" /Work//Product/ "))
        assertEquals(listOf("work", "meeting"), parseTagInput(" #work, meeting, work, "))
        assertEquals(listOf("existing", "work", "meeting"), mergeTags(listOf("existing", "work"), "#work, meeting"))
        assertEquals(listOf("existing"), removeTags(listOf("existing", "work", "meeting"), "#work, meeting"))
        assertEquals(listOf("existing"), removeTags(listOf("existing"), " "))
    }

    @Test
    fun folderAndTagRenameHelpersUpdateMatchingOrganizationValues() {
        assertEquals("Archive", renameFolderPath("Work", "Work", "Archive"))
        assertEquals("Archive/Product", renameFolderPath("Work/Product", "Work", "Archive"))
        assertEquals("Homework", renameFolderPath("Homework", "Work", "Archive"))
        assertEquals(listOf("personal", "ideas"), renameTagList(listOf("work", "ideas", "personal"), "work", "personal"))
        assertEquals(listOf("work", "ideas"), renameTagList(listOf("work", "ideas"), "work", "   "))
    }

    @Test
    fun removeFolderFromNotesMovesFolderContentsWithoutDeletingNotes() {
        val notes = listOf(
            SNote(id = "root", folder = "Work"),
            SNote(id = "child", folder = "Work/Product"),
            SNote(id = "other", folder = "Homework"),
            SNote(id = "deleted", folder = "Work", deleted = true)
        )

        val updated = notes.removeFolderFromNotes("Work")

        assertEquals(listOf("root", "child", "other", "deleted"), updated.map { it.id })
        assertEquals("All notes", updated.first { it.id == "root" }.folder)
        assertEquals("All notes", updated.first { it.id == "child" }.folder)
        assertEquals("Homework", updated.first { it.id == "other" }.folder)
        assertEquals("All notes", updated.first { it.id == "deleted" }.folder)
        assertTrue(updated.first { it.id == "deleted" }.deleted)
    }

    @Test
    fun removeTagFromNotesDeletesTagWithoutDeletingNotes() {
        val notes = listOf(
            SNote(id = "one", tags = listOf("work", "launch")),
            SNote(id = "two", tags = listOf("personal")),
            SNote(id = "three", tags = listOf("work"), deleted = true)
        )

        val updated = notes.removeTagFromNotes("#work")

        assertEquals(listOf("one", "two", "three"), updated.map { it.id })
        assertEquals(listOf("launch"), updated.first { it.id == "one" }.tags)
        assertEquals(listOf("personal"), updated.first { it.id == "two" }.tags)
        assertTrue(updated.first { it.id == "three" }.tags.isEmpty())
        assertTrue(updated.first { it.id == "three" }.deleted)
    }

    @Test
    fun widgetSummaryShowsLatestVisibleNoteWithoutLockedPreview() {
        val empty = notesWidgetSummary(emptyList())
        val normal = notesWidgetSummary(
            listOf(
                SNote(id = "old", title = "Old", updatedAt = 1, blocks = listOf(NoteBlock.Text(text = "Old body"))),
                SNote(id = "latest", title = "Latest", updatedAt = 3, blocks = listOf(NoteBlock.Text(text = "Latest body"))),
                SNote(title = "Archived", archived = true, updatedAt = 6),
                SNote(title = "Deleted", deleted = true, updatedAt = 5)
            )
        )
        val pinned = notesWidgetSummary(
            listOf(
                SNote(title = "Recent", updatedAt = 10),
                SNote(title = "Pinned", pinned = true, updatedAt = 1)
            )
        )
        val locked = notesWidgetSummary(listOf(SNote(title = "Private", locked = true, blocks = listOf(NoteBlock.Text(text = "Secret")))))

        assertEquals(NotesWidgetSummary("S Notes Style", "No notes yet"), empty)
        assertEquals("Latest", normal.title)
        assertEquals("latest", normal.noteId)
        assertTrue(normal.subtitle.contains("Latest body"))
        assertTrue(normal.subtitle.contains("2 notes"))
        assertFalse(normal.subtitle.contains("Archived"))
        assertEquals("Pinned", pinned.title)
        assertEquals("Locked note • 1 note", locked.subtitle)
        assertFalse(locked.subtitle.contains("Secret"))
    }

    @Test
    fun widgetSummaryIncludesReminderChecklistAndMediaStatus() {
        val now = 10_000L
        val summary = notesWidgetSummary(
            listOf(
                SNote(
                    id = "latest",
                    title = "Today",
                    updatedAt = 30,
                    reminderAt = now - 1_000,
                    blocks = listOf(
                        NoteBlock.Checklist(
                            items = listOf(
                                CheckItem(text = "Done", checked = true),
                                CheckItem(text = "Open", checked = false)
                            )
                        ),
                        NoteBlock.Attachment(uri = "content://example/pdf", name = "brief.pdf")
                    )
                ),
                SNote(
                    title = "Soon",
                    updatedAt = 20,
                    reminderAt = now + 60_000,
                    blocks = listOf(NoteBlock.Audio(path = "/audio/clip.m4a", name = "clip.m4a"))
                )
            ),
            now = now
        )

        assertEquals("Today", summary.title)
        assertTrue(summary.subtitle.contains("Overdue"))
        assertTrue(summary.subtitle.contains("2 notes"))
        assertTrue(summary.subtitle.contains("1 overdue"))
        assertTrue(summary.subtitle.contains("1/2 tasks"))
        assertTrue(summary.subtitle.contains("2 media"))
    }

    @Test
    fun widgetQuickActionsExposePrimaryNoteKinds() {
        assertEquals(
            listOf(
                NewNoteKind.Text,
                NewNoteKind.Checklist,
                NewNoteKind.Sticky,
                NewNoteKind.Drawing,
                NewNoteKind.Meeting
            ),
            widgetQuickNoteKinds()
        )
    }

    @Test
    fun roomEntityRoundTripPreservesMetadataAndBlocks() {
        val note = SNote(
            title = "Lecture",
            folder = "School/Physics",
            tags = listOf("lecture", "audio"),
            pinned = true,
            favorite = true,
            locked = true,
            archived = true,
            reminderAt = 1_710_000_000_000,
            pageTemplate = PageTemplate.Planner,
            paperColor = 0xFFFFF8D6,
            blocks = listOf(
                NoteBlock.Text(text = "Momentum notes", highlight = 0xFFFFFF00),
                NoteBlock.Audio(
                    path = "/audio/lecture.m4a",
                    name = "lecture.m4a",
                    durationHintMs = 42_000,
                    markers = listOf(AudioMarker(label = "Exam topic", timestampMs = 21_000))
                ),
                NoteBlock.Drawing(
                    strokes = listOf(
                        DrawStroke(
                            color = 0xFF222222,
                            width = 4f,
                            points = listOf(DrawPoint(10f, 20f), DrawPoint(30f, 40f))
                        )
                    )
                )
            )
        )

        val restored = note.toEntity().toNote()

        assertEquals(note.id, restored.id)
        assertEquals("School/Physics", restored.folder)
        assertEquals(listOf("lecture", "audio"), restored.tags)
        assertTrue(restored.pinned)
        assertTrue(restored.favorite)
        assertTrue(restored.locked)
        assertTrue(restored.archived)
        assertFalse(restored.deleted)
        assertNull(restored.deletedAt)
        assertEquals(1_710_000_000_000, restored.reminderAt)
        assertEquals(PageTemplate.Planner, restored.pageTemplate)
        assertEquals(0xFFFFF8D6, restored.paperColor)
        assertEquals("Momentum notes", restored.preview)
        assertEquals(3, restored.blocks.size)
        assertEquals("lecture.m4a", (restored.blocks[1] as NoteBlock.Audio).name)
        assertEquals("Exam topic", (restored.blocks[1] as NoteBlock.Audio).markers.single().label)
        assertEquals(2, (restored.blocks[2] as NoteBlock.Drawing).strokes.single().points.size)
    }

    @Test
    fun surfacesExposeFavoritesTrashAndNestedFolders() {
        val notes = listOf(
            SNote(title = "Root", folder = "School", favorite = true, updatedAt = 3),
            SNote(title = "Child", folder = "School/Physics", reminderAt = 1_700_000_000_000, updatedAt = 2),
            SNote(title = "Trash", folder = "School", deleted = true, updatedAt = 4),
            SNote(title = "Work", folder = "Work", updatedAt = 1)
        )

        val folderState = NotesUiState(notes = notes, surface = NotesSurface.Folders, folderFilter = "School")
        assertEquals(listOf("Root", "Child"), folderState.visibleNotes.map { it.title })
        assertEquals(listOf("School", "Work"), folderState.rootFolders)
        assertEquals(
            listOf(OrganizationSummary("School", 2), OrganizationSummary("Work", 1)),
            folderState.rootFolderSummaries
        )
        assertEquals(
            listOf(OrganizationSummary("School", 2), OrganizationSummary("School/Physics", 1), OrganizationSummary("Work", 1)),
            folderState.folderSummaries
        )

        val favoritesState = NotesUiState(notes = notes, surface = NotesSurface.Favorites)
        assertEquals(listOf("Root"), favoritesState.visibleNotes.map { it.title })
        assertEquals(1, favoritesState.favoritesCount)

        val trashState = NotesUiState(notes = notes, surface = NotesSurface.Trash)
        assertEquals(listOf("Trash"), trashState.visibleNotes.map { it.title })
        assertEquals(1, trashState.trashCount)

        val reminderState = NotesUiState(notes = notes, surface = NotesSurface.Reminders)
        assertEquals(listOf("Child"), reminderState.visibleNotes.map { it.title })
        assertEquals(1, reminderState.reminderCount)
    }

    @Test
    fun tagSummariesCountVisibleOrganizationTags() {
        val notes = listOf(
            SNote(title = "Launch", tags = listOf("work", "launch")),
            SNote(title = "Follow up", tags = listOf("work")),
            SNote(title = "Old", tags = listOf("work"), deleted = true)
        )
        val state = NotesUiState(notes = notes)

        assertEquals(
            listOf(OrganizationSummary("launch", 1), OrganizationSummary("work", 2)),
            state.tagSummaries
        )
        assertEquals("work 2", state.tagSummaries.last().label)
    }

    @Test
    fun trashHelpersRestoreOrDeleteDeletedNotesOnly() {
        val active = SNote(id = "active", title = "Active")
        val deleted = SNote(id = "deleted", title = "Deleted", deleted = true, deletedAt = 1_000)
        val notes = listOf(active, deleted)

        assertEquals(listOf("active"), notes.deleteTrash().map { it.id })
        assertTrue(notes.restoreTrash().none { it.deleted })
        assertNull(notes.restoreTrash().first { it.id == "deleted" }.deletedAt)
        assertEquals(listOf("active", "deleted"), notes.restoreTrash().map { it.id })
    }

    @Test
    fun trashMetadataLabelsAndRoundTrips() {
        val trashed = SNote(id = "trash", title = "Trash", deleted = true, deletedAt = 1_000)
        val moved = SNote(id = "fresh", archived = true).moveToTrash(deletedAt = 3_600_000)
        val restored = moved.restoreFromTrash()
        val roundTrip = trashed.toJson().toNote()

        assertTrue(moved.deleted)
        assertFalse(moved.archived)
        assertEquals(3_600_000L, moved.deletedAt)
        assertFalse(restored.deleted)
        assertNull(restored.deletedAt)
        assertEquals("Moved to Trash 1h ago • 30 days left", trashed.trashLabel(now = 3_601_000))
        assertEquals("In Trash", trashed.copy(deletedAt = null).trashLabel(now = 3_601_000))
        assertNull(SNote(title = "Active").trashLabel())
        assertEquals(1_000L, roundTrip.deletedAt)
        assertEquals(1_000L, trashed.toEntity().toNote().deletedAt)
    }

    @Test
    fun trashRetentionLabelsShowReviewWindow() {
        val deletedAt = 10_000L

        assertEquals("30 days left", trashRetentionLabel(deletedAt, now = deletedAt))
        assertEquals("1 day left", trashRetentionLabel(deletedAt, now = deletedAt + 29L * DAY_MS))
        assertEquals("review window ended", trashRetentionLabel(deletedAt, now = deletedAt + 30L * DAY_MS))
        assertEquals("review window ended", trashRetentionLabel(deletedAt, now = deletedAt + 45L * DAY_MS))
    }

    @Test
    fun expiredTrashHelpersPruneOnlyExpiredDeletedNotes() {
        val deletedAt = 100_000L
        val active = SNote(id = "active", title = "Active", updatedAt = 4)
        val recentTrash = SNote(id = "recent", title = "Recent", deleted = true, deletedAt = deletedAt + DAY_MS, updatedAt = 3)
        val expiredTrash = SNote(id = "expired", title = "Expired", deleted = true, deletedAt = deletedAt, updatedAt = 2)
        val undatedTrash = SNote(id = "undated", title = "Undated", deleted = true, deletedAt = null, updatedAt = 1)
        val notes = listOf(active, recentTrash, expiredTrash, undatedTrash)
        val now = deletedAt + TRASH_RETENTION_DAYS * DAY_MS

        assertEquals(deletedAt + TRASH_RETENTION_DAYS * DAY_MS, expiredTrash.trashExpiresAt())
        assertTrue(expiredTrash.isExpiredTrash(now))
        assertFalse(recentTrash.isExpiredTrash(now))
        assertFalse(active.isExpiredTrash(now))
        assertFalse(undatedTrash.isExpiredTrash(now))
        assertEquals(listOf("expired"), notes.expiredTrashNotes(now).map { it.id })
        assertEquals(listOf("active", "recent", "undated"), notes.deleteExpiredTrash(now).map { it.id })
    }

    @Test
    fun sortModesControlVisibleNoteOrdering() {
        val notes = listOf(
            SNote(title = "Beta", folder = "B", createdAt = 1, updatedAt = 1),
            SNote(title = "Alpha", folder = "A", createdAt = 3, updatedAt = 3),
            SNote(title = "Favorite", folder = "Z", favorite = true, createdAt = 2, updatedAt = 2),
            SNote(title = "Pinned", folder = "Z", pinned = true, createdAt = 1, updatedAt = 1),
            SNote(
                title = "Tasks",
                folder = "C",
                createdAt = 4,
                updatedAt = 4,
                blocks = listOf(
                    NoteBlock.Checklist(
                        items = listOf(
                            CheckItem(text = "One", checked = true),
                            CheckItem(text = "Two", checked = true),
                            CheckItem(text = "Three")
                        )
                    )
                )
            ),
            SNote(
                title = "Media",
                folder = "D",
                createdAt = 5,
                updatedAt = 5,
                blocks = listOf(
                    NoteBlock.Attachment(uri = "content://example/file", name = "brief.pdf"),
                    NoteBlock.Audio(path = "/audio/one.m4a", name = "one.m4a")
                )
            )
        )

        val titleState = NotesUiState(notes = notes, sortMode = NoteSortMode.TitleAscending)
        assertEquals(listOf("Pinned", "Favorite", "Alpha", "Beta", "Media", "Tasks"), titleState.visibleNotes.map { it.title })

        val createdState = NotesUiState(notes = notes, sortMode = NoteSortMode.CreatedNewest)
        assertEquals(listOf("Pinned", "Favorite", "Media", "Tasks", "Alpha", "Beta"), createdState.visibleNotes.map { it.title })

        val modifiedOldestState = NotesUiState(notes = notes, sortMode = NoteSortMode.ModifiedOldest)
        assertEquals(listOf("Pinned", "Favorite", "Beta", "Alpha", "Tasks", "Media"), modifiedOldestState.visibleNotes.map { it.title })

        val createdOldestState = NotesUiState(notes = notes, sortMode = NoteSortMode.CreatedOldest)
        assertEquals(listOf("Pinned", "Favorite", "Beta", "Alpha", "Tasks", "Media"), createdOldestState.visibleNotes.map { it.title })

        val titleDescendingState = NotesUiState(notes = notes, sortMode = NoteSortMode.TitleDescending)
        assertEquals(listOf("Pinned", "Favorite", "Tasks", "Media", "Beta", "Alpha"), titleDescendingState.visibleNotes.map { it.title })

        val folderState = NotesUiState(notes = notes, sortMode = NoteSortMode.FolderAscending)
        assertEquals(listOf("Pinned", "Favorite", "Alpha", "Beta", "Tasks", "Media"), folderState.visibleNotes.map { it.title })

        val checklistState = NotesUiState(notes = notes, sortMode = NoteSortMode.ChecklistProgress)
        assertEquals(listOf("Pinned", "Favorite", "Tasks", "Media", "Alpha", "Beta"), checklistState.visibleNotes.map { it.title })

        val mediaState = NotesUiState(notes = notes, sortMode = NoteSortMode.MediaHeavy)
        assertEquals(listOf("Pinned", "Favorite", "Media", "Tasks", "Alpha", "Beta"), mediaState.visibleNotes.map { it.title })
    }

    @Test
    fun reminderSurfaceOrdersNotesBySoonestReminder() {
        val notes = listOf(
            SNote(title = "Later", reminderAt = 3_000, updatedAt = 3),
            SNote(title = "No reminder", updatedAt = 4),
            SNote(title = "Soon", reminderAt = 1_000, updatedAt = 1),
            SNote(title = "Deleted", reminderAt = 500, deleted = true, updatedAt = 5)
        )

        val state = NotesUiState(
            notes = notes,
            surface = NotesSurface.Reminders,
            sortMode = NoteSortMode.ReminderSoonest
        )

        assertEquals(listOf("Soon", "Later"), state.visibleNotes.map { it.title })
    }

    @Test
    fun trashSurfaceCanOrderByDeletionRecencyAndReviewWindow() {
        val notes = listOf(
            SNote(title = "Active", updatedAt = 9),
            SNote(title = "Old trash", deleted = true, deletedAt = 1_000, updatedAt = 2),
            SNote(title = "Fresh trash", deleted = true, deletedAt = 3_000, updatedAt = 1),
            SNote(title = "Middle trash", deleted = true, deletedAt = 2_000, updatedAt = 3)
        )

        val newestState = NotesUiState(
            notes = notes,
            surface = NotesSurface.Trash,
            sortMode = NoteSortMode.TrashNewest
        )
        val reviewEndingState = newestState.copy(sortMode = NoteSortMode.TrashReviewEnding)

        assertEquals(listOf("Fresh trash", "Middle trash", "Old trash"), newestState.visibleNotes.map { it.title })
        assertEquals(listOf("Old trash", "Middle trash", "Fresh trash"), reviewEndingState.visibleNotes.map { it.title })
    }

    @Test
    fun drawingEraserRemovesOnlyNearbyStrokes() {
        val nearby = DrawStroke(
            color = 0xFF111827,
            width = 4f,
            points = listOf(DrawPoint(10f, 10f), DrawPoint(14f, 14f))
        )
        val far = DrawStroke(
            color = 0xFF1D4ED8,
            width = 4f,
            points = listOf(DrawPoint(200f, 200f), DrawPoint(220f, 220f))
        )

        val remaining = listOf(nearby, far).eraseNear(listOf(DrawPoint(12f, 12f)), radius = 8f)

        assertEquals(listOf(far), remaining)
    }

    @Test
    fun drawingEraserPreservesUnaffectedStrokeSegments() {
        val stroke = DrawStroke(
            color = 0xFF111827,
            width = 4f,
            points = listOf(
                DrawPoint(0f, 0f),
                DrawPoint(8f, 8f),
                DrawPoint(50f, 50f),
                DrawPoint(90f, 90f)
            )
        )

        val remaining = listOf(stroke).eraseNear(listOf(DrawPoint(50f, 50f)), radius = 4f)

        assertEquals(2, remaining.size)
        assertEquals(listOf(DrawPoint(0f, 0f), DrawPoint(8f, 8f)), remaining.first().points)
        assertEquals(listOf(DrawPoint(90f, 90f)), remaining.last().points)
        assertTrue(remaining.all { it.color == stroke.color && it.width == stroke.width })
    }

    @Test
    fun backupJsonSupportsWrappedAndLegacyNoteArrays() {
        val notes = listOf(
            SNote(title = "Backup one", folder = "Work", tags = listOf("backup")),
            SNote(title = "Backup two", folder = "Personal", favorite = true)
        )

        val wrapped = notesFromBackupJson(notesToBackupJson(notes))
        val metadata = backupMetadataFromJson(notesToBackupJson(notes))
        assertEquals(listOf("Backup two", "Backup one"), wrapped.map { it.title }.sortedDescending())
        assertTrue(wrapped.first { it.title == "Backup two" }.favorite)
        assertEquals(BACKUP_SCHEMA_VERSION, metadata?.schemaVersion)
        assertEquals(BACKUP_APP_ID, metadata?.appId)
        assertEquals(2, metadata?.noteCount)
        assertTrue((metadata?.exportedAt ?: 0L) > 0L)

        val legacyArray = org.json.JSONArray().also { array ->
            notes.forEach { array.put(it.toJson()) }
        }.toString()
        val legacy = notesFromBackupJson(legacyArray)
        val legacyMetadata = backupMetadataFromJson(legacyArray)
        assertEquals(2, legacy.size)
        assertEquals("Work", legacy.first { it.title == "Backup one" }.folder)
        assertEquals(0, legacyMetadata?.schemaVersion)
        assertEquals("legacy-array", legacyMetadata?.appId)
        assertEquals(2, legacyMetadata?.noteCount)
    }

    @Test
    fun backupImportSkipsExpiredTrashNotes() {
        val deletedAt = 5_000L
        val notes = listOf(
            SNote(id = "active", title = "Active backup note", updatedAt = 3),
            SNote(id = "recent-trash", title = "Recent trash", deleted = true, deletedAt = deletedAt + DAY_MS, updatedAt = 2),
            SNote(id = "expired-trash", title = "Expired trash", deleted = true, deletedAt = deletedAt, updatedAt = 1)
        )
        val importable = importableNotesFromBackupJson(
            notesToBackupJson(notes),
            now = deletedAt + TRASH_RETENTION_DAYS * DAY_MS
        )

        assertEquals(listOf("active", "recent-trash"), importable.map { it.id })
    }

    @Test
    fun backupImportMergeReplacesMatchingIdsAndKeepsLocalNotes() {
        val current = listOf(
            SNote(id = "same", title = "Local old", updatedAt = 1),
            SNote(id = "local", title = "Local only", updatedAt = 4)
        )
        val imported = listOf(
            SNote(id = "same", title = "Imported replacement", updatedAt = 5),
            SNote(id = "new", title = "Imported new", updatedAt = 3)
        )

        val merged = mergeImportedNotes(current, imported)

        assertEquals(listOf("Imported replacement", "Local only", "Imported new"), merged.map { it.title })
        assertEquals(1, merged.count { it.id == "same" })
        assertTrue(merged.any { it.id == "local" })
    }

    @Test
    fun backupImportStatusNamesSourceWhenMetadataExists() {
        assertEquals(
            "Imported 2 notes from S Notes Style backup v$BACKUP_SCHEMA_VERSION",
            backupImportStatus(
                importedCount = 2,
                metadata = BackupMetadata(
                    schemaVersion = BACKUP_SCHEMA_VERSION,
                    appId = BACKUP_APP_ID,
                    exportedAt = 1L,
                    noteCount = 2
                )
            )
        )
        assertEquals(
            "Imported 1 note from legacy backup",
            backupImportStatus(
                importedCount = 1,
                metadata = BackupMetadata(schemaVersion = 0, appId = "legacy-array", exportedAt = 0L, noteCount = 1)
            )
        )
        assertEquals("Imported 3 notes", backupImportStatus(importedCount = 3, metadata = null))
    }

    @Test
    fun lockedNotesHidePreviewContentFromSearch() {
        val state = NotesUiState(
            notes = listOf(
                SNote(title = "Private", locked = true, blocks = listOf(NoteBlock.Text(text = "secret body"))),
                SNote(title = "Public", blocks = listOf(NoteBlock.Text(text = "secret body")))
            ),
            search = "secret"
        )

        assertEquals(listOf("Public"), state.visibleNotes.map { it.title })

        val titleState = state.copy(search = "Private")
        assertEquals(listOf("Private"), titleState.visibleNotes.map { it.title })
    }

    @Test
    fun attachmentMetadataHelpersClassifyImagesAndFormatSizes() {
        val image = NoteBlock.Attachment(
            uri = "content://example/photo",
            name = "photo.jpg",
            mimeHint = "image/jpeg",
            sizeBytes = 2_621_440
        )
        val file = NoteBlock.Attachment(
            uri = "content://example/document",
            name = "document.pdf",
            mimeHint = "application/pdf",
            sizeBytes = 900,
            pageCount = 12
        )

        assertTrue(image.isImageAttachment)
        assertFalse(file.isImageAttachment)
        assertTrue(file.isPdfAttachment)
        assertEquals("image/jpeg", image.viewMimeType)
        assertEquals("*/*", file.copy(mimeHint = "").viewMimeType)
        assertEquals("2 MB", image.sizeLabel)
        assertEquals("900 B", file.sizeLabel)
        assertEquals("12 pages", file.pageCountLabel)
        assertEquals("application/pdf • 12 pages • 900 B", file.metadataLabel)
        assertEquals("12 pages, 900 B", file.exportDetailLabel)
        assertEquals("", formatBytes(0))
        assertEquals("", formatPageCount(0))
        assertEquals("1 page", formatPageCount(1))
    }

    @Test
    fun importedAudioMetadataCreatesPlayableAudioBlock() {
        val audio = AttachmentMetadata("clip.m4a", "audio/mp4", 4096).toNoteBlock("content://example/audio")
        val pdf = AttachmentMetadata("brief.pdf", "application/pdf", 4096, pageCount = 7).toNoteBlock("content://example/pdf")

        assertTrue(audio is NoteBlock.Audio)
        assertEquals("content://example/audio", (audio as NoteBlock.Audio).path)
        assertEquals("clip.m4a", audio.name)
        assertTrue(audio.markers.isEmpty())
        assertTrue(pdf is NoteBlock.Attachment)
        assertEquals("brief.pdf", (pdf as NoteBlock.Attachment).name)
        assertEquals(7, pdf.pageCount)
    }

    @Test
    fun localImportFileNamesAreSafeAndBounded() {
        assertEquals("brief.pdf", "brief.pdf".safeLocalFileName())
        assertEquals("Work-Plan-Q3-.pdf", "Work/Plan:Q3?.pdf".safeLocalFileName())
        assertEquals("attachment", "   ".safeLocalFileName())
        assertEquals(96, "a".repeat(140).safeLocalFileName().length)
    }

    @Test
    fun localFileReferencesOnlyMatchAppFileProviderRoots() {
        val imported = "content://com.example.snotes.fileprovider/imported_attachments/brief.pdf".localFileReference("com.example.snotes")
        val captured = NoteBlock.Attachment(
            uri = "content://com.example.snotes.fileprovider/captured_images/capture.jpg",
            name = "capture.jpg"
        ).localFileReference("com.example.snotes")
        val audio = NoteBlock.Audio(
            path = "content://com.example.snotes.fileprovider/imported_attachments/clip.m4a",
            name = "clip.m4a"
        ).localFileReference("com.example.snotes")

        assertEquals(LocalFileReference("imports", "brief.pdf"), imported)
        assertEquals(LocalFileReference("captures", "capture.jpg"), captured)
        assertEquals(LocalFileReference("imports", "clip.m4a"), audio)
        assertNull("content://other.app/fileprovider/imported_attachments/brief.pdf".localFileReference("com.example.snotes"))
        assertNull("content://com.example.snotes.fileprovider/other/brief.pdf".localFileReference("com.example.snotes"))
        assertNull("file:///tmp/brief.pdf".localFileReference("com.example.snotes"))
    }

    @Test
    fun audioDurationFormattingUsesMinuteSecondLabels() {
        assertEquals("", formatDuration(0))
        assertEquals("0:01", formatDuration(1_500))
        assertEquals("1:05", formatDuration(65_000))
        assertEquals("10:00", formatDuration(600_000))
    }

    @Test
    fun audioMarkerHelpersAddAndRemoveTimelineBookmarks() {
        val block = NoteBlock.Audio(path = "/audio/session.m4a", name = "session.m4a")
            .addMarker(9_250)
            .addMarker(-1_000)

        assertEquals(listOf("Marker 1", "Marker 2"), block.markers.map { it.label })
        assertEquals(listOf(9_250L, 0L), block.markers.map { it.timestampMs })

        val remaining = block.removeMarker(block.markers.first().id)

        assertEquals(listOf("Marker 2"), remaining.markers.map { it.label })
    }

    @Test
    fun checklistHelpersTrackProgressReorderAndClearCompleted() {
        val first = CheckItem(id = "first", text = "First", checked = false)
        val second = CheckItem(id = "second", text = "Second", checked = true)
        val third = CheckItem(id = "third", text = "Third", checked = false)
        val checklist = NoteBlock.Checklist(items = listOf(first, second, third))

        assertEquals(ChecklistProgress(done = 1, total = 3), checklist.progress())
        assertEquals(listOf("second", "first", "third"), checklist.moveItem("second", -1).items.map { it.id })
        assertEquals(listOf("first", "third"), checklist.clearCompleted().items.map { it.id })
        assertTrue(checklist.setAllChecked(true).items.all { it.checked })
        assertTrue(checklist.setAllChecked(true).setAllChecked(false).items.none { it.checked })

        val onlyDone = NoteBlock.Checklist(items = listOf(CheckItem(text = "Done", checked = true)))
        assertEquals(1, onlyDone.clearCompleted().items.size)
        assertEquals("", onlyDone.clearCompleted().items.single().text)
    }

    @Test
    fun selectionStateAndBatchHelpersMutateOnlySelectedNotes() {
        val notes = listOf(
            SNote(id = "one", title = "One"),
            SNote(id = "two", title = "Two"),
            SNote(id = "three", title = "Three")
        )
        val selected = setOf("one", "three")
        val state = NotesUiState(notes = notes, selectedNoteIds = selected)

        assertTrue(state.isSelectionMode)
        assertEquals(listOf("One", "Three"), state.selectedNotes.map { it.title })

        val favorited = notes.updateByIds(selected) { it.copy(favorite = true) }
        assertTrue(favorited.first { it.id == "one" }.favorite)
        assertFalse(favorited.first { it.id == "two" }.favorite)
        assertTrue(favorited.first { it.id == "three" }.favorite)

        val remaining = notes.deleteByIds(setOf("two"))
        assertEquals(listOf("one", "three"), remaining.map { it.id })
    }
}
