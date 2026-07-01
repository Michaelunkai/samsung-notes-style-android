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
            pageTemplate = PageTemplate.Grid,
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
                NoteBlock.Attachment(uri = "content://example/file", name = "brief.pdf", mimeHint = "application/pdf", sizeBytes = 2048),
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
        assertEquals(PageTemplate.Grid, restored.pageTemplate)
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
        assertEquals("one.m4a", (restored.blocks[5] as NoteBlock.Audio).name)
        assertEquals("Decision point", (restored.blocks[5] as NoteBlock.Audio).markers.single().label)
        assertEquals(12_000, (restored.blocks[5] as NoteBlock.Audio).markers.single().timestampMs)
    }

    @Test
    fun visibleNotesApplySearchFolderTagAndDeletedFilters() {
        val state = NotesUiState(
            notes = listOf(
                SNote(title = "Meeting", folder = "Work", tags = listOf("team"), blocks = listOf(NoteBlock.Text(text = "Roadmap"))),
                SNote(title = "Groceries", folder = "Personal", tags = listOf("errand"), blocks = listOf(NoteBlock.Checklist())),
                SNote(title = "Old", folder = "Work", tags = listOf("team"), deleted = true)
            ),
            search = "road",
            folderFilter = "Work",
            tagFilter = null
        )

        assertEquals(listOf("Meeting"), state.visibleNotes.map { it.title })
        assertFalse(state.visibleNotes.any { it.deleted })

        val tagState = state.copy(search = "", folderFilter = null, tagFilter = "errand")
        assertEquals(listOf("Groceries"), tagState.visibleNotes.map { it.title })
    }

    @Test
    fun emptyNotesCopyExplainsCurrentHomeSurface() {
        assertEquals("No matching notes", NotesUiState(search = "missing").emptyNotesCopy().title)
        assertEquals("Trash is empty", NotesUiState(surface = NotesSurface.Trash).emptyNotesCopy().title)
        assertEquals("No favorites yet", NotesUiState(surface = NotesSurface.Favorites).emptyNotesCopy().title)
        assertEquals(
            "No notes in Work",
            NotesUiState(surface = NotesSurface.Folders, folderFilter = "Work").emptyNotesCopy().title
        )
        assertEquals(
            "No notes tagged #project",
            NotesUiState(surface = NotesSurface.Tags, tagFilter = "project").emptyNotesCopy().title
        )
        assertEquals("Create note", NotesUiState().emptyNotesCopy().actionLabel)
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
                NoteBlock.Attachment(uri = "content://example/deck", name = "launch-deck.pdf", mimeHint = "application/pdf"),
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
            listOf("File: launch-deck.pdf", "Audio: launch-briefing.m4a"),
            note.searchMatches("launch", SearchScope.Attachments).map { it.label }
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
                NoteBlock.Attachment(id = "file", uri = "content://example/file", name = "launch-deck.pdf"),
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

        val folderNote = NewNoteKind.Text.createNoteForState(folderState)
        val tagNote = NewNoteKind.Checklist.createNoteForState(tagState)
        val meetingNote = NewNoteKind.Meeting.createNoteForState(folderState)

        assertEquals("Work/Product", folderNote.folder)
        assertEquals(PageTemplate.Grid, folderNote.pageTemplate)
        assertEquals(listOf("launch"), tagNote.tags)
        assertTrue(tagNote.blocks.single() is NoteBlock.Checklist)
        assertEquals("Work/Product", meetingNote.folder)
        assertTrue(meetingNote.blocks.any { it is NoteBlock.Checklist })
    }

    @Test
    fun storedNoteDefaultsFallbackToSupportedValues() {
        val restored = noteDefaultsFromStoredValues("Grid", 0xFFFFF8D6)
        val fallback = noteDefaultsFromStoredValues("LegacyTemplate", 0xFF123456)

        assertEquals(PageTemplate.Grid, restored.pageTemplate)
        assertEquals(0xFFFFF8D6, restored.paperColor)
        assertEquals(PageTemplate.Plain, fallback.pageTemplate)
        assertEquals(DEFAULT_PAPER_COLORS.first(), fallback.paperColor)
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
    fun selectionStateTracksAvailablePinAndFavoriteActions() {
        val pinnedFavorite = SNote(id = "pinned", pinned = true, favorite = true)
        val normal = SNote(id = "normal")
        val state = NotesUiState(
            notes = listOf(pinnedFavorite, normal),
            selectedNoteIds = setOf("pinned", "normal")
        )

        assertTrue(state.selectedNotesIncludePinned)
        assertTrue(state.selectedNotesIncludeUnpinned)
        assertTrue(state.selectedNotesIncludeFavorite)
        assertTrue(state.selectedNotesIncludeNonFavorite)

        val pinnedOnly = state.copy(selectedNoteIds = setOf("pinned"))
        assertFalse(pinnedOnly.selectedNotesIncludeUnpinned)
        assertFalse(pinnedOnly.selectedNotesIncludeNonFavorite)
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
    fun duplicateNoteCreatesIndependentCopyWithFreshMetadata() {
        val original = SNote(
            id = "source",
            title = "Design brief",
            folder = "Work",
            tags = listOf("copy"),
            pinned = true,
            favorite = true,
            deleted = true,
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
        assertFalse(duplicate.deleted)
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
                NoteBlock.Attachment(uri = "content://example/file", name = "brief.pdf", sizeBytes = 2048),
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
        assertTrue(text.contains("Tags: #team, #export"))
        assertTrue(text.contains("Release summary"))
        assertTrue(text.contains("- [x] Demo"))
        assertTrue(text.contains("- [ ] Follow up"))
        assertTrue(text.contains("[Sticky note] Remember stakeholder questions"))
        assertTrue(text.contains("[Handwriting: 1 stroke]"))
        assertTrue(text.contains("[Attachment: brief.pdf, 2 KB]"))
        assertTrue(text.contains("[Audio: review.m4a, 1:05]"))
        assertTrue(text.contains("- 0:12 Stakeholder question"))
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
            blocks = listOf(
                NoteBlock.Text(text = "alpha beta gamma delta epsilon"),
                NoteBlock.Checklist(items = listOf(CheckItem(text = "send file", checked = true)))
            )
        )

        val lines = note.toPdfLines(maxLineLength = 14)

        assertEquals("PDF plan", lines.first())
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
        assertEquals("Pinned", pinned.title)
        assertEquals("Locked note • 1 note", locked.subtitle)
        assertFalse(locked.subtitle.contains("Secret"))
    }

    @Test
    fun widgetQuickActionsExposePrimaryNoteKinds() {
        assertEquals(
            listOf(NewNoteKind.Text, NewNoteKind.Checklist, NewNoteKind.Sticky, NewNoteKind.Drawing),
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
            pageTemplate = PageTemplate.Ruled,
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
        assertEquals(PageTemplate.Ruled, restored.pageTemplate)
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
            SNote(title = "Child", folder = "School/Physics", updatedAt = 2),
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
        val deleted = SNote(id = "deleted", title = "Deleted", deleted = true)
        val notes = listOf(active, deleted)

        assertEquals(listOf("active"), notes.deleteTrash().map { it.id })
        assertTrue(notes.restoreTrash().none { it.deleted })
        assertEquals(listOf("active", "deleted"), notes.restoreTrash().map { it.id })
    }

    @Test
    fun sortModesControlVisibleNoteOrdering() {
        val notes = listOf(
            SNote(title = "Beta", folder = "B", createdAt = 1, updatedAt = 1),
            SNote(title = "Alpha", folder = "A", createdAt = 3, updatedAt = 3),
            SNote(title = "Favorite", folder = "Z", favorite = true, createdAt = 2, updatedAt = 2),
            SNote(title = "Pinned", folder = "Z", pinned = true, createdAt = 1, updatedAt = 1)
        )

        val titleState = NotesUiState(notes = notes, sortMode = NoteSortMode.TitleAscending)
        assertEquals(listOf("Pinned", "Favorite", "Alpha", "Beta"), titleState.visibleNotes.map { it.title })

        val createdState = NotesUiState(notes = notes, sortMode = NoteSortMode.CreatedNewest)
        assertEquals(listOf("Pinned", "Favorite", "Alpha", "Beta"), createdState.visibleNotes.map { it.title })

        val modifiedOldestState = NotesUiState(notes = notes, sortMode = NoteSortMode.ModifiedOldest)
        assertEquals(listOf("Pinned", "Favorite", "Beta", "Alpha"), modifiedOldestState.visibleNotes.map { it.title })

        val createdOldestState = NotesUiState(notes = notes, sortMode = NoteSortMode.CreatedOldest)
        assertEquals(listOf("Pinned", "Favorite", "Beta", "Alpha"), createdOldestState.visibleNotes.map { it.title })

        val titleDescendingState = NotesUiState(notes = notes, sortMode = NoteSortMode.TitleDescending)
        assertEquals(listOf("Pinned", "Favorite", "Beta", "Alpha"), titleDescendingState.visibleNotes.map { it.title })

        val folderState = NotesUiState(notes = notes, sortMode = NoteSortMode.FolderAscending)
        assertEquals(listOf("Pinned", "Favorite", "Alpha", "Beta"), folderState.visibleNotes.map { it.title })
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
            sizeBytes = 900
        )

        assertTrue(image.isImageAttachment)
        assertFalse(file.isImageAttachment)
        assertEquals("image/jpeg", image.viewMimeType)
        assertEquals("*/*", file.copy(mimeHint = "").viewMimeType)
        assertEquals("2 MB", image.sizeLabel)
        assertEquals("900 B", file.sizeLabel)
        assertEquals("", formatBytes(0))
    }

    @Test
    fun importedAudioMetadataCreatesPlayableAudioBlock() {
        val audio = AttachmentMetadata("clip.m4a", "audio/mp4", 4096).toNoteBlock("content://example/audio")
        val pdf = AttachmentMetadata("brief.pdf", "application/pdf", 4096).toNoteBlock("content://example/pdf")

        assertTrue(audio is NoteBlock.Audio)
        assertEquals("content://example/audio", (audio as NoteBlock.Audio).path)
        assertEquals("clip.m4a", audio.name)
        assertTrue(audio.markers.isEmpty())
        assertTrue(pdf is NoteBlock.Attachment)
        assertEquals("brief.pdf", (pdf as NoteBlock.Attachment).name)
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
