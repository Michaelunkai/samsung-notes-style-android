package com.example.snotes

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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
                    alignment = TextAlignment.Center
                ),
                NoteBlock.Checklist(
                    items = listOf(
                        CheckItem(text = "Ship debug build", checked = true),
                        CheckItem(text = "Collect feedback", checked = false)
                    )
                ),
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
                NoteBlock.Audio(path = "/recordings/one.m4a", name = "one.m4a")
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
        assertEquals(5, restored.blocks.size)
        assertEquals("Discuss release", (restored.blocks[0] as NoteBlock.Text).text)
        assertTrue((restored.blocks[0] as NoteBlock.Text).bold)
        assertTrue((restored.blocks[0] as NoteBlock.Text).underline)
        assertEquals(TextAlignment.Center, (restored.blocks[0] as NoteBlock.Text).alignment)
        assertEquals("Ship debug build", (restored.blocks[1] as NoteBlock.Checklist).items[0].text)
        assertTrue((restored.blocks[1] as NoteBlock.Checklist).items[0].checked)
        assertEquals(2, (restored.blocks[2] as NoteBlock.Drawing).strokes.first().points.size)
        assertEquals(DrawTool.Highlighter, (restored.blocks[2] as NoteBlock.Drawing).activeTool)
        assertEquals(DrawTool.Fountain, (restored.blocks[2] as NoteBlock.Drawing).strokes.first().tool)
        assertEquals("brief.pdf", (restored.blocks[3] as NoteBlock.Attachment).name)
        assertEquals("2 KB", (restored.blocks[3] as NoteBlock.Attachment).sizeLabel)
        assertEquals("one.m4a", (restored.blocks[4] as NoteBlock.Audio).name)
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
    fun searchScopesMatchContentMetadataAndAttachments() {
        val note = SNote(
            title = "Launch plan",
            folder = "Work/Product",
            tags = listOf("release", "team"),
            blocks = listOf(
                NoteBlock.Text(text = "Discuss roadmap milestones and launch readiness"),
                NoteBlock.Checklist(items = listOf(CheckItem(text = "Collect launch assets"))),
                NoteBlock.Attachment(uri = "content://example/deck", name = "launch-deck.pdf", mimeHint = "application/pdf"),
                NoteBlock.Audio(path = "/audio/launch-briefing.m4a", name = "launch-briefing.m4a")
            )
        )

        assertEquals(SearchScope.Title, note.searchMatches("launch", SearchScope.Title).single().scope)
        assertEquals(listOf("Folder: Work/Product"), note.searchMatches("product", SearchScope.Folders).map { it.label })
        assertEquals(listOf("Tag: #release"), note.searchMatches("release", SearchScope.Tags).map { it.label })
        assertTrue(note.searchMatches("roadmap", SearchScope.Content).single().label.startsWith("Text:"))
        assertTrue(note.searchMatches("assets", SearchScope.Content).single().label.startsWith("Checklist:"))
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
    fun newNoteKindsUseConfiguredDefaults() {
        val defaults = NoteDefaults(pageTemplate = PageTemplate.Dotted, paperColor = 0xFFEFF6FF)

        val text = NewNoteKind.Text.createNoteWithDefaults(defaults)
        val checklist = NewNoteKind.Checklist.createNoteWithDefaults(defaults)
        val drawing = NewNoteKind.Drawing.createNoteWithDefaults(defaults)

        assertEquals(PageTemplate.Dotted, text.pageTemplate)
        assertEquals(0xFFEFF6FF, text.paperColor)
        assertTrue(text.blocks.single() is NoteBlock.Text)
        assertTrue(checklist.blocks.single() is NoteBlock.Checklist)
        assertTrue(drawing.blocks.single() is NoteBlock.Drawing)
        assertEquals(PageTemplate.Dotted, checklist.pageTemplate)
        assertEquals(0xFFEFF6FF, drawing.paperColor)
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
        val invalid = noteLaunchRequestFrom(
            action = ACTION_QUICK_NOTE,
            mimeType = null,
            sharedText = null,
            quickKindName = "Unknown"
        )

        assertEquals("Shared meeting note", shared.sharedText)
        assertEquals(NewNoteKind.Drawing, quickDraw.quickNoteKind)
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
                NoteBlock.Drawing(strokes = listOf(DrawStroke(color = 0xFF111111, width = 4f, points = listOf(DrawPoint(1f, 1f))))),
                NoteBlock.Attachment(uri = "content://example/file", name = "brief.pdf", sizeBytes = 2048),
                NoteBlock.Audio(path = "/audio/review.m4a", name = "review.m4a", durationHintMs = 65_000)
            )
        )

        val text = note.toPlainText()

        assertTrue(text.contains("Sprint review"))
        assertTrue(text.contains("Folder: Work"))
        assertTrue(text.contains("Tags: #team, #export"))
        assertTrue(text.contains("Release summary"))
        assertTrue(text.contains("- [x] Demo"))
        assertTrue(text.contains("- [ ] Follow up"))
        assertTrue(text.contains("[Handwriting: 1 stroke]"))
        assertTrue(text.contains("[Attachment: brief.pdf, 2 KB]"))
        assertTrue(text.contains("[Audio: review.m4a, 1:05]"))
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
    fun widgetSummaryShowsLatestVisibleNoteWithoutLockedPreview() {
        val empty = notesWidgetSummary(emptyList())
        val normal = notesWidgetSummary(
            listOf(
                SNote(title = "Old", updatedAt = 1, blocks = listOf(NoteBlock.Text(text = "Old body"))),
                SNote(title = "Latest", updatedAt = 3, blocks = listOf(NoteBlock.Text(text = "Latest body"))),
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
        assertTrue(normal.subtitle.contains("Latest body"))
        assertEquals("Pinned", pinned.title)
        assertEquals("Locked note • 1 note", locked.subtitle)
        assertFalse(locked.subtitle.contains("Secret"))
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
                NoteBlock.Audio(path = "/audio/lecture.m4a", name = "lecture.m4a", durationHintMs = 42_000),
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

        val favoritesState = NotesUiState(notes = notes, surface = NotesSurface.Favorites)
        assertEquals(listOf("Root"), favoritesState.visibleNotes.map { it.title })
        assertEquals(1, favoritesState.favoritesCount)

        val trashState = NotesUiState(notes = notes, surface = NotesSurface.Trash)
        assertEquals(listOf("Trash"), trashState.visibleNotes.map { it.title })
        assertEquals(1, trashState.trashCount)
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
        assertEquals(listOf("Backup two", "Backup one"), wrapped.map { it.title }.sortedDescending())
        assertTrue(wrapped.first { it.title == "Backup two" }.favorite)

        val legacyArray = org.json.JSONArray().also { array ->
            notes.forEach { array.put(it.toJson()) }
        }.toString()
        val legacy = notesFromBackupJson(legacyArray)
        assertEquals(2, legacy.size)
        assertEquals("Work", legacy.first { it.title == "Backup one" }.folder)
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
        assertEquals("2 MB", image.sizeLabel)
        assertEquals("900 B", file.sizeLabel)
        assertEquals("", formatBytes(0))
    }

    @Test
    fun audioDurationFormattingUsesMinuteSecondLabels() {
        assertEquals("", formatDuration(0))
        assertEquals("0:01", formatDuration(1_500))
        assertEquals("1:05", formatDuration(65_000))
        assertEquals("10:00", formatDuration(600_000))
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
