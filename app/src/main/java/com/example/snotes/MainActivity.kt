package com.example.snotes

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.graphics.Color as AndroidColor
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FormatAlignCenter
import androidx.compose.material.icons.filled.FormatAlignLeft
import androidx.compose.material.icons.filled.FormatAlignRight
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatColorFill
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.FormatUnderlined
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import java.io.File
import java.security.MessageDigest
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONArray
import org.json.JSONObject

const val ACTION_QUICK_NOTE = "com.example.snotes.action.QUICK_NOTE"
const val EXTRA_QUICK_NOTE_KIND = "com.example.snotes.extra.QUICK_NOTE_KIND"
const val SETTINGS_STORE = "notes_settings"
const val SETTING_DARK_MODE = "dark_mode"
const val SETTING_DEFAULT_PAGE_TEMPLATE = "default_page_template"
const val SETTING_DEFAULT_PAPER_COLOR = "default_paper_color"
const val SETTING_NOTE_PIN_DIGEST = "note_pin_digest"
const val NOTE_PIN_SALT = "s-notes-style-local-pin-v1"

data class NoteLaunchRequest(
    val sharedText: String? = null,
    val sharedAttachments: List<SharedAttachmentRequest> = emptyList(),
    val quickNoteKind: NewNoteKind? = null
)

data class SharedAttachmentRequest(
    val uri: String,
    val mimeHint: String?
)

data class SequencedLaunchRequest(
    val sequence: Int,
    val request: NoteLaunchRequest
)

class MainActivity : ComponentActivity() {
    private var launchSequence = 0
    private var launchRequest by mutableStateOf(SequencedLaunchRequest(0, NoteLaunchRequest()))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        launchRequest = nextLaunchRequest(intent)
        setContent {
            val notesViewModel: NotesViewModel = viewModel(
                factory = AndroidViewModelFactory.getInstance(application)
            )
            NotesApp(notesViewModel, launchRequest)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        launchRequest = nextLaunchRequest(intent)
    }

    private fun nextLaunchRequest(intent: Intent): SequencedLaunchRequest =
        SequencedLaunchRequest(++launchSequence, intent.toNoteLaunchRequest())
}

data class SNote(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "Untitled note",
    val folder: String = "All notes",
    val tags: List<String> = emptyList(),
    val blocks: List<NoteBlock> = listOf(NoteBlock.Text()),
    val pinned: Boolean = false,
    val favorite: Boolean = false,
    val locked: Boolean = false,
    val deleted: Boolean = false,
    val pageTemplate: PageTemplate = PageTemplate.Plain,
    val paperColor: Long = 0xFFFFFBF0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    val preview: String
        get() = blocks.firstOrNull { it is NoteBlock.Text && it.text.isNotBlank() }
            ?.let { (it as NoteBlock.Text).text }
            ?: blocks.firstOrNull()?.label.orEmpty()
}

sealed class NoteBlock(open val id: String, open val label: String) {
    data class Text(
        override val id: String = UUID.randomUUID().toString(),
        val text: String = "",
        val bold: Boolean = false,
        val italic: Boolean = false,
        val underline: Boolean = false,
        val color: Long = 0xFF2B2A27,
        val highlight: Long = 0x00FFFFFF,
        val sizeSp: Int = 18,
        val alignment: TextAlignment = TextAlignment.Start
    ) : NoteBlock(id, "Text")

    data class Checklist(
        override val id: String = UUID.randomUUID().toString(),
        val items: List<CheckItem> = listOf(CheckItem(text = "Checklist item"))
    ) : NoteBlock(id, "Checklist")

    data class Drawing(
        override val id: String = UUID.randomUUID().toString(),
        val strokes: List<DrawStroke> = emptyList(),
        val activeTool: DrawTool = DrawTool.Pen,
        val penColor: Long = 0xFF1D4ED8,
        val strokeWidth: Float = 5f
    ) : NoteBlock(id, "Handwriting")

    data class Attachment(
        override val id: String = UUID.randomUUID().toString(),
        val uri: String,
        val name: String,
        val mimeHint: String = "file",
        val sizeBytes: Long = 0L
    ) : NoteBlock(id, "Attachment")

    data class Audio(
        override val id: String = UUID.randomUUID().toString(),
        val path: String,
        val name: String,
        val durationHintMs: Long = 0L
    ) : NoteBlock(id, "Audio")
}

data class CheckItem(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val checked: Boolean = false
)

data class ChecklistProgress(val done: Int, val total: Int) {
    val label: String
        get() = "$done/$total done"
}

data class SearchMatch(val scope: SearchScope, val label: String)

data class DrawPoint(val x: Float, val y: Float)

data class DrawStroke(
    val id: String = UUID.randomUUID().toString(),
    val color: Long,
    val width: Float,
    val tool: DrawTool = DrawTool.Pen,
    val points: List<DrawPoint>
)

enum class DrawTool(val label: String) {
    Pen("Pen"),
    Fountain("Fountain"),
    Highlighter("Highlighter"),
    Eraser("Eraser")
}

enum class TextAlignment(val label: String) {
    Start("Left"),
    Center("Center"),
    End("Right")
}

fun TextAlignment.toComposeTextAlign(): TextAlign = when (this) {
    TextAlignment.Start -> TextAlign.Start
    TextAlignment.Center -> TextAlign.Center
    TextAlignment.End -> TextAlign.End
}

enum class PageTemplate(val label: String) {
    Plain("Plain"),
    Ruled("Ruled"),
    Grid("Grid"),
    Dotted("Dotted")
}

val DEFAULT_PAPER_COLORS = listOf(
    0xFFFFFBF0L,
    0xFFFFFFFFL,
    0xFFFFF8D6L,
    0xFFEFF6FFL,
    0xFFF5F5F4L
)

data class NoteDefaults(
    val pageTemplate: PageTemplate = PageTemplate.Plain,
    val paperColor: Long = DEFAULT_PAPER_COLORS.first()
)

fun noteDefaultsFromStoredValues(templateName: String?, paperColor: Long): NoteDefaults =
    NoteDefaults(
        pageTemplate = templateName.orEmpty().toPageTemplate(PageTemplate.Plain),
        paperColor = paperColor.takeIf { it in DEFAULT_PAPER_COLORS } ?: DEFAULT_PAPER_COLORS.first()
    )

fun normalizeFolder(folder: String): String =
    folder.trim().replace(Regex("""[/\\]+"""), "/").trim('/').ifBlank { "All notes" }

fun parseTagInput(tags: String): List<String> =
    tags.split(",")
        .map { it.trim().removePrefix("#") }
        .filter { it.isNotBlank() }
        .distinct()

fun mergeTags(existing: List<String>, added: String): List<String> =
    (existing + parseTagInput(added)).distinct()

fun renameFolderPath(folder: String, from: String, to: String): String {
    val source = normalizeFolder(from)
    val target = normalizeFolder(to)
    return when {
        folder == source -> target
        folder.startsWith("$source/") -> target + folder.removePrefix(source)
        else -> folder
    }
}

fun renameTagList(tags: List<String>, from: String, to: String): List<String> {
    val source = from.trim().removePrefix("#")
    val target = parseTagInput(to).firstOrNull() ?: return tags
    return tags.map { tag -> if (tag == source) target else tag }.distinct()
}

fun SharedPreferences.loadNoteDefaults(): NoteDefaults =
    noteDefaultsFromStoredValues(
        templateName = getString(SETTING_DEFAULT_PAGE_TEMPLATE, PageTemplate.Plain.name),
        paperColor = getLong(SETTING_DEFAULT_PAPER_COLOR, DEFAULT_PAPER_COLORS.first())
    )

fun hashNotesPin(pin: String): String =
    MessageDigest.getInstance("SHA-256")
        .digest("$NOTE_PIN_SALT:${pin.trim()}".toByteArray())
        .joinToString("") { "%02x".format(it) }

fun isUsableNotesPin(pin: String): Boolean =
    pin.length in 4..12 && pin.all { it.isDigit() }

fun verifyNotesPin(pin: String, digest: String?): Boolean =
    !digest.isNullOrBlank() && isUsableNotesPin(pin) && hashNotesPin(pin) == digest

data class NotesUiState(
    val notes: List<SNote> = emptyList(),
    val selectedNoteId: String? = null,
    val selectedNoteIds: Set<String> = emptySet(),
    val search: String = "",
    val folderFilter: String? = null,
    val tagFilter: String? = null,
    val surface: NotesSurface = NotesSurface.All,
    val sortMode: NoteSortMode = NoteSortMode.ModifiedNewest,
    val viewMode: NoteViewMode = NoteViewMode.List,
    val searchScope: SearchScope = SearchScope.All,
    val statusMessage: String? = null,
    val darkMode: Boolean = false,
    val noteDefaults: NoteDefaults = NoteDefaults(),
    val notePinDigest: String? = null,
    val unlockedNoteIds: Set<String> = emptySet()
) {
    val visibleNotes: List<SNote>
        get() = notes
            .filter { note ->
                when (surface) {
                    NotesSurface.All, NotesSurface.Folders, NotesSurface.Tags -> !note.deleted
                    NotesSurface.Favorites -> !note.deleted && note.favorite
                    NotesSurface.Trash -> note.deleted
                }
            }
            .filter { note -> folderFilter == null || note.folder == folderFilter || note.folder.startsWith("$folderFilter/") }
            .filter { note -> tagFilter == null || tagFilter in note.tags }
            .filter { note -> search.isBlank() || note.searchMatches(search, searchScope).isNotEmpty() }
            .sortedWith(sortMode.comparator)

    val selectedNote: SNote?
        get() = notes.firstOrNull { it.id == selectedNoteId }

    val selectedNotes: List<SNote>
        get() = notes.filter { it.id in selectedNoteIds }

    val isSelectionMode: Boolean
        get() = selectedNoteIds.isNotEmpty()

    val folders: List<String>
        get() = notes.filter { !it.deleted }.map { it.folder }.distinct().sorted()

    val rootFolders: List<String>
        get() = folders.map { it.substringBefore("/") }.distinct().sorted()

    val tags: List<String>
        get() = notes.filter { !it.deleted }.flatMap { it.tags }.distinct().sorted()

    val trashCount: Int
        get() = notes.count { it.deleted }

    val favoritesCount: Int
        get() = notes.count { !it.deleted && it.favorite }

    val hasNotePin: Boolean
        get() = !notePinDigest.isNullOrBlank()
}

enum class NotesSurface(val label: String) {
    All("All notes"),
    Folders("Folders"),
    Tags("Tags"),
    Favorites("Favorites"),
    Trash("Trash")
}

enum class SearchScope(val label: String) {
    All("All"),
    Title("Title"),
    Content("Content"),
    Folders("Folders"),
    Tags("Tags"),
    Attachments("Files")
}

enum class NoteSortMode(val label: String, val comparator: Comparator<SNote>) {
    ModifiedNewest(
        "Date modified",
        compareByDescending<SNote> { it.pinned }.thenByDescending { it.favorite }.thenByDescending { it.updatedAt }
    ),
    CreatedNewest(
        "Date created",
        compareByDescending<SNote> { it.pinned }.thenByDescending { it.favorite }.thenByDescending { it.createdAt }
    ),
    TitleAscending(
        "Title",
        compareByDescending<SNote> { it.pinned }.thenByDescending { it.favorite }.thenBy { it.title.lowercase() }
    ),
    FolderAscending(
        "Folder",
        compareByDescending<SNote> { it.pinned }.thenByDescending { it.favorite }.thenBy { it.folder.lowercase() }.thenBy { it.title.lowercase() }
    )
}

enum class NoteViewMode(val label: String) {
    List("List"),
    Grid("Grid")
}

class NotesViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = RoomNoteRepository(application)
    private val settings = application.getSharedPreferences(SETTINGS_STORE, Context.MODE_PRIVATE)
    private val _state = MutableStateFlow(
        NotesUiState(
            notes = loadInitialNotes(),
            darkMode = settings.getBoolean(SETTING_DARK_MODE, false),
            noteDefaults = settings.loadNoteDefaults(),
            notePinDigest = settings.getString(SETTING_NOTE_PIN_DIGEST, null)
        )
    )
    val state: StateFlow<NotesUiState> = _state

    fun createNote(kind: NewNoteKind) {
        val note = kind.createNoteWithDefaults(_state.value.noteDefaults)
        _state.update { it.copy(notes = listOf(note) + it.notes, selectedNoteId = note.id) }
        persist()
    }

    fun createSharedTextNote(text: String) {
        createSharedNote(
            title = text.lineSequence().firstOrNull()?.take(48)?.ifBlank { "Shared note" } ?: "Shared note",
            blocks = listOf(NoteBlock.Text(text = text))
        )
    }

    fun createSharedImportNote(sharedText: String?, attachments: List<NoteBlock.Attachment>) {
        val firstAttachment = attachments.firstOrNull()
        val title = sharedText
            ?.lineSequence()
            ?.firstOrNull()
            ?.take(48)
            ?.ifBlank { null }
            ?: firstAttachment?.name?.take(48)
            ?: "Shared file"
        val blocks = buildList {
            if (!sharedText.isNullOrBlank()) add(NoteBlock.Text(text = sharedText))
            addAll(attachments)
            if (isEmpty()) add(NoteBlock.Text())
        }
        createSharedNote(title = title, blocks = blocks)
    }

    private fun createSharedNote(title: String, blocks: List<NoteBlock>) {
        val note = SNote(
            title = title,
            folder = "Shared",
            tags = listOf("shared"),
            pageTemplate = _state.value.noteDefaults.pageTemplate,
            paperColor = _state.value.noteDefaults.paperColor,
            blocks = blocks
        )
        _state.update { it.copy(notes = listOf(note) + it.notes, selectedNoteId = note.id) }
        persist()
    }

    fun selectNote(id: String?) {
        _state.update { it.copy(selectedNoteId = id) }
    }

    fun toggleNoteSelection(id: String) {
        _state.update { state ->
            val selected = if (id in state.selectedNoteIds) state.selectedNoteIds - id else state.selectedNoteIds + id
            state.copy(selectedNoteIds = selected, selectedNoteId = null)
        }
    }

    fun clearSelection() {
        _state.update { it.copy(selectedNoteIds = emptySet()) }
    }

    fun setSearch(search: String) {
        _state.update { it.copy(search = search) }
    }

    fun setSearchScope(searchScope: SearchScope) {
        _state.update { it.copy(searchScope = searchScope) }
    }

    fun filterFolder(folder: String?) {
        _state.update { it.copy(surface = if (folder == null) NotesSurface.All else NotesSurface.Folders, folderFilter = folder, tagFilter = null) }
    }

    fun filterTag(tag: String?) {
        _state.update { it.copy(surface = if (tag == null) NotesSurface.All else NotesSurface.Tags, tagFilter = tag, folderFilter = null) }
    }

    fun setSurface(surface: NotesSurface) {
        _state.update {
            when (surface) {
                NotesSurface.All -> it.copy(surface = surface, folderFilter = null, tagFilter = null)
                NotesSurface.Favorites, NotesSurface.Trash -> it.copy(surface = surface, folderFilter = null, tagFilter = null)
                NotesSurface.Folders -> it.copy(surface = surface, tagFilter = null, folderFilter = it.folderFilter ?: it.rootFolders.firstOrNull() ?: it.folders.firstOrNull())
                NotesSurface.Tags -> it.copy(surface = surface, folderFilter = null, tagFilter = it.tagFilter ?: it.tags.firstOrNull())
            }
        }
    }

    fun setSortMode(sortMode: NoteSortMode) {
        _state.update { it.copy(sortMode = sortMode) }
    }

    fun toggleViewMode() {
        _state.update {
            it.copy(viewMode = if (it.viewMode == NoteViewMode.List) NoteViewMode.Grid else NoteViewMode.List)
        }
    }

    fun toggleTheme() {
        _state.update { it.copy(darkMode = !it.darkMode) }
        persistSettings()
    }

    fun setDefaultPageTemplate(pageTemplate: PageTemplate) {
        _state.update { it.copy(noteDefaults = it.noteDefaults.copy(pageTemplate = pageTemplate)) }
        persistSettings()
    }

    fun setDefaultPaperColor(paperColor: Long) {
        _state.update { it.copy(noteDefaults = it.noteDefaults.copy(paperColor = paperColor)) }
        persistSettings()
    }

    private fun persistSettings() {
        val state = _state.value
        settings.edit()
            .putBoolean(SETTING_DARK_MODE, state.darkMode)
            .putString(SETTING_DEFAULT_PAGE_TEMPLATE, state.noteDefaults.pageTemplate.name)
            .putLong(SETTING_DEFAULT_PAPER_COLOR, state.noteDefaults.paperColor)
            .putString(SETTING_NOTE_PIN_DIGEST, state.notePinDigest)
            .apply()
    }

    fun setNotesPin(pin: String): Boolean {
        if (!isUsableNotesPin(pin)) {
            _state.update { it.copy(statusMessage = "Use a 4-12 digit PIN") }
            return false
        }
        _state.update {
            it.copy(
                notePinDigest = hashNotesPin(pin),
                unlockedNoteIds = emptySet(),
                statusMessage = "Notes PIN updated"
            )
        }
        persistSettings()
        return true
    }

    fun clearNotesPin() {
        _state.update {
            it.copy(
                notePinDigest = null,
                unlockedNoteIds = emptySet(),
                statusMessage = "Notes PIN removed"
            )
        }
        persistSettings()
    }

    fun updateNote(note: SNote) {
        _state.update { state ->
            state.copy(notes = state.notes.map {
                if (it.id == note.id) note.copy(updatedAt = System.currentTimeMillis()) else it
            })
        }
        persist()
    }

    fun updateTitle(note: SNote, title: String) {
        updateNote(note.copy(title = title.ifBlank { "Untitled note" }))
    }

    fun updateFolder(note: SNote, folder: String) {
        updateNote(note.copy(folder = normalizeFolder(folder)))
    }

    fun updateTags(note: SNote, tags: String) {
        updateNote(
            note.copy(
                tags = parseTagInput(tags)
            )
        )
    }

    fun updatePageStyle(note: SNote, template: PageTemplate = note.pageTemplate, paperColor: Long = note.paperColor) {
        updateNote(note.copy(pageTemplate = template, paperColor = paperColor))
    }

    fun toggleFavorite(note: SNote) {
        updateNote(note.copy(favorite = !note.favorite))
    }

    fun togglePinned(note: SNote) {
        updateNote(note.copy(pinned = !note.pinned))
    }

    fun duplicateNote(note: SNote) {
        val duplicate = note.duplicate()
        _state.update { it.copy(notes = listOf(duplicate) + it.notes, selectedNoteId = duplicate.id, selectedNoteIds = emptySet()) }
        persist()
    }

    fun toggleLocked(note: SNote) {
        val state = _state.value
        if (!note.locked && !state.hasNotePin) {
            _state.update { it.copy(statusMessage = "Set a Notes PIN in Settings first") }
            return
        }
        if (note.locked && note.id !in state.unlockedNoteIds) {
            _state.update { it.copy(statusMessage = "Unlock note with PIN first") }
            return
        }
        val locked = !note.locked
        updateNote(note.copy(locked = locked))
        _state.update { current ->
            current.copy(
                unlockedNoteIds = current.unlockedNoteIds - note.id,
                selectedNoteId = current.selectedNoteId.takeUnless { selectedId -> selectedId == note.id && locked },
                statusMessage = if (locked) "Note locked" else "Note lock removed"
            )
        }
    }

    fun unlockNote(note: SNote, pin: String): Boolean {
        val digest = _state.value.notePinDigest
        if (!note.locked) {
            selectNote(note.id)
            return true
        }
        if (!verifyNotesPin(pin, digest)) {
            _state.update { it.copy(statusMessage = "Incorrect Notes PIN") }
            return false
        }
        _state.update {
            it.copy(
                unlockedNoteIds = it.unlockedNoteIds + note.id,
                selectedNoteId = note.id,
                statusMessage = "Note unlocked for this session"
            )
        }
        return true
    }

    fun deleteNote(note: SNote) {
        updateNote(note.copy(deleted = true))
        _state.update { it.copy(selectedNoteId = null) }
    }

    fun restoreNote(note: SNote) {
        updateNote(note.copy(deleted = false))
    }

    fun permanentlyDeleteNote(note: SNote) {
        _state.update { state ->
            state.copy(
                notes = state.notes.filterNot { it.id == note.id },
                selectedNoteId = state.selectedNoteId.takeUnless { it == note.id }
            )
        }
        persist()
    }

    fun batchFavoriteSelected(favorite: Boolean) {
        updateSelectedNotes { it.copy(favorite = favorite) }
    }

    fun batchPinSelected(pinned: Boolean) {
        updateSelectedNotes { it.copy(pinned = pinned) }
    }

    fun batchLockSelected(locked: Boolean) {
        if (locked && !_state.value.hasNotePin) {
            _state.update { it.copy(statusMessage = "Set a Notes PIN in Settings first") }
            return
        }
        updateSelectedNotes { it.copy(locked = locked) }
    }

    fun batchMoveSelectedToFolder(folder: String) {
        updateSelectedNotes { it.copy(folder = normalizeFolder(folder)) }
    }

    fun batchAddTagsSelected(tags: String) {
        val parsed = parseTagInput(tags)
        if (parsed.isEmpty()) {
            _state.update { it.copy(statusMessage = "Enter at least one tag") }
            return
        }
        updateSelectedNotes { it.copy(tags = mergeTags(it.tags, tags)) }
    }

    fun renameFolder(from: String, to: String) {
        val target = normalizeFolder(to)
        if (target == "All notes" && to.isBlank()) {
            _state.update { it.copy(statusMessage = "Enter a folder name") }
            return
        }
        _state.update { state ->
            state.copy(
                notes = state.notes.map { note -> note.copy(folder = renameFolderPath(note.folder, from, target)) },
                folderFilter = state.folderFilter?.let { renameFolderPath(it, from, target) },
                statusMessage = "Folder renamed"
            )
        }
        persist()
    }

    fun renameTag(from: String, to: String) {
        val target = parseTagInput(to).firstOrNull()
        if (target == null) {
            _state.update { it.copy(statusMessage = "Enter a tag") }
            return
        }
        _state.update { state ->
            state.copy(
                notes = state.notes.map { note -> note.copy(tags = renameTagList(note.tags, from, target)) },
                tagFilter = state.tagFilter?.let { if (it == from) target else it },
                statusMessage = "Tag renamed"
            )
        }
        persist()
    }

    fun batchMoveSelectedToTrash() {
        updateSelectedNotes { it.copy(deleted = true) }
    }

    fun batchRestoreSelected() {
        updateSelectedNotes { it.copy(deleted = false) }
    }

    fun batchDeleteSelectedPermanently() {
        _state.update { state ->
            state.copy(
                notes = state.notes.deleteByIds(state.selectedNoteIds),
                selectedNoteIds = emptySet(),
                selectedNoteId = state.selectedNoteId.takeUnless { it in state.selectedNoteIds },
                statusMessage = "Deleted selected notes"
            )
        }
        persist()
    }

    private fun updateSelectedNotes(transform: (SNote) -> SNote) {
        _state.update { state ->
            state.copy(
                notes = state.notes.updateByIds(state.selectedNoteIds, transform),
                selectedNoteIds = emptySet(),
                statusMessage = "Updated selected notes"
            )
        }
        persist()
    }

    fun exportBackupText(): String = notesToBackupJson(_state.value.notes)

    fun restoreBackupText(rawBackup: String) {
        val imported = notesFromBackupJson(rawBackup)
        if (imported.isEmpty()) {
            _state.update { it.copy(statusMessage = "No notes found in backup") }
            return
        }
        _state.update { state ->
            val importedIds = imported.map { it.id }.toSet()
            val merged = (imported + state.notes.filterNot { it.id in importedIds })
                .sortedWith(NoteSortMode.ModifiedNewest.comparator)
            state.copy(
                notes = merged,
                selectedNoteId = imported.firstOrNull()?.id,
                statusMessage = "Imported ${imported.size} notes"
            )
        }
        persist()
    }

    fun setStatus(message: String?) {
        _state.update { it.copy(statusMessage = message) }
    }

    fun addBlock(note: SNote, block: NoteBlock) {
        updateNote(note.copy(blocks = note.blocks + block))
    }

    fun updateBlock(note: SNote, block: NoteBlock) {
        updateNote(note.copy(blocks = note.blocks.map { if (it.id == block.id) block else it }))
    }

    fun removeBlock(note: SNote, block: NoteBlock) {
        updateNote(note.copy(blocks = note.blocks.filterNot { it.id == block.id }))
    }

    fun moveBlock(note: SNote, blockId: String, direction: Int) {
        updateNote(note.moveBlock(blockId, direction))
    }

    private fun persist() {
        val notes = _state.value.notes
        viewModelScope.launch(Dispatchers.IO) {
            repository.save(notes)
            refreshNotesWidgets(getApplication())
        }
    }

    private fun loadInitialNotes(): List<SNote> = runBlocking(Dispatchers.IO) {
        val loaded = repository.load()
        if (loaded.isNotEmpty()) {
            loaded
        } else {
            listOf(sampleNote()).also { repository.save(it) }
        }
    }

    private fun sampleNote() = SNote(
        title = "Welcome notebook",
        folder = "Ideas",
        tags = listOf("demo", "notes"),
        favorite = true,
        blocks = listOf(
            NoteBlock.Text(
                text = "Create text notes, sketch handwriting, attach files, record audio, and organize everything with folders, tags, and search.",
                bold = true,
                sizeSp = 20
            ),
            NoteBlock.Checklist(
                items = listOf(
                    CheckItem(text = "Draft a meeting note", checked = true),
                    CheckItem(text = "Add an audio recording"),
                    CheckItem(text = "Sketch an idea")
                )
            ),
            NoteBlock.Drawing(
                strokes = listOf(
                    DrawStroke(
                        color = 0xFF1D4ED8,
                        width = 7f,
                        points = listOf(
                            DrawPoint(40f, 120f),
                            DrawPoint(90f, 80f),
                            DrawPoint(160f, 130f),
                            DrawPoint(230f, 70f)
                        )
                    )
                )
            )
        )
    )
}

enum class NewNoteKind(val title: String) {
    Text("New note"),
    Checklist("New checklist"),
    Drawing("New sketch")
}

fun NewNoteKind.createNoteWithDefaults(defaults: NoteDefaults = NoteDefaults()): SNote {
    val blocks = when (this) {
        NewNoteKind.Text -> listOf(NoteBlock.Text())
        NewNoteKind.Checklist -> listOf(NoteBlock.Checklist())
        NewNoteKind.Drawing -> listOf(NoteBlock.Drawing())
    }
    return SNote(
        title = title,
        blocks = blocks,
        pageTemplate = defaults.pageTemplate,
        paperColor = defaults.paperColor
    )
}

fun SNote.duplicate(now: Long = System.currentTimeMillis()): SNote = copy(
    id = UUID.randomUUID().toString(),
    title = duplicateTitle(title),
    blocks = blocks.map { it.duplicateBlock() },
    pinned = false,
    favorite = false,
    deleted = false,
    createdAt = now,
    updatedAt = now
)

fun duplicateTitle(title: String): String =
    if (title.startsWith("Copy of ")) "$title copy" else "Copy of $title"

fun NoteBlock.duplicateBlock(): NoteBlock = when (this) {
    is NoteBlock.Text -> copy(id = UUID.randomUUID().toString())
    is NoteBlock.Checklist -> copy(
        id = UUID.randomUUID().toString(),
        items = items.map { it.copy(id = UUID.randomUUID().toString()) }
    )
    is NoteBlock.Drawing -> copy(
        id = UUID.randomUUID().toString(),
        strokes = strokes.map { stroke -> stroke.copy(id = UUID.randomUUID().toString()) }
    )
    is NoteBlock.Attachment -> copy(id = UUID.randomUUID().toString())
    is NoteBlock.Audio -> copy(id = UUID.randomUUID().toString())
}

fun SNote.moveBlock(blockId: String, direction: Int): SNote {
    val from = blocks.indexOfFirst { it.id == blockId }
    if (from < 0) return this
    val to = (from + direction).coerceIn(blocks.indices)
    if (from == to) return this
    val reordered = blocks.toMutableList()
    val block = reordered.removeAt(from)
    reordered.add(to, block)
    return copy(blocks = reordered)
}

fun SNote.toPlainText(): String = buildString {
    appendLine(title)
    appendLine("Folder: $folder")
    if (tags.isNotEmpty()) appendLine("Tags: ${tags.joinToString(", ") { "#$it" }}")
    appendLine()
    blocks.forEachIndexed { index, block ->
        if (index > 0) appendLine()
        append(block.toPlainText())
    }
}.trim()

fun SNote.toPdfLines(maxLineLength: Int = 88): List<String> =
    toPlainText()
        .lineSequence()
        .flatMap { line -> line.wrapLine(maxLineLength).asSequence() }
        .toList()

fun String.wrapLine(maxLineLength: Int): List<String> {
    if (length <= maxLineLength || maxLineLength <= 8) return listOf(this)
    val words = split(Regex("""\s+"""))
    val lines = mutableListOf<String>()
    var current = ""
    words.forEach { word ->
        current = when {
            current.isBlank() -> word
            current.length + word.length + 1 <= maxLineLength -> "$current $word"
            else -> {
                lines += current
                word
            }
        }
    }
    if (current.isNotBlank()) lines += current
    return lines.ifEmpty { listOf("") }
}

fun NoteBlock.toPlainText(): String = when (this) {
    is NoteBlock.Text -> text.ifBlank { "[Empty text]" }
    is NoteBlock.Checklist -> items.joinToString("\n") { item ->
        "${if (item.checked) "- [x]" else "- [ ]"} ${item.text.ifBlank { "Checklist item" }}"
    }
    is NoteBlock.Drawing -> "[Handwriting: ${strokes.size} stroke${if (strokes.size == 1) "" else "s"}]"
    is NoteBlock.Attachment -> "[Attachment: $name${sizeLabel.takeIf { it.isNotBlank() }?.let { ", $it" }.orEmpty()}]"
    is NoteBlock.Audio -> "[Audio: $name${formatDuration(durationHintMs).takeIf { it.isNotBlank() }?.let { ", $it" }.orEmpty()}]"
}

fun String.sanitizeFileName(): String =
    replace(Regex("""[\\/:*?"<>|]+"""), "-")
        .trim()
        .ifBlank { "Untitled note" }
        .take(80)

fun shareNoteText(context: Context, note: SNote) {
    val intent = Intent(Intent.ACTION_SEND)
        .setType("text/plain")
        .putExtra(Intent.EXTRA_SUBJECT, note.title)
        .putExtra(Intent.EXTRA_TEXT, note.toPlainText())
    context.startActivity(Intent.createChooser(intent, "Share note"))
}

fun writeNotePdf(context: Context, uri: Uri, note: SNote) {
    val document = PdfDocument()
    try {
        val titlePaint = Paint().apply {
            color = AndroidColor.rgb(43, 42, 39)
            textSize = 20f
            isFakeBoldText = true
        }
        val bodyPaint = Paint().apply {
            color = AndroidColor.rgb(43, 42, 39)
            textSize = 12f
        }
        val pageWidth = 595
        val pageHeight = 842
        val margin = 48f
        val lineHeight = 18f
        val maxLinesPerPage = ((pageHeight - margin * 2) / lineHeight).toInt()
        val lines = note.toPdfLines().drop(1).ifEmpty { listOf(" ") }
        var pageNumber = 1
        lines.chunked(maxLinesPerPage).forEach { chunk ->
            val page = document.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create())
            val canvas = page.canvas
            var y = margin
            if (pageNumber == 1) {
                canvas.drawText(note.title, margin, y, titlePaint)
                y += lineHeight * 1.5f
            }
            chunk.forEach { line ->
                canvas.drawText(line, margin, y, bodyPaint)
                y += lineHeight
            }
            document.finishPage(page)
            pageNumber += 1
        }
        context.contentResolver.openOutputStream(uri)?.use { output ->
            document.writeTo(output)
        } ?: error("Unable to open PDF destination")
    } finally {
        document.close()
    }
}

fun Intent.toNoteLaunchRequest(): NoteLaunchRequest =
    noteLaunchRequestFrom(
        action = action,
        mimeType = type,
        sharedText = getStringExtra(Intent.EXTRA_TEXT),
        quickKindName = getStringExtra(EXTRA_QUICK_NOTE_KIND),
        sharedAttachments = sharedStreamUris().map { uri -> SharedAttachmentRequest(uri.toString(), type) }
    )

fun noteLaunchRequestFrom(
    action: String?,
    mimeType: String?,
    sharedText: String?,
    quickKindName: String?,
    sharedAttachments: List<SharedAttachmentRequest> = emptyList()
): NoteLaunchRequest {
    val isSharedAction = action == Intent.ACTION_SEND || action == Intent.ACTION_SEND_MULTIPLE
    val shared = sharedText
        ?.takeIf { isSharedAction && mimeType?.startsWith("text/") == true }
        ?.takeIf { it.isNotBlank() }
    val attachments = sharedAttachments
        .takeIf { isSharedAction }
        .orEmpty()
        .distinctBy { it.uri }
        .filter { it.uri.isNotBlank() }
    val quickKind = quickKindName
        ?.takeIf { action == ACTION_QUICK_NOTE }
        ?.let { raw -> NewNoteKind.entries.firstOrNull { it.name.equals(raw, ignoreCase = true) } }
    return NoteLaunchRequest(sharedText = shared, sharedAttachments = attachments, quickNoteKind = quickKind)
}

@Suppress("DEPRECATION")
fun Intent.sharedStreamUris(): List<Uri> = buildList {
    getParcelableExtra<Uri>(Intent.EXTRA_STREAM)?.let { add(it) }
    getParcelableArrayListExtra<Uri>(Intent.EXTRA_STREAM)?.let { addAll(it) }
    val clips = clipData ?: return@buildList
    repeat(clips.itemCount) { index ->
        clips.getItemAt(index).uri?.let { add(it) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesApp(viewModel: NotesViewModel, launchRequest: SequencedLaunchRequest = SequencedLaunchRequest(0, NoteLaunchRequest())) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    LaunchedEffect(launchRequest.sequence) {
        val request = launchRequest.request
        if (request.sharedAttachments.isNotEmpty()) {
            val attachments = request.sharedAttachments.map { shared ->
                val uri = Uri.parse(shared.uri)
                runCatching {
                    context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                val metadata = queryAttachmentMetadata(context, uri)
                NoteBlock.Attachment(
                    uri = shared.uri,
                    name = metadata.name,
                    mimeHint = shared.mimeHint ?: metadata.mimeHint,
                    sizeBytes = metadata.sizeBytes
                )
            }
            viewModel.createSharedImportNote(request.sharedText, attachments)
        } else {
            request.sharedText?.let { viewModel.createSharedTextNote(it) }
        }
        request.quickNoteKind?.let { viewModel.createNote(it) }
    }
    val scheme = if (state.darkMode) {
        darkColorScheme(
            primary = Color(0xFFFAD65A),
            secondary = Color(0xFF9AD0F5),
            background = Color(0xFF141311),
            surface = Color(0xFF1F1E1B)
        )
    } else {
        lightColorScheme(
            primary = Color(0xFF2B2A27),
            secondary = Color(0xFF386FA4),
            background = Color(0xFFF7F4EA),
            surface = Color(0xFFFFFBF0)
        )
    }

    MaterialTheme(colorScheme = scheme) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            val selected = state.selectedNote
            if (selected == null || (selected.locked && selected.id !in state.unlockedNoteIds)) {
                NotesHome(state, viewModel)
            } else {
                NoteEditor(selected, state, viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesHome(state: NotesUiState, viewModel: NotesViewModel) {
    val context = LocalContext.current
    var createMenuOpen by remember { mutableStateOf(false) }
    var sortMenuOpen by remember { mutableStateOf(false) }
    var settingsOpen by remember { mutableStateOf(false) }
    var unlockTarget by remember { mutableStateOf<SNote?>(null) }
    var pendingBackupText by remember { mutableStateOf("") }
    val exportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri: Uri? ->
        uri ?: return@rememberLauncherForActivityResult
        runCatching {
            context.contentResolver.openOutputStream(uri)?.bufferedWriter()?.use { writer ->
                writer.write(pendingBackupText)
            } ?: error("Unable to open backup destination")
        }.onSuccess {
            viewModel.setStatus("Backup exported")
        }.onFailure {
            viewModel.setStatus("Backup export failed")
        }
    }
    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        uri ?: return@rememberLauncherForActivityResult
        runCatching {
            context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { reader ->
                reader.readText()
            } ?: error("Unable to open backup file")
        }.onSuccess { rawBackup ->
            viewModel.restoreBackupText(rawBackup)
        }.onFailure {
            viewModel.setStatus("Backup import failed")
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.surface.label) },
                actions = {
                    IconButton(onClick = viewModel::toggleTheme) {
                        Icon(
                            if (state.darkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle theme"
                        )
                    }
                    Box {
                        IconButton(onClick = { sortMenuOpen = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More")
                        }
                        DropdownMenu(expanded = sortMenuOpen, onDismissRequest = { sortMenuOpen = false }) {
                            DropdownMenuItem(
                                text = { Text("View: ${state.viewMode.label}") },
                                leadingIcon = { Icon(Icons.Default.Description, null) },
                                onClick = {
                                    sortMenuOpen = false
                                    viewModel.toggleViewMode()
                                }
                            )
                            NoteSortMode.entries.forEach { mode ->
                                DropdownMenuItem(
                                    text = { Text("Sort: ${mode.label}") },
                                    leadingIcon = {
                                        if (state.sortMode == mode) Icon(Icons.Default.CheckBox, null)
                                    },
                                    onClick = {
                                        sortMenuOpen = false
                                        viewModel.setSortMode(mode)
                                    }
                                )
                            }
                            DropdownMenuItem(
                                text = { Text("Settings") },
                                leadingIcon = { Icon(Icons.Default.Settings, null) },
                                onClick = {
                                    sortMenuOpen = false
                                    settingsOpen = true
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Export backup") },
                                leadingIcon = { Icon(Icons.Default.AttachFile, null) },
                                onClick = {
                                    sortMenuOpen = false
                                    pendingBackupText = viewModel.exportBackupText()
                                    exportLauncher.launch("snotes-backup-${System.currentTimeMillis()}.json")
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Import backup") },
                                leadingIcon = { Icon(Icons.Default.Description, null) },
                                onClick = {
                                    sortMenuOpen = false
                                    importLauncher.launch(arrayOf("application/json", "text/*", "*/*"))
                                }
                            )
                        }
                    }
                    Box {
                        IconButton(onClick = { createMenuOpen = true }) {
                            Icon(Icons.Default.Add, contentDescription = "Create")
                        }
                        DropdownMenu(expanded = createMenuOpen, onDismissRequest = { createMenuOpen = false }) {
                            DropdownMenuItem(
                                text = { Text("Text note") },
                                leadingIcon = { Icon(Icons.Default.TextFields, null) },
                                onClick = {
                                    createMenuOpen = false
                                    viewModel.createNote(NewNoteKind.Text)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Checklist") },
                                leadingIcon = { Icon(Icons.Default.CheckBox, null) },
                                onClick = {
                                    createMenuOpen = false
                                    viewModel.createNote(NewNoteKind.Checklist)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Handwriting") },
                                leadingIcon = { Icon(Icons.Default.Brush, null) },
                                onClick = {
                                    createMenuOpen = false
                                    viewModel.createNote(NewNoteKind.Drawing)
                                }
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("New note") },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                onClick = { viewModel.createNote(NewNoteKind.Text) }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = state.surface == NotesSurface.All,
                    onClick = { viewModel.setSurface(NotesSurface.All) },
                    icon = { Icon(Icons.Default.Description, contentDescription = null) },
                    label = { Text("All") }
                )
                NavigationBarItem(
                    selected = state.surface == NotesSurface.Folders,
                    onClick = { viewModel.setSurface(NotesSurface.Folders) },
                    icon = { Icon(Icons.Default.Folder, contentDescription = null) },
                    label = { Text("Folders") }
                )
                NavigationBarItem(
                    selected = state.surface == NotesSurface.Favorites,
                    onClick = { viewModel.setSurface(NotesSurface.Favorites) },
                    icon = { Icon(Icons.Default.Favorite, contentDescription = null) },
                    label = { Text("Favorites") }
                )
                NavigationBarItem(
                    selected = state.surface == NotesSurface.Trash,
                    onClick = { viewModel.setSurface(NotesSurface.Trash) },
                    icon = { Icon(Icons.Default.Delete, contentDescription = null) },
                    label = { Text("Trash") }
                )
                NavigationBarItem(
                    selected = state.surface == NotesSurface.Tags,
                    onClick = { viewModel.setSurface(NotesSurface.Tags) },
                    icon = { Icon(Icons.Default.Tag, contentDescription = null) },
                    label = { Text("Tags") }
                )
            }
        }
    ) { padding ->
        if (settingsOpen) {
            SettingsDialog(
                state = state,
                viewModel = viewModel,
                onDismiss = { settingsOpen = false }
            )
        }
        unlockTarget?.let { note ->
            UnlockNoteDialog(
                note = note,
                onUnlock = { pin ->
                    if (viewModel.unlockNote(note, pin)) unlockTarget = null
                },
                onDismiss = { unlockTarget = null }
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.search,
                onValueChange = viewModel::setSearch,
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                placeholder = { Text("Search notes") },
                singleLine = true
            )
            if (state.search.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                SearchScopeChips(state, viewModel)
            }
            Spacer(Modifier.height(12.dp))
            state.statusMessage?.let { message ->
                AssistChip(
                    onClick = { viewModel.setStatus(null) },
                    label = { Text(message) },
                    leadingIcon = { Icon(Icons.Default.CheckBox, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
                Spacer(Modifier.height(8.dp))
            }
            FilterRail(state, viewModel)
            Spacer(Modifier.height(12.dp))
            Text(
                text = if (state.isSelectionMode) {
                    "${state.selectedNoteIds.size} selected"
                } else {
                    "${state.visibleNotes.size} notes • ${state.sortMode.label} • ${state.viewMode.label}"
                },
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))
            if (state.isSelectionMode) {
                SelectionActionBar(state, viewModel)
                Spacer(Modifier.height(8.dp))
            }
            if (state.viewMode == NoteViewMode.Grid) {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 170.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 96.dp)
                ) {
                    gridItems(state.visibleNotes, key = { it.id }) { note ->
                        NoteCard(
                            note = note,
                            inTrash = state.surface == NotesSurface.Trash,
                            selected = note.id in state.selectedNoteIds,
                            search = state.search,
                            searchScope = state.searchScope,
                            onClick = {
                                if (state.isSelectionMode) {
                                    viewModel.toggleNoteSelection(note.id)
                                } else {
                                    when {
                                        note.deleted -> Unit
                                        note.locked && state.hasNotePin -> unlockTarget = note
                                        note.locked -> viewModel.setStatus("Set a Notes PIN in Settings first")
                                        else -> viewModel.selectNote(note.id)
                                    }
                                }
                            },
                            onLongClick = { viewModel.toggleNoteSelection(note.id) },
                            onOpenLocked = {
                                if (state.hasNotePin) unlockTarget = note else viewModel.setStatus("Set a Notes PIN in Settings first")
                            },
                            onDuplicate = { viewModel.duplicateNote(note) },
                            onTogglePinned = { viewModel.togglePinned(note) },
                            onToggleFavorite = { viewModel.toggleFavorite(note) },
                            onToggleLock = { viewModel.toggleLocked(note) },
                            onMoveToTrash = { viewModel.deleteNote(note) },
                            onRestore = { viewModel.restoreNote(note) },
                            onPermanentDelete = { viewModel.permanentlyDeleteNote(note) }
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 96.dp)
                ) {
                    items(state.visibleNotes, key = { it.id }) { note ->
                        NoteCard(
                            note = note,
                            inTrash = state.surface == NotesSurface.Trash,
                            selected = note.id in state.selectedNoteIds,
                            search = state.search,
                            searchScope = state.searchScope,
                            onClick = {
                                if (state.isSelectionMode) {
                                    viewModel.toggleNoteSelection(note.id)
                                } else {
                                    when {
                                        note.deleted -> Unit
                                        note.locked && state.hasNotePin -> unlockTarget = note
                                        note.locked -> viewModel.setStatus("Set a Notes PIN in Settings first")
                                        else -> viewModel.selectNote(note.id)
                                    }
                                }
                            },
                            onLongClick = { viewModel.toggleNoteSelection(note.id) },
                            onOpenLocked = {
                                if (state.hasNotePin) unlockTarget = note else viewModel.setStatus("Set a Notes PIN in Settings first")
                            },
                            onDuplicate = { viewModel.duplicateNote(note) },
                            onTogglePinned = { viewModel.togglePinned(note) },
                            onToggleFavorite = { viewModel.toggleFavorite(note) },
                            onToggleLock = { viewModel.toggleLocked(note) },
                            onMoveToTrash = { viewModel.deleteNote(note) },
                            onRestore = { viewModel.restoreNote(note) },
                            onPermanentDelete = { viewModel.permanentlyDeleteNote(note) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchScopeChips(state: NotesUiState, viewModel: NotesViewModel) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        SearchScope.entries.forEach { scope ->
            FilterChip(
                selected = state.searchScope == scope,
                onClick = { viewModel.setSearchScope(scope) },
                label = { Text(scope.label) }
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SelectionActionBar(state: NotesUiState, viewModel: NotesViewModel) {
    var moveDialogOpen by remember { mutableStateOf(false) }
    var tagDialogOpen by remember { mutableStateOf(false) }
    if (moveDialogOpen) {
        BatchTextActionDialog(
            title = "Move to folder",
            label = "Folder",
            confirmText = "Move",
            onConfirm = {
                viewModel.batchMoveSelectedToFolder(it)
                moveDialogOpen = false
            },
            onDismiss = { moveDialogOpen = false }
        )
    }
    if (tagDialogOpen) {
        BatchTextActionDialog(
            title = "Add tags",
            label = "Tags",
            confirmText = "Add",
            onConfirm = {
                viewModel.batchAddTagsSelected(it)
                tagDialogOpen = false
            },
            onDismiss = { tagDialogOpen = false }
        )
    }
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        if (state.surface == NotesSurface.Trash) {
            Button(onClick = viewModel::batchRestoreSelected) {
                Text("Restore")
            }
            Button(onClick = viewModel::batchDeleteSelectedPermanently) {
                Text("Delete")
            }
        } else {
            Button(onClick = { moveDialogOpen = true }) {
                Text("Move")
            }
            Button(onClick = { tagDialogOpen = true }) {
                Text("Tag")
            }
            Button(onClick = { viewModel.batchPinSelected(true) }) {
                Text("Pin")
            }
            Button(onClick = { viewModel.batchFavoriteSelected(true) }) {
                Text("Favorite")
            }
            Button(onClick = { viewModel.batchLockSelected(true) }) {
                Text("Lock")
            }
            Button(onClick = viewModel::batchMoveSelectedToTrash) {
                Text("Trash")
            }
        }
        OutlinedButton(onClick = viewModel::clearSelection) {
            Text("Cancel")
        }
    }
}

@Composable
fun BatchTextActionDialog(
    title: String,
    label: String,
    confirmText: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var value by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = value,
                onValueChange = { value = it },
                label = { Text(label) },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(value) }) {
                Text(confirmText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
fun FilterRail(state: NotesUiState, viewModel: NotesViewModel) {
    var folderRenameTarget by remember { mutableStateOf<String?>(null) }
    var tagRenameTarget by remember { mutableStateOf<String?>(null) }
    folderRenameTarget?.let { folder ->
        BatchTextActionDialog(
            title = "Rename folder",
            label = "Folder",
            confirmText = "Rename",
            onConfirm = {
                viewModel.renameFolder(folder, it)
                folderRenameTarget = null
            },
            onDismiss = { folderRenameTarget = null }
        )
    }
    tagRenameTarget?.let { tag ->
        BatchTextActionDialog(
            title = "Rename tag",
            label = "Tag",
            confirmText = "Rename",
            onConfirm = {
                viewModel.renameTag(tag, it)
                tagRenameTarget = null
            },
            onDismiss = { tagRenameTarget = null }
        )
    }
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        FilterChip(
            selected = state.surface == NotesSurface.All,
            onClick = { viewModel.setSurface(NotesSurface.All) },
            label = { Text("All notes") },
            leadingIcon = { Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(16.dp)) }
        )
        FilterChip(
            selected = state.surface == NotesSurface.Favorites,
            onClick = { viewModel.setSurface(NotesSurface.Favorites) },
            label = { Text("Favorites ${state.favoritesCount}") },
            leadingIcon = { Icon(Icons.Default.Favorite, contentDescription = null, modifier = Modifier.size(16.dp)) }
        )
        FilterChip(
            selected = state.surface == NotesSurface.Trash,
            onClick = { viewModel.setSurface(NotesSurface.Trash) },
            label = { Text("Trash ${state.trashCount}") },
            leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp)) }
        )
        val folderChips = if (state.surface == NotesSurface.Folders && state.folderFilter != null) {
            state.folders
        } else {
            state.rootFolders
        }
        folderChips.forEach { folder ->
            val childCount = state.notes.count { !it.deleted && (it.folder == folder || it.folder.startsWith("$folder/")) }
            if (childCount > 0) {
                FilterChip(
                    selected = state.folderFilter == folder,
                    onClick = { viewModel.filterFolder(folder) },
                    label = { Text("$folder $childCount") },
                    leadingIcon = { Icon(Icons.Default.Folder, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
            }
        }
        if (state.surface == NotesSurface.Folders && state.folderFilter != null) {
            AssistChip(
                onClick = { folderRenameTarget = state.folderFilter },
                label = { Text("Rename folder") },
                leadingIcon = { Icon(Icons.Default.Folder, contentDescription = null, modifier = Modifier.size(16.dp)) }
            )
        }
        state.tags.forEach { tag ->
            FilterChip(
                selected = state.tagFilter == tag,
                onClick = { viewModel.filterTag(tag) },
                label = { Text("#$tag") },
                leadingIcon = { Icon(Icons.Default.Tag, contentDescription = null, modifier = Modifier.size(16.dp)) }
            )
        }
        if (state.surface == NotesSurface.Tags && state.tagFilter != null) {
            AssistChip(
                onClick = { tagRenameTarget = state.tagFilter },
                label = { Text("Rename tag") },
                leadingIcon = { Icon(Icons.Default.Tag, contentDescription = null, modifier = Modifier.size(16.dp)) }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteCard(
    note: SNote,
    inTrash: Boolean,
    selected: Boolean,
    search: String,
    searchScope: SearchScope,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onOpenLocked: () -> Unit,
    onDuplicate: () -> Unit,
    onTogglePinned: () -> Unit,
    onToggleFavorite: () -> Unit,
    onToggleLock: () -> Unit,
    onMoveToTrash: () -> Unit,
    onRestore: () -> Unit,
    onPermanentDelete: () -> Unit
) {
    var menuOpen by remember { mutableStateOf(false) }
    val matches = remember(note, search, searchScope) { note.searchMatches(search, searchScope) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(onClick = onClick, onLongClick = onLongClick),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (selected) {
                    Icon(Icons.Default.CheckBox, contentDescription = "Selected", tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(8.dp))
                }
                Text(
                    text = note.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                if (note.pinned) Icon(Icons.Default.PushPin, contentDescription = "Pinned", tint = MaterialTheme.colorScheme.primary)
                if (note.favorite) Icon(Icons.Default.Favorite, contentDescription = "Favorite", tint = Color(0xFFE3A008))
                if (note.locked) Icon(Icons.Default.Lock, contentDescription = "Locked", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Box {
                    IconButton(onClick = { menuOpen = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Note actions")
                    }
                    DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                        if (inTrash) {
                            DropdownMenuItem(
                                text = { Text("Select") },
                                leadingIcon = { Icon(Icons.Default.CheckBox, null) },
                                onClick = {
                                    menuOpen = false
                                    onLongClick()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Restore") },
                                leadingIcon = { Icon(Icons.Default.Description, null) },
                                onClick = {
                                    menuOpen = false
                                    onRestore()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Delete permanently") },
                                leadingIcon = { Icon(Icons.Default.Delete, null) },
                                onClick = {
                                    menuOpen = false
                                    onPermanentDelete()
                                }
                            )
                        } else {
                            DropdownMenuItem(
                                text = { Text("Select") },
                                leadingIcon = { Icon(Icons.Default.CheckBox, null) },
                                onClick = {
                                    menuOpen = false
                                    onLongClick()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Duplicate") },
                                leadingIcon = { Icon(Icons.Default.ContentCopy, null) },
                                onClick = {
                                    menuOpen = false
                                    onDuplicate()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(if (note.pinned) "Unpin note" else "Pin note") },
                                leadingIcon = { Icon(Icons.Default.PushPin, null) },
                                onClick = {
                                    menuOpen = false
                                    onTogglePinned()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(if (note.favorite) "Remove favorite" else "Add favorite") },
                                leadingIcon = { Icon(Icons.Default.Favorite, null) },
                                onClick = {
                                    menuOpen = false
                                    onToggleFavorite()
                                }
                            )
                            if (note.locked) {
                                DropdownMenuItem(
                                    text = { Text("Unlock and edit") },
                                    leadingIcon = { Icon(Icons.Default.LockOpen, null) },
                                    onClick = {
                                        menuOpen = false
                                        onOpenLocked()
                                    }
                                )
                            } else {
                                DropdownMenuItem(
                                    text = { Text("Lock note") },
                                    leadingIcon = { Icon(Icons.Default.Lock, null) },
                                    onClick = {
                                        menuOpen = false
                                        onToggleLock()
                                    }
                                )
                            }
                            DropdownMenuItem(
                                text = { Text("Move to Trash") },
                                leadingIcon = { Icon(Icons.Default.Delete, null) },
                                onClick = {
                                    menuOpen = false
                                    onMoveToTrash()
                                }
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(6.dp))
            Text(
                if (note.locked) "Locked note" else note.preview,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (search.isNotBlank() && matches.isNotEmpty()) {
                Spacer(Modifier.height(6.dp))
                Text(
                    matches.joinToString(" • ") { it.label },
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                AssistChip(onClick = {}, label = { Text(note.folder) }, leadingIcon = { Icon(Icons.Default.Folder, null) })
                note.tags.take(2).forEach { tag ->
                    AssistChip(onClick = {}, label = { Text("#$tag") })
                }
                Spacer(Modifier.weight(1f))
                if (note.locked) {
                    Icon(Icons.Default.Lock, contentDescription = "Locked note")
                } else {
                    BlockBadges(note.blocks)
                }
            }
        }
    }
}

@Composable
fun BlockBadges(blocks: List<NoteBlock>) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        if (blocks.any { it is NoteBlock.Drawing }) Icon(Icons.Default.Brush, "Drawing", modifier = Modifier.size(18.dp))
        if (blocks.any { it is NoteBlock.Checklist }) Icon(Icons.Default.CheckBox, "Checklist", modifier = Modifier.size(18.dp))
        if (blocks.any { it is NoteBlock.Attachment }) Icon(Icons.Default.AttachFile, "Attachment", modifier = Modifier.size(18.dp))
        if (blocks.any { it is NoteBlock.Audio }) Icon(Icons.Default.AudioFile, "Audio", modifier = Modifier.size(18.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditor(note: SNote, state: NotesUiState, viewModel: NotesViewModel) {
    val context = LocalContext.current
    val audioRecorder = remember { AudioRecorder(context) }
    var isRecording by remember { mutableStateOf(false) }
    var recordingStartedAt by remember { mutableStateOf(0L) }
    var pendingNoteExportText by remember { mutableStateOf("") }
    var pendingPdfExportNote by remember { mutableStateOf<SNote?>(null) }
    DisposableEffect(audioRecorder) {
        onDispose {
            audioRecorder.stop()
        }
    }
    val audioPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted && !isRecording) {
            val file = audioRecorder.start()
            if (file != null) {
                recordingStartedAt = System.currentTimeMillis()
                isRecording = true
            }
        }
    }
    val attachmentLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        uri ?: return@rememberLauncherForActivityResult
        runCatching {
            context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val metadata = queryAttachmentMetadata(context, uri)
        viewModel.addBlock(
            note,
            NoteBlock.Attachment(
                uri = uri.toString(),
                name = metadata.name,
                mimeHint = metadata.mimeHint,
                sizeBytes = metadata.sizeBytes
            )
        )
    }
    val noteExportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/plain")) { uri: Uri? ->
        uri ?: return@rememberLauncherForActivityResult
        runCatching {
            context.contentResolver.openOutputStream(uri)?.bufferedWriter()?.use { writer ->
                writer.write(pendingNoteExportText)
            } ?: error("Unable to open note export destination")
        }.onSuccess {
            viewModel.setStatus("Note exported")
        }.onFailure {
            viewModel.setStatus("Note export failed")
        }
    }
    val notePdfExportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/pdf")) { uri: Uri? ->
        uri ?: return@rememberLauncherForActivityResult
        val exportNote = pendingPdfExportNote ?: note
        runCatching {
            writeNotePdf(context, uri, exportNote)
        }.onSuccess {
            viewModel.setStatus("PDF exported")
        }.onFailure {
            viewModel.setStatus("PDF export failed")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(note.title, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.selectNote(null) }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.togglePinned(note) }) {
                        Icon(
                            Icons.Default.PushPin,
                            contentDescription = "Pin note",
                            tint = if (note.pinned) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = { viewModel.duplicateNote(note) }) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Duplicate note")
                    }
                    IconButton(onClick = { shareNoteText(context, note) }) {
                        Icon(Icons.Default.Share, contentDescription = "Share note")
                    }
                    IconButton(
                        onClick = {
                            pendingNoteExportText = note.toPlainText()
                            noteExportLauncher.launch("${note.title.sanitizeFileName()}.txt")
                        }
                    ) {
                        Icon(Icons.Default.Description, contentDescription = "Export note")
                    }
                    IconButton(
                        onClick = {
                            pendingPdfExportNote = note
                            notePdfExportLauncher.launch("${note.title.sanitizeFileName()}.pdf")
                        }
                    ) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = "Export PDF")
                    }
                    IconButton(onClick = { viewModel.toggleFavorite(note) }) {
                        Icon(
                            if (note.favorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite"
                        )
                    }
                    IconButton(onClick = { viewModel.toggleLocked(note) }) {
                        Icon(Icons.Default.Lock, contentDescription = "Lock note")
                    }
                    IconButton(onClick = { viewModel.deleteNote(note) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Move to trash")
                    }
                }
            )
        },
        bottomBar = {
            EditorToolbar(
                isRecording = isRecording,
                onAddText = { viewModel.addBlock(note, NoteBlock.Text()) },
                onAddChecklist = { viewModel.addBlock(note, NoteBlock.Checklist()) },
                onAddDrawing = { viewModel.addBlock(note, NoteBlock.Drawing()) },
                onAddAttachment = { attachmentLauncher.launch(arrayOf("*/*")) },
                onRecord = {
                    if (isRecording) {
                        val file = audioRecorder.stop()
                        val durationMs = (System.currentTimeMillis() - recordingStartedAt).coerceAtLeast(0L)
                        isRecording = false
                        if (file != null) {
                            viewModel.addBlock(note, NoteBlock.Audio(path = file.absolutePath, name = file.name, durationHintMs = durationMs))
                        }
                    } else if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                        val file = audioRecorder.start()
                        if (file != null) {
                            recordingStartedAt = System.currentTimeMillis()
                            isRecording = true
                        }
                    } else {
                        audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                NoteMetaEditor(note, state, viewModel)
            }
            itemsIndexed(note.blocks, key = { _, block -> block.id }) { index, block ->
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                    Column(
                        modifier = Modifier.width(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        IconButton(
                            enabled = index > 0,
                            onClick = { viewModel.moveBlock(note, block.id, -1) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Move block up")
                        }
                        IconButton(
                            enabled = index < note.blocks.lastIndex,
                            onClick = { viewModel.moveBlock(note, block.id, 1) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Move block down")
                        }
                    }
                    Box(Modifier.weight(1f)) {
                        when (block) {
                            is NoteBlock.Text -> TextBlockEditor(note, block, viewModel)
                            is NoteBlock.Checklist -> ChecklistBlockEditor(note, block, viewModel)
                            is NoteBlock.Drawing -> DrawingBlockEditor(note, block, viewModel)
                            is NoteBlock.Attachment -> AttachmentBlock(note, block, viewModel)
                            is NoteBlock.Audio -> AudioBlock(note, block, viewModel)
                        }
                    }
                }
            }
            item {
                Spacer(Modifier.height(72.dp))
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsDialog(state: NotesUiState, viewModel: NotesViewModel, onDismiss: () -> Unit) {
    var notesPin by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Settings") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Theme", style = MaterialTheme.typography.labelLarge)
                        Text(
                            if (state.darkMode) "Dark mode" else "Light mode",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    OutlinedButton(onClick = viewModel::toggleTheme) {
                        Icon(
                            if (state.darkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = null
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(if (state.darkMode) "Light" else "Dark")
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Locked notes", style = MaterialTheme.typography.labelLarge)
                    Text(
                        if (state.hasNotePin) "A Notes PIN is set" else "Set a 4-12 digit PIN before locking notes",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            modifier = Modifier.weight(1f),
                            value = notesPin,
                            onValueChange = { notesPin = it.filter(Char::isDigit).take(12) },
                            label = { Text("PIN") },
                            singleLine = true
                        )
                        Button(
                            onClick = {
                                if (viewModel.setNotesPin(notesPin)) notesPin = ""
                            }
                        ) {
                            Text("Set")
                        }
                    }
                    if (state.hasNotePin) {
                        OutlinedButton(onClick = viewModel::clearNotesPin) {
                            Text("Remove PIN")
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Default page", style = MaterialTheme.typography.labelLarge)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        PageTemplate.entries.forEach { template ->
                            FilterChip(
                                selected = state.noteDefaults.pageTemplate == template,
                                onClick = { viewModel.setDefaultPageTemplate(template) },
                                label = { Text(template.label) }
                            )
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Default paper", style = MaterialTheme.typography.labelLarge)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        DEFAULT_PAPER_COLORS.forEach { color ->
                            Box(
                                Modifier
                                    .size(32.dp)
                                    .background(Color(color), CircleShape)
                                    .border(
                                        width = if (state.noteDefaults.paperColor == color) 3.dp else 1.dp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        shape = CircleShape
                                    )
                                    .clickable { viewModel.setDefaultPaperColor(color) }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done")
            }
        }
    )
}

@Composable
fun UnlockNoteDialog(note: SNote, onUnlock: (String) -> Unit, onDismiss: () -> Unit) {
    var notesPin by remember(note.id) { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Unlock note") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(note.title, style = MaterialTheme.typography.labelLarge)
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = notesPin,
                    onValueChange = { notesPin = it.filter(Char::isDigit).take(12) },
                    label = { Text("Notes PIN") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onUnlock(notesPin) }) {
                Text("Unlock")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NoteMetaEditor(note: SNote, state: NotesUiState, viewModel: NotesViewModel) {
    Card(shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = note.title,
                onValueChange = { viewModel.updateTitle(note, it) },
                label = { Text("Title") },
                singleLine = true
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = note.folder,
                    onValueChange = { viewModel.updateFolder(note, it) },
                    label = { Text("Folder") },
                    singleLine = true
                )
                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = note.tags.joinToString(", "),
                    onValueChange = { viewModel.updateTags(note, it) },
                    label = { Text("Tags") },
                    singleLine = true
                )
            }
            if (state.folders.isNotEmpty() || state.tags.isNotEmpty()) {
                Text("Folders and tags update search and filter immediately.", style = MaterialTheme.typography.bodySmall)
            }
            Text("Page", style = MaterialTheme.typography.labelLarge)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                PageTemplate.entries.forEach { template ->
                    FilterChip(
                        selected = note.pageTemplate == template,
                        onClick = { viewModel.updatePageStyle(note, template = template) },
                        label = { Text(template.label) }
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                DEFAULT_PAPER_COLORS.forEach { color ->
                    Box(
                        Modifier
                            .size(30.dp)
                            .background(Color(color), CircleShape)
                            .border(
                                width = if (note.paperColor == color) 3.dp else 1.dp,
                                color = MaterialTheme.colorScheme.onSurface,
                                shape = CircleShape
                            )
                            .clickable { viewModel.updatePageStyle(note, paperColor = color) }
                    )
                }
            }
        }
    }
}

@Composable
fun EditorToolbar(
    isRecording: Boolean,
    onAddText: () -> Unit,
    onAddChecklist: () -> Unit,
    onAddDrawing: () -> Unit,
    onAddAttachment: () -> Unit,
    onRecord: () -> Unit
) {
    Surface(tonalElevation = 4.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onAddText) { Icon(Icons.Default.TextFields, "Text") }
            IconButton(onClick = onAddChecklist) { Icon(Icons.Default.CheckBox, "Checklist") }
            IconButton(onClick = onAddDrawing) { Icon(Icons.Default.Brush, "Handwriting") }
            IconButton(onClick = onAddAttachment) { Icon(Icons.Default.AttachFile, "Attachment") }
            FilledIconButton(onClick = onRecord) {
                Icon(Icons.Default.Mic, if (isRecording) "Stop recording" else "Record audio")
            }
        }
    }
}

@Composable
fun TextBlockEditor(note: SNote, block: NoteBlock.Text, viewModel: NotesViewModel) {
    var colorMenuOpen by remember { mutableStateOf(false) }
    var highlightMenuOpen by remember { mutableStateOf(false) }
    val textStyle = TextStyle(
        color = Color(block.color),
        fontSize = block.sizeSp.sp,
        fontWeight = if (block.bold) FontWeight.Bold else FontWeight.Normal,
        fontStyle = if (block.italic) FontStyle.Italic else FontStyle.Normal,
        textDecoration = if (block.underline) TextDecoration.Underline else TextDecoration.None,
        textAlign = block.alignment.toComposeTextAlign()
    )

    Card(shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Icon(Icons.Default.TextFields, contentDescription = null)
                Text("Text", modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelLarge)
                IconButton(onClick = { viewModel.updateBlock(note, block.copy(bold = !block.bold)) }) {
                    Icon(Icons.Default.FormatBold, "Bold")
                }
                IconButton(onClick = { viewModel.updateBlock(note, block.copy(italic = !block.italic)) }) {
                    Icon(Icons.Default.FormatItalic, "Italic")
                }
                IconButton(onClick = { viewModel.updateBlock(note, block.copy(underline = !block.underline)) }) {
                    Icon(Icons.Default.FormatUnderlined, "Underline")
                }
                Box {
                    IconButton(onClick = { colorMenuOpen = true }) {
                        Icon(Icons.Default.Palette, "Color")
                    }
                    DropdownMenu(expanded = colorMenuOpen, onDismissRequest = { colorMenuOpen = false }) {
                        listOf(
                            "Ink" to 0xFF2B2A27,
                            "Blue" to 0xFF1D4ED8,
                            "Green" to 0xFF15803D,
                            "Red" to 0xFFB91C1C,
                            "Purple" to 0xFF7E22CE
                        ).forEach { (name, color) ->
                            DropdownMenuItem(
                                text = { Text(name) },
                                leadingIcon = {
                                    Box(
                                        Modifier
                                            .size(18.dp)
                                            .background(Color(color), CircleShape)
                                    )
                                },
                                onClick = {
                                    colorMenuOpen = false
                                    viewModel.updateBlock(note, block.copy(color = color))
                                }
                            )
                        }
                    }
                }
                Box {
                    IconButton(onClick = { highlightMenuOpen = true }) {
                        Icon(Icons.Default.FormatColorFill, "Highlight")
                    }
                    DropdownMenu(expanded = highlightMenuOpen, onDismissRequest = { highlightMenuOpen = false }) {
                        listOf(
                            "None" to 0x00FFFFFFL,
                            "Yellow" to 0xFFFFF59DL,
                            "Green" to 0xFFBBF7D0L,
                            "Blue" to 0xFFBFDBFEL,
                            "Pink" to 0xFFFBCFE8L
                        ).forEach { (name, color) ->
                            DropdownMenuItem(
                                text = { Text(name) },
                                leadingIcon = {
                                    Box(
                                        Modifier
                                            .size(18.dp)
                                            .background(Color(color), CircleShape)
                                            .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                                    )
                                },
                                onClick = {
                                    highlightMenuOpen = false
                                    viewModel.updateBlock(note, block.copy(highlight = color))
                                }
                            )
                        }
                    }
                }
                IconButton(onClick = { viewModel.removeBlock(note, block) }) {
                    Icon(Icons.Default.Delete, "Delete text block")
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                listOf(14, 18, 22, 28).forEach { size ->
                    FilterChip(
                        selected = block.sizeSp == size,
                        onClick = { viewModel.updateBlock(note, block.copy(sizeSp = size)) },
                        label = { Text("${size}sp") }
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("Align", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                TextAlignment.entries.forEach { alignment ->
                    FilterChip(
                        selected = block.alignment == alignment,
                        onClick = { viewModel.updateBlock(note, block.copy(alignment = alignment)) },
                        label = { Text(alignment.label) },
                        leadingIcon = {
                            Icon(
                                when (alignment) {
                                    TextAlignment.Start -> Icons.Default.FormatAlignLeft
                                    TextAlignment.Center -> Icons.Default.FormatAlignCenter
                                    TextAlignment.End -> Icons.Default.FormatAlignRight
                                },
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    )
                }
            }
            BasicTextField(
                value = block.text,
                onValueChange = { viewModel.updateBlock(note, block.copy(text = it)) },
                textStyle = textStyle,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(
                        if (block.highlight == 0x00FFFFFFL) Color(note.paperColor) else Color(block.highlight),
                        RoundedCornerShape(6.dp)
                    )
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(6.dp))
                    .padding(12.dp),
                decorationBox = { inner ->
                    if (block.text.isBlank()) {
                        Text("Start writing...", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    inner()
                }
            )
        }
    }
}

@Composable
fun ChecklistBlockEditor(note: SNote, block: NoteBlock.Checklist, viewModel: NotesViewModel) {
    val progress = block.progress()
    Card(shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CheckBox, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Checklist", modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelLarge)
                Text(progress.label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.width(8.dp))
                IconButton(onClick = {
                    viewModel.updateBlock(note, block.copy(items = block.items + CheckItem(text = "")))
                }) { Icon(Icons.Default.Add, "Add item") }
                IconButton(onClick = { viewModel.removeBlock(note, block) }) {
                    Icon(Icons.Default.Delete, "Delete checklist")
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = { viewModel.updateBlock(note, block.setAllChecked(true)) }) {
                    Text("Complete all")
                }
                TextButton(onClick = { viewModel.updateBlock(note, block.setAllChecked(false)) }) {
                    Text("Uncheck all")
                }
                TextButton(
                    enabled = block.items.any { it.checked },
                    onClick = { viewModel.updateBlock(note, block.clearCompleted()) }
                ) {
                    Text("Clear done")
                }
            }
            block.items.forEachIndexed { index, item ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = item.checked,
                        onCheckedChange = { checked ->
                            viewModel.updateBlock(
                                note,
                                block.copy(items = block.items.map { if (it.id == item.id) it.copy(checked = checked) else it })
                            )
                        }
                    )
                    Column {
                        IconButton(
                            enabled = index > 0,
                            onClick = { viewModel.updateBlock(note, block.moveItem(item.id, -1)) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Text("↑")
                        }
                        IconButton(
                            enabled = index < block.items.lastIndex,
                            onClick = { viewModel.updateBlock(note, block.moveItem(item.id, 1)) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Text("↓")
                        }
                    }
                    BasicTextField(
                        value = item.text,
                        onValueChange = { text ->
                            viewModel.updateBlock(
                                note,
                                block.copy(items = block.items.map { if (it.id == item.id) it.copy(text = text) else it })
                            )
                        },
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            color = if (item.checked) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                            textDecoration = if (item.checked) TextDecoration.LineThrough else TextDecoration.None
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 8.dp),
                        decorationBox = { inner ->
                            if (item.text.isBlank()) Text("Item", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            inner()
                        }
                    )
                    IconButton(onClick = {
                        viewModel.updateBlock(note, block.copy(items = block.items.filterNot { it.id == item.id }))
                    }) {
                        Icon(Icons.Default.Delete, "Delete item", modifier = Modifier.size(18.dp))
                    }
                }
                Divider()
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DrawingBlockEditor(note: SNote, block: NoteBlock.Drawing, viewModel: NotesViewModel) {
    var selectedColor by remember(block.id) { mutableStateOf(block.penColor) }
    var selectedTool by remember(block.id) { mutableStateOf(block.activeTool) }
    var selectedWidth by remember(block.id) { mutableStateOf(block.strokeWidth) }
    var currentStroke by remember { mutableStateOf<DrawStroke?>(null) }
    var eraserPoints by remember { mutableStateOf<List<DrawPoint>>(emptyList()) }
    val strokes = block.strokes + listOfNotNull(currentStroke)

    Card(shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Brush, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Handwriting and drawing", modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelLarge)
                TextButton(
                    enabled = block.strokes.isNotEmpty(),
                    onClick = { viewModel.updateBlock(note, block.copy(strokes = block.strokes.dropLast(1))) }
                ) {
                    Text("Undo")
                }
                IconButton(onClick = { viewModel.updateBlock(note, block.copy(strokes = emptyList())) }) {
                    Icon(Icons.Default.FormatColorFill, "Clear drawing")
                }
                IconButton(onClick = { viewModel.removeBlock(note, block) }) {
                    Icon(Icons.Default.Delete, "Delete drawing")
                }
            }
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                DrawTool.entries.forEach { tool ->
                    FilterChip(
                        selected = selectedTool == tool,
                        onClick = {
                            selectedTool = tool
                            viewModel.updateBlock(note, block.copy(activeTool = tool))
                        },
                        label = { Text(tool.label) }
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(0xFF1D4ED8, 0xFF111827, 0xFFB91C1C, 0xFF15803D, 0xFFF59E0B).forEach { color ->
                    Box(
                        Modifier
                            .size(28.dp)
                            .background(Color(color), CircleShape)
                            .border(
                                width = if (selectedColor == color) 3.dp else 1.dp,
                                color = MaterialTheme.colorScheme.onSurface,
                                shape = CircleShape
                            )
                            .clickable {
                                selectedColor = color
                                viewModel.updateBlock(note, block.copy(penColor = color, activeTool = selectedTool))
                            }
                    )
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Stroke", style = MaterialTheme.typography.bodySmall)
                Slider(
                    value = selectedWidth,
                    onValueChange = { selectedWidth = it },
                    onValueChangeFinished = {
                        viewModel.updateBlock(note, block.copy(strokeWidth = selectedWidth, activeTool = selectedTool))
                    },
                    valueRange = 2f..18f,
                    steps = 7,
                    modifier = Modifier.weight(1f)
                )
                Text("${selectedWidth.toInt()}px", style = MaterialTheme.typography.bodySmall)
            }
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .background(Color(note.paperColor), RoundedCornerShape(8.dp))
                    .border(1.dp, Color(0xFFD6D3C4), RoundedCornerShape(8.dp))
                    .pointerInput(block.id, selectedColor, selectedTool, selectedWidth) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                val point = DrawPoint(offset.x, offset.y)
                                if (selectedTool == DrawTool.Eraser) {
                                    eraserPoints = listOf(point)
                                } else {
                                    currentStroke = DrawStroke(
                                        color = selectedColor,
                                        width = selectedWidth,
                                        tool = selectedTool,
                                        points = listOf(point)
                                    )
                                }
                            },
                            onDrag = { change, _ ->
                                val point = DrawPoint(change.position.x, change.position.y)
                                if (selectedTool == DrawTool.Eraser) {
                                    eraserPoints = eraserPoints + point
                                } else {
                                    val old = currentStroke ?: return@detectDragGestures
                                    currentStroke = old.copy(points = old.points + point)
                                }
                            },
                            onDragEnd = {
                                if (selectedTool == DrawTool.Eraser) {
                                    viewModel.updateBlock(
                                        note,
                                        block.copy(
                                            strokes = block.strokes.eraseNear(eraserPoints, selectedWidth * 2.5f),
                                            activeTool = selectedTool,
                                            strokeWidth = selectedWidth
                                        )
                                    )
                                    eraserPoints = emptyList()
                                } else {
                                    currentStroke?.let { stroke ->
                                        viewModel.updateBlock(
                                            note,
                                            block.copy(
                                                strokes = block.strokes + stroke,
                                                activeTool = selectedTool,
                                                penColor = selectedColor,
                                                strokeWidth = selectedWidth
                                            )
                                        )
                                    }
                                }
                                currentStroke = null
                            },
                            onDragCancel = {
                                currentStroke = null
                                eraserPoints = emptyList()
                            }
                        )
                    }
            ) {
                drawPageTemplate(note.pageTemplate)
                strokes.forEach { stroke ->
                    val strokeColor = if (stroke.tool == DrawTool.Highlighter) {
                        Color(stroke.color).copy(alpha = 0.35f)
                    } else {
                        Color(stroke.color)
                    }
                    val strokeWidth = if (stroke.tool == DrawTool.Fountain) stroke.width * 1.2f else stroke.width
                    if (stroke.points.size == 1) {
                        val point = stroke.points.first()
                        drawCircle(strokeColor, radius = strokeWidth, center = Offset(point.x, point.y))
                    } else {
                        val path = Path().apply {
                            stroke.points.firstOrNull()?.let { moveTo(it.x, it.y) }
                            stroke.points.drop(1).forEach { lineTo(it.x, it.y) }
                        }
                        drawPath(
                            path = path,
                            color = strokeColor,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                    }
                }
            }
        }
    }
}

fun List<DrawStroke>.eraseNear(points: List<DrawPoint>, radius: Float): List<DrawStroke> {
    if (points.isEmpty()) return this
    val radiusSquared = radius * radius
    return flatMap { stroke ->
        stroke.splitAroundErasedPoints(points, radiusSquared)
    }
}

fun DrawStroke.splitAroundErasedPoints(eraserPoints: List<DrawPoint>, radiusSquared: Float): List<DrawStroke> {
    val keptSegments = mutableListOf<List<DrawPoint>>()
    var current = mutableListOf<DrawPoint>()
    points.forEach { strokePoint ->
        val erased = eraserPoints.any { eraserPoint ->
            val dx = strokePoint.x - eraserPoint.x
            val dy = strokePoint.y - eraserPoint.y
            dx * dx + dy * dy <= radiusSquared
        }
        if (erased) {
            if (current.isNotEmpty()) {
                keptSegments += current
                current = mutableListOf()
            }
        } else {
            current += strokePoint
        }
    }
    if (current.isNotEmpty()) keptSegments += current
    if (keptSegments.size == 1 && keptSegments.single().size == points.size) return listOf(this)
    return keptSegments.map { segment ->
        copy(id = UUID.randomUUID().toString(), points = segment)
    }
}

fun DrawScope.drawPageTemplate(template: PageTemplate) {
    val templateColor = Color(0xFFD6D3C4).copy(alpha = 0.75f)
    when (template) {
        PageTemplate.Plain -> Unit
        PageTemplate.Ruled -> {
            var y = 36f
            while (y < size.height) {
                drawLine(templateColor, start = Offset(0f, y), end = Offset(size.width, y), strokeWidth = 1f)
                y += 34f
            }
        }
        PageTemplate.Grid -> {
            var x = 32f
            while (x < size.width) {
                drawLine(templateColor, start = Offset(x, 0f), end = Offset(x, size.height), strokeWidth = 1f)
                x += 32f
            }
            var y = 32f
            while (y < size.height) {
                drawLine(templateColor, start = Offset(0f, y), end = Offset(size.width, y), strokeWidth = 1f)
                y += 32f
            }
        }
        PageTemplate.Dotted -> {
            var y = 28f
            while (y < size.height) {
                var x = 28f
                while (x < size.width) {
                    drawCircle(templateColor, radius = 1.8f, center = Offset(x, y))
                    x += 28f
                }
                y += 28f
            }
        }
    }
}

@Composable
fun AttachmentBlock(note: SNote, block: NoteBlock.Attachment, viewModel: NotesViewModel) {
    Card(shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            if (block.isImageAttachment) {
                val context = LocalContext.current
                val bitmap = remember(block.uri) {
                    runCatching {
                        context.contentResolver.openInputStream(Uri.parse(block.uri))?.use { stream ->
                            BitmapFactory.decodeStream(stream)?.asImageBitmap()
                        }
                    }.getOrNull()
                }
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap,
                        contentDescription = block.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .background(Color(note.paperColor), RoundedCornerShape(8.dp))
                    )
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(if (block.isImageAttachment) Icons.Default.Image else Icons.Default.AttachFile, contentDescription = null)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(block.name, style = MaterialTheme.typography.titleSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(
                        listOf(block.mimeHint, block.sizeLabel).filter { it.isNotBlank() }.joinToString(" • "),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                IconButton(onClick = { viewModel.removeBlock(note, block) }) {
                    Icon(Icons.Default.Delete, "Delete attachment")
                }
            }
        }
    }
}

data class AttachmentMetadata(val name: String, val mimeHint: String, val sizeBytes: Long)

fun queryAttachmentMetadata(context: Context, uri: Uri): AttachmentMetadata {
    var name = uri.lastPathSegment?.substringAfterLast('/') ?: "Attachment"
    var size = 0L
    context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
        if (cursor.moveToFirst()) {
            if (nameIndex >= 0) name = cursor.getString(nameIndex).orEmpty().ifBlank { name }
            if (sizeIndex >= 0 && !cursor.isNull(sizeIndex)) size = cursor.getLong(sizeIndex)
        }
    }
    return AttachmentMetadata(
        name = name,
        mimeHint = context.contentResolver.getType(uri) ?: "file",
        sizeBytes = size
    )
}

val NoteBlock.Attachment.isImageAttachment: Boolean
    get() = mimeHint.startsWith("image/")

val NoteBlock.Attachment.sizeLabel: String
    get() = formatBytes(sizeBytes)

fun formatBytes(sizeBytes: Long): String = when {
    sizeBytes <= 0L -> ""
    sizeBytes < 1024L -> "$sizeBytes B"
    sizeBytes < 1024L * 1024L -> "${sizeBytes / 1024L} KB"
    else -> "${sizeBytes / (1024L * 1024L)} MB"
}

@Composable
fun AudioBlock(note: SNote, block: NoteBlock.Audio, viewModel: NotesViewModel) {
    var playing by remember(block.path) { mutableStateOf(false) }
    val player = remember(block.path) { MediaPlayer() }
    DisposableEffect(block.path) {
        onDispose {
            runCatching {
                if (player.isPlaying) player.stop()
                player.release()
            }
        }
    }
    Card(shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            FilledIconButton(
                onClick = {
                    if (playing) {
                        runCatching {
                            player.stop()
                            player.reset()
                        }
                        playing = false
                    } else {
                        runCatching {
                            player.reset()
                            player.setDataSource(block.path)
                            player.setOnCompletionListener { playing = false }
                            player.prepare()
                            player.start()
                            playing = true
                        }.onFailure {
                            playing = false
                        }
                    }
                }
            ) {
                Icon(if (playing) Icons.Default.Stop else Icons.Default.PlayArrow, if (playing) "Stop audio" else "Play audio")
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(block.name, style = MaterialTheme.typography.titleSmall)
                Text(
                    listOf(formatDuration(block.durationHintMs), block.path).filter { it.isNotBlank() }.joinToString(" • "),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            IconButton(onClick = { viewModel.removeBlock(note, block) }) {
                Icon(Icons.Default.Delete, "Delete audio")
            }
        }
    }
}

fun formatDuration(durationMs: Long): String {
    if (durationMs <= 0L) return ""
    val totalSeconds = durationMs / 1000L
    val minutes = totalSeconds / 60L
    val seconds = totalSeconds % 60L
    return "%d:%02d".format(minutes, seconds)
}

fun SNote.searchMatches(query: String, scope: SearchScope = SearchScope.All): List<SearchMatch> {
    val normalized = query.trim()
    if (normalized.isBlank()) return emptyList()
    val matches = buildList {
        if (scope.includes(SearchScope.Title) && title.contains(normalized, ignoreCase = true)) {
            add(SearchMatch(SearchScope.Title, "Title: $title"))
        }
        if (scope.includes(SearchScope.Folders) && folder.contains(normalized, ignoreCase = true)) {
            add(SearchMatch(SearchScope.Folders, "Folder: $folder"))
        }
        if (scope.includes(SearchScope.Tags)) {
            tags.filter { it.contains(normalized, ignoreCase = true) }
                .take(3)
                .forEach { add(SearchMatch(SearchScope.Tags, "Tag: #$it")) }
        }
        if (!locked && scope.includes(SearchScope.Content)) {
            blocks.flatMap { it.contentSearchLabels(normalized) }
                .take(4)
                .forEach { add(SearchMatch(SearchScope.Content, it)) }
        }
        if (!locked && scope.includes(SearchScope.Attachments)) {
            blocks.flatMap { it.attachmentSearchLabels(normalized) }
                .take(4)
                .forEach { add(SearchMatch(SearchScope.Attachments, it)) }
        }
    }
    return matches.distinctBy { it.scope to it.label }
}

fun SearchScope.includes(candidate: SearchScope): Boolean =
    this == SearchScope.All || this == candidate

fun NoteBlock.contentSearchLabels(query: String): List<String> = when (this) {
    is NoteBlock.Text -> if (text.contains(query, ignoreCase = true)) listOf("Text: ${text.searchSnippet(query)}") else emptyList()
    is NoteBlock.Checklist -> items
        .filter { it.text.contains(query, ignoreCase = true) }
        .map { "Checklist: ${it.text.searchSnippet(query)}" }
    else -> emptyList()
}

fun NoteBlock.attachmentSearchLabels(query: String): List<String> = when (this) {
    is NoteBlock.Attachment -> if (
        name.contains(query, ignoreCase = true) ||
        mimeHint.contains(query, ignoreCase = true)
    ) {
        listOf("File: $name")
    } else {
        emptyList()
    }
    is NoteBlock.Audio -> if (name.contains(query, ignoreCase = true) || path.contains(query, ignoreCase = true)) {
        listOf("Audio: $name")
    } else {
        emptyList()
    }
    else -> emptyList()
}

fun String.searchSnippet(query: String, radius: Int = 28): String {
    val index = indexOf(query, ignoreCase = true)
    if (index < 0) return take(radius * 2)
    val start = (index - radius).coerceAtLeast(0)
    val end = (index + query.length + radius).coerceAtMost(length)
    val prefix = if (start > 0) "..." else ""
    val suffix = if (end < length) "..." else ""
    return prefix + substring(start, end).trim() + suffix
}

fun List<SNote>.updateByIds(ids: Set<String>, transform: (SNote) -> SNote): List<SNote> =
    map { note -> if (note.id in ids) transform(note) else note }

fun List<SNote>.deleteByIds(ids: Set<String>): List<SNote> =
    filterNot { it.id in ids }

fun NoteBlock.Checklist.progress(): ChecklistProgress =
    ChecklistProgress(done = items.count { it.checked }, total = items.size)

fun NoteBlock.Checklist.moveItem(itemId: String, direction: Int): NoteBlock.Checklist {
    val index = items.indexOfFirst { it.id == itemId }
    val target = index + direction
    if (index !in items.indices || target !in items.indices) return this
    val updated = items.toMutableList()
    val item = updated.removeAt(index)
    updated.add(target, item)
    return copy(items = updated)
}

fun NoteBlock.Checklist.clearCompleted(): NoteBlock.Checklist =
    copy(items = items.filterNot { it.checked }.ifEmpty { listOf(CheckItem(text = "")) })

fun NoteBlock.Checklist.setAllChecked(checked: Boolean): NoteBlock.Checklist =
    copy(items = items.map { it.copy(checked = checked) })

class AudioRecorder(private val context: Context) {
    private var recorder: MediaRecorder? = null
    private var outputFile: File? = null

    fun start(): File? = runCatching {
        val audioDir = File(context.filesDir, "audio").apply { mkdirs() }
        val file = File(audioDir, "recording-${System.currentTimeMillis()}.m4a")
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(file.absolutePath)
            prepare()
            start()
        }
        outputFile = file
        file
    }.getOrNull()

    fun stop(): File? = runCatching {
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
        outputFile
    }.getOrNull()
}

fun SNote.toJson(): JSONObject = JSONObject()
    .put("id", id)
    .put("title", title)
    .put("folder", folder)
    .put("tags", JSONArray(tags))
    .put("blocks", JSONArray().also { array -> blocks.forEach { array.put(it.toJson()) } })
    .put("pinned", pinned)
    .put("favorite", favorite)
    .put("locked", locked)
    .put("deleted", deleted)
    .put("pageTemplate", pageTemplate.name)
    .put("paperColor", paperColor)
    .put("createdAt", createdAt)
    .put("updatedAt", updatedAt)

fun notesToBackupJson(notes: List<SNote>): String = JSONObject()
    .put("schemaVersion", 1)
    .put("exportedAt", System.currentTimeMillis())
    .put("notes", JSONArray().also { array -> notes.forEach { array.put(it.toBackupJson()) } })
    .toString(2)

fun notesFromBackupJson(rawBackup: String): List<SNote> = runCatching {
    val trimmed = rawBackup.trim()
    val notesArray = if (trimmed.startsWith("[")) {
        JSONArray(trimmed)
    } else {
        JSONObject(trimmed).optJSONArray("notes") ?: JSONArray()
    }
    notesArray.toNotes()
}.getOrDefault(emptyList())

fun JSONArray.toNotes(): List<SNote> = buildList {
    for (i in 0 until length()) {
        val noteJson = optJSONObject(i) ?: continue
        add(noteJson.toNote())
    }
}

fun NoteBlock.toJson(): JSONObject {
    val json = JSONObject().put("id", id)
    return when (this) {
        is NoteBlock.Text -> json
            .put("type", "text")
            .put("text", text)
            .put("bold", bold)
            .put("italic", italic)
            .put("underline", underline)
            .put("color", color)
            .put("highlight", highlight)
            .put("sizeSp", sizeSp)
            .put("alignment", alignment.name)

        is NoteBlock.Checklist -> json
            .put("type", "checklist")
            .put("items", JSONArray().also { array ->
                items.forEach { item ->
                    array.put(JSONObject().put("id", item.id).put("text", item.text).put("checked", item.checked))
                }
            })

        is NoteBlock.Drawing -> json
            .put("type", "drawing")
            .put("activeTool", activeTool.name)
            .put("penColor", penColor)
            .put("strokeWidth", strokeWidth.toDouble())
            .put("strokes", JSONArray().also { strokeArray ->
                strokes.forEach { stroke ->
                    strokeArray.put(
                        JSONObject()
                            .put("id", stroke.id)
                            .put("color", stroke.color)
                            .put("width", stroke.width.toDouble())
                            .put("tool", stroke.tool.name)
                            .put("points", JSONArray().also { points ->
                                stroke.points.forEach { point ->
                                    points.put(JSONObject().put("x", point.x.toDouble()).put("y", point.y.toDouble()))
                                }
                            })
                    )
                }
            })

        is NoteBlock.Attachment -> json
            .put("type", "attachment")
            .put("uri", uri)
            .put("name", name)
            .put("mimeHint", mimeHint)
            .put("sizeBytes", sizeBytes)

        is NoteBlock.Audio -> json
            .put("type", "audio")
            .put("path", path)
            .put("name", name)
            .put("durationHintMs", durationHintMs)
    }
}

fun JSONObject.toNote(): SNote = SNote(
    id = optString("id", UUID.randomUUID().toString()),
    title = optString("title", "Untitled note"),
    folder = optString("folder", "All notes"),
    tags = optJSONArray("tags").toStringList(),
    blocks = optJSONArray("blocks").toBlocks().ifEmpty { listOf(NoteBlock.Text()) },
    pinned = optBoolean("pinned", false),
    favorite = optBoolean("favorite", false),
    locked = optBoolean("locked", false),
    deleted = optBoolean("deleted", false),
    pageTemplate = optString("pageTemplate").toPageTemplate(PageTemplate.Plain),
    paperColor = optLong("paperColor", 0xFFFFFBF0),
    createdAt = optLong("createdAt", System.currentTimeMillis()),
    updatedAt = optLong("updatedAt", System.currentTimeMillis())
)

fun JSONArray?.toStringList(): List<String> {
    if (this == null) return emptyList()
    return buildList {
        for (i in 0 until length()) add(optString(i))
    }.filter { it.isNotBlank() }
}

fun JSONArray?.toBlocks(): List<NoteBlock> {
    if (this == null) return emptyList()
    return buildList {
        for (i in 0 until length()) {
            val json = optJSONObject(i) ?: continue
            add(json.toBlock())
        }
    }
}

fun JSONObject.toBlock(): NoteBlock = when (optString("type")) {
    "checklist" -> NoteBlock.Checklist(
        id = optString("id", UUID.randomUUID().toString()),
        items = optJSONArray("items").toCheckItems().ifEmpty { listOf(CheckItem(text = "")) }
    )

    "drawing" -> NoteBlock.Drawing(
        id = optString("id", UUID.randomUUID().toString()),
        activeTool = optString("activeTool").toDrawTool(DrawTool.Pen),
        penColor = optLong("penColor", 0xFF1D4ED8),
        strokeWidth = optDouble("strokeWidth", 5.0).toFloat(),
        strokes = optJSONArray("strokes").toStrokes()
    )

    "attachment" -> NoteBlock.Attachment(
        id = optString("id", UUID.randomUUID().toString()),
        uri = optString("uri"),
        name = optString("name", "Attachment"),
        mimeHint = optString("mimeHint", "file"),
        sizeBytes = optLong("sizeBytes", 0L)
    )

    "audio" -> NoteBlock.Audio(
        id = optString("id", UUID.randomUUID().toString()),
        path = optString("path"),
        name = optString("name", "Recording"),
        durationHintMs = optLong("durationHintMs", 0L)
    )

    else -> NoteBlock.Text(
        id = optString("id", UUID.randomUUID().toString()),
        text = optString("text"),
        bold = optBoolean("bold", false),
        italic = optBoolean("italic", false),
        underline = optBoolean("underline", false),
        color = optLong("color", 0xFF2B2A27),
        highlight = optLong("highlight", 0x00FFFFFF),
        sizeSp = optInt("sizeSp", 18),
        alignment = optString("alignment").toTextAlignment(TextAlignment.Start)
    )
}

fun JSONArray?.toCheckItems(): List<CheckItem> {
    if (this == null) return emptyList()
    return buildList {
        for (i in 0 until length()) {
            val item = optJSONObject(i) ?: continue
            add(
                CheckItem(
                    id = item.optString("id", UUID.randomUUID().toString()),
                    text = item.optString("text"),
                    checked = item.optBoolean("checked", false)
                )
            )
        }
    }
}

fun JSONArray?.toStrokes(): List<DrawStroke> {
    if (this == null) return emptyList()
    return buildList {
        for (i in 0 until length()) {
            val stroke = optJSONObject(i) ?: continue
            add(
                DrawStroke(
                    id = stroke.optString("id", UUID.randomUUID().toString()),
                    color = stroke.optLong("color", 0xFF1D4ED8),
                    width = stroke.optDouble("width", 5.0).toFloat(),
                    tool = stroke.optString("tool").toDrawTool(DrawTool.Pen),
                    points = stroke.optJSONArray("points").toPoints()
                )
            )
        }
    }
}

fun String.toDrawTool(default: DrawTool): DrawTool =
    DrawTool.entries.firstOrNull { it.name.equals(this, ignoreCase = true) } ?: default

fun String.toTextAlignment(default: TextAlignment): TextAlignment =
    TextAlignment.entries.firstOrNull { it.name.equals(this, ignoreCase = true) } ?: default

fun String.toPageTemplate(default: PageTemplate): PageTemplate =
    PageTemplate.entries.firstOrNull { it.name.equals(this, ignoreCase = true) } ?: default

fun JSONArray?.toPoints(): List<DrawPoint> {
    if (this == null) return emptyList()
    return buildList {
        for (i in 0 until length()) {
            val point = optJSONObject(i) ?: continue
            add(DrawPoint(point.optDouble("x").toFloat(), point.optDouble("y").toFloat()))
        }
    }
}
