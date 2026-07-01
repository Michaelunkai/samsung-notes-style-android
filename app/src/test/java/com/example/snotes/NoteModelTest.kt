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
            favorite = true,
            blocks = listOf(
                NoteBlock.Text(text = "Discuss release", bold = true, italic = true, underline = true, color = 0xFF1D4ED8),
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
                NoteBlock.Attachment(uri = "content://example/file", name = "brief.pdf", mimeHint = "application/pdf"),
                NoteBlock.Audio(path = "/recordings/one.m4a", name = "one.m4a")
            )
        )

        val restored = note.toJson().toNote()

        assertEquals(note.title, restored.title)
        assertEquals(note.folder, restored.folder)
        assertEquals(note.tags, restored.tags)
        assertTrue(restored.favorite)
        assertEquals(5, restored.blocks.size)
        assertEquals("Discuss release", (restored.blocks[0] as NoteBlock.Text).text)
        assertTrue((restored.blocks[0] as NoteBlock.Text).bold)
        assertTrue((restored.blocks[0] as NoteBlock.Text).underline)
        assertEquals("Ship debug build", (restored.blocks[1] as NoteBlock.Checklist).items[0].text)
        assertTrue((restored.blocks[1] as NoteBlock.Checklist).items[0].checked)
        assertEquals(2, (restored.blocks[2] as NoteBlock.Drawing).strokes.first().points.size)
        assertEquals(DrawTool.Highlighter, (restored.blocks[2] as NoteBlock.Drawing).activeTool)
        assertEquals(DrawTool.Fountain, (restored.blocks[2] as NoteBlock.Drawing).strokes.first().tool)
        assertEquals("brief.pdf", (restored.blocks[3] as NoteBlock.Attachment).name)
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
    fun roomEntityRoundTripPreservesMetadataAndBlocks() {
        val note = SNote(
            title = "Lecture",
            folder = "School/Physics",
            tags = listOf("lecture", "audio"),
            favorite = true,
            locked = true,
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
        assertTrue(restored.favorite)
        assertTrue(restored.locked)
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
            SNote(title = "Favorite", folder = "Z", favorite = true, createdAt = 2, updatedAt = 2)
        )

        val titleState = NotesUiState(notes = notes, sortMode = NoteSortMode.TitleAscending)
        assertEquals(listOf("Favorite", "Alpha", "Beta"), titleState.visibleNotes.map { it.title })

        val createdState = NotesUiState(notes = notes, sortMode = NoteSortMode.CreatedNewest)
        assertEquals(listOf("Favorite", "Alpha", "Beta"), createdState.visibleNotes.map { it.title })

        val folderState = NotesUiState(notes = notes, sortMode = NoteSortMode.FolderAscending)
        assertEquals(listOf("Favorite", "Alpha", "Beta"), folderState.visibleNotes.map { it.title })
    }

    @Test
    fun drawingEraserRemovesOnlyNearbyStrokes() {
        val nearby = DrawStroke(
            color = 0xFF111827,
            width = 4f,
            points = listOf(DrawPoint(10f, 10f), DrawPoint(20f, 20f))
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
}
