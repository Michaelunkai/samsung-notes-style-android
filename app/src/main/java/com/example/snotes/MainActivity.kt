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
import android.os.Build
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.FormatAlignLeft
import androidx.compose.material.icons.automirrored.filled.FormatAlignRight
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FormatAlignCenter
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatColorFill
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.FormatUnderlined
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import java.io.File
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
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
const val EXTRA_OPEN_NOTE_ID = "com.example.snotes.extra.OPEN_NOTE_ID"
const val SETTINGS_STORE = "notes_settings"
const val SETTING_DARK_MODE = "dark_mode"
const val SETTING_DEFAULT_NOTE_KIND = "default_note_kind"
const val SETTING_DEFAULT_PAGE_TEMPLATE = "default_page_template"
const val SETTING_DEFAULT_PAPER_COLOR = "default_paper_color"
const val SETTING_NOTE_PIN_DIGEST = "note_pin_digest"
const val SETTING_SEARCH_SCOPE = "search_scope"
const val SETTING_SORT_MODE = "sort_mode"
const val SETTING_VIEW_MODE = "view_mode"
const val NOTE_PIN_SALT = "s-notes-style-local-pin-v1"
const val NOTE_HISTORY_LIMIT = 50
const val BACKUP_SCHEMA_VERSION = 2
const val BACKUP_APP_ID = "com.example.snotes"

data class NoteLaunchRequest(
    val sharedText: String? = null,
    val sharedAttachments: List<SharedAttachmentRequest> = emptyList(),
    val quickNoteKind: NewNoteKind? = null,
    val openNoteId: String? = null
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
            ?: blocks.firstOrNull { it is NoteBlock.Sticky && it.text.isNotBlank() }
                ?.let { (it as NoteBlock.Sticky).text }
            ?: blocks.firstOrNull()?.label.orEmpty()
}

fun SNote.editableContentEquals(other: SNote): Boolean =
    id == other.id &&
        title == other.title &&
        folder == other.folder &&
        tags == other.tags &&
        blocks == other.blocks &&
        pinned == other.pinned &&
        favorite == other.favorite &&
        locked == other.locked &&
        deleted == other.deleted &&
        pageTemplate == other.pageTemplate &&
        paperColor == other.paperColor

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
        val fontFamily: NoteFontFamily = NoteFontFamily.Default,
        val alignment: TextAlignment = TextAlignment.Start
    ) : NoteBlock(id, "Text")

    data class Checklist(
        override val id: String = UUID.randomUUID().toString(),
        val items: List<CheckItem> = listOf(CheckItem(text = "Checklist item"))
    ) : NoteBlock(id, "Checklist")

    data class Sticky(
        override val id: String = UUID.randomUUID().toString(),
        val text: String = "",
        val color: Long = 0xFFFFF59D,
        val collapsed: Boolean = false
    ) : NoteBlock(id, "Sticky note")

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
        val durationHintMs: Long = 0L,
        val markers: List<AudioMarker> = emptyList()
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

data class AudioMarker(
    val id: String = UUID.randomUUID().toString(),
    val label: String,
    val timestampMs: Long
)

data class NoteDetails(
    val blockCount: Int,
    val wordCount: Int,
    val checklistItems: Int,
    val completedChecklistItems: Int,
    val stickyNotes: Int,
    val drawingStrokes: Int,
    val attachments: Int,
    val audioBlocks: Int,
    val audioMarkers: Int
) {
    val blockLabel: String
        get() = "$blockCount block${if (blockCount == 1) "" else "s"}"

    val wordLabel: String
        get() = "$wordCount word${if (wordCount == 1) "" else "s"}"

    val checklistLabel: String
        get() = if (checklistItems == 0) "No checklist items" else "$completedChecklistItems/$checklistItems done"

    val inkLabel: String
        get() = if (drawingStrokes == 0) "No ink strokes" else "$drawingStrokes stroke${if (drawingStrokes == 1) "" else "s"}"

    val attachmentLabel: String
        get() = if (attachments == 0) "No attachments" else "$attachments file${if (attachments == 1) "" else "s"}"

    val audioLabel: String
        get() = when {
            audioBlocks == 0 -> "No audio"
            audioMarkers == 0 -> "$audioBlocks recording${if (audioBlocks == 1) "" else "s"}"
            else -> "$audioBlocks recording${if (audioBlocks == 1) "" else "s"} • $audioMarkers marker${if (audioMarkers == 1) "" else "s"}"
        }
}

data class SearchMatch(val scope: SearchScope, val label: String)

data class EditorSearchMatch(
    val blockId: String?,
    val label: String,
    val snippet: String
)

data class EmptyNotesCopy(
    val title: String,
    val subtitle: String,
    val actionLabel: String? = null
)

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

enum class NoteFontFamily(val label: String) {
    Default("Default"),
    Sans("Sans"),
    Serif("Serif"),
    Mono("Mono"),
    Cursive("Cursive")
}

fun TextAlignment.toComposeTextAlign(): TextAlign = when (this) {
    TextAlignment.Start -> TextAlign.Start
    TextAlignment.Center -> TextAlign.Center
    TextAlignment.End -> TextAlign.End
}

fun NoteFontFamily.toComposeFontFamily(): FontFamily? = when (this) {
    NoteFontFamily.Default -> null
    NoteFontFamily.Sans -> FontFamily.SansSerif
    NoteFontFamily.Serif -> FontFamily.Serif
    NoteFontFamily.Mono -> FontFamily.Monospace
    NoteFontFamily.Cursive -> FontFamily.Cursive
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

val STICKY_NOTE_COLORS = listOf(
    0xFFFFF59DL,
    0xFFBBF7D0L,
    0xFFBFDBFEL,
    0xFFFBCFE8L,
    0xFFE9D5FFL
)

data class NoteDefaults(
    val newNoteKind: NewNoteKind = NewNoteKind.Text,
    val pageTemplate: PageTemplate = PageTemplate.Plain,
    val paperColor: Long = DEFAULT_PAPER_COLORS.first()
)

fun noteDefaultsFromStoredValues(kindName: String?, templateName: String?, paperColor: Long): NoteDefaults =
    NoteDefaults(
        newNoteKind = newNoteKindFromStoredValue(kindName),
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

fun removeTags(existing: List<String>, removed: String): List<String> {
    val targets = parseTagInput(removed).toSet()
    if (targets.isEmpty()) return existing
    return existing.filterNot { it in targets }
}

fun renameFolderPath(folder: String, from: String, to: String): String {
    val source = normalizeFolder(from)
    val target = normalizeFolder(to)
    return when {
        folder == source -> target
        folder.startsWith("$source/") -> target + folder.removePrefix(source)
        else -> folder
    }
}

fun List<SNote>.removeFolderFromNotes(folder: String): List<SNote> {
    val target = normalizeFolder(folder)
    return map { note ->
        if (note.folder == target || note.folder.startsWith("$target/")) note.copy(folder = "All notes") else note
    }
}

fun renameTagList(tags: List<String>, from: String, to: String): List<String> {
    val source = from.trim().removePrefix("#")
    val target = parseTagInput(to).firstOrNull() ?: return tags
    return tags.map { tag -> if (tag == source) target else tag }.distinct()
}

fun List<SNote>.removeTagFromNotes(tag: String): List<SNote> {
    val target = parseTagInput(tag).firstOrNull() ?: return this
    return map { note ->
        if (target in note.tags) note.copy(tags = removeTags(note.tags, target)) else note
    }
}

fun SharedPreferences.loadNoteDefaults(): NoteDefaults =
    noteDefaultsFromStoredValues(
        kindName = getString(SETTING_DEFAULT_NOTE_KIND, NewNoteKind.Text.name),
        templateName = getString(SETTING_DEFAULT_PAGE_TEMPLATE, PageTemplate.Plain.name),
        paperColor = getLong(SETTING_DEFAULT_PAPER_COLOR, DEFAULT_PAPER_COLORS.first())
    )

fun newNoteKindFromStoredValue(value: String?): NewNoteKind =
    NewNoteKind.entries.firstOrNull { it.name.equals(value.orEmpty(), ignoreCase = true) }
        ?: NewNoteKind.Text

fun sortModeFromStoredValue(value: String?): NoteSortMode =
    NoteSortMode.entries.firstOrNull { it.name.equals(value.orEmpty(), ignoreCase = true) }
        ?: NoteSortMode.ModifiedNewest

fun viewModeFromStoredValue(value: String?): NoteViewMode =
    NoteViewMode.entries.firstOrNull { it.name.equals(value.orEmpty(), ignoreCase = true) }
        ?: NoteViewMode.List

fun searchScopeFromStoredValue(value: String?): SearchScope =
    SearchScope.entries.firstOrNull { it.name.equals(value.orEmpty(), ignoreCase = true) }
        ?: SearchScope.All

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
    val unlockedNoteIds: Set<String> = emptySet(),
    val undoAvailableNoteIds: Set<String> = emptySet(),
    val redoAvailableNoteIds: Set<String> = emptySet()
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

    val librarySummaryLabel: String
        get() = buildList {
            add("${visibleNotes.size} notes")
            if (search.isNotBlank()) add(searchScope.label)
            add(sortMode.label)
            add(viewMode.label)
        }.joinToString(" • ")

    val isSelectionMode: Boolean
        get() = selectedNoteIds.isNotEmpty()

    val allVisibleNotesSelected: Boolean
        get() = visibleNotes.isNotEmpty() && visibleNotes.all { it.id in selectedNoteIds }

    val selectedNotesIncludePinned: Boolean
        get() = selectedNotes.any { it.pinned }

    val selectedNotesIncludeUnpinned: Boolean
        get() = selectedNotes.any { !it.pinned }

    val selectedNotesIncludeFavorite: Boolean
        get() = selectedNotes.any { it.favorite }

    val selectedNotesIncludeNonFavorite: Boolean
        get() = selectedNotes.any { !it.favorite }

    val selectedNotesIncludeLocked: Boolean
        get() = selectedNotes.any { it.locked }

    val selectedNotesIncludeUnlocked: Boolean
        get() = selectedNotes.any { !it.locked }

    val folders: List<String>
        get() = notes.filter { !it.deleted }.map { it.folder }.distinct().sorted()

    val rootFolders: List<String>
        get() = folders.map { it.substringBefore("/") }.distinct().sorted()

    val folderSummaries: List<OrganizationSummary>
        get() = folders.map { folder ->
            OrganizationSummary(folder, notes.count { !it.deleted && (it.folder == folder || it.folder.startsWith("$folder/")) })
        }.filter { it.noteCount > 0 }

    val rootFolderSummaries: List<OrganizationSummary>
        get() = rootFolders.map { folder ->
            OrganizationSummary(folder, notes.count { !it.deleted && (it.folder == folder || it.folder.startsWith("$folder/")) })
        }.filter { it.noteCount > 0 }

    val tags: List<String>
        get() = notes.filter { !it.deleted }.flatMap { it.tags }.distinct().sorted()

    val tagSummaries: List<OrganizationSummary>
        get() = tags.map { tag ->
            OrganizationSummary(tag, notes.count { !it.deleted && tag in it.tags })
        }.filter { it.noteCount > 0 }

    val trashCount: Int
        get() = notes.count { it.deleted }

    val favoritesCount: Int
        get() = notes.count { !it.deleted && it.favorite }

    val hasNotePin: Boolean
        get() = !notePinDigest.isNullOrBlank()

    val selectedNoteCanUndo: Boolean
        get() = selectedNoteId in undoAvailableNoteIds

    val selectedNoteCanRedo: Boolean
        get() = selectedNoteId in redoAvailableNoteIds
}

data class OrganizationSummary(val name: String, val noteCount: Int) {
    val label: String
        get() = "$name $noteCount"
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
        "Modified newest",
        compareByDescending<SNote> { it.pinned }.thenByDescending { it.favorite }.thenByDescending { it.updatedAt }
    ),
    ModifiedOldest(
        "Modified oldest",
        compareByDescending<SNote> { it.pinned }.thenByDescending { it.favorite }.thenBy { it.updatedAt }
    ),
    CreatedNewest(
        "Created newest",
        compareByDescending<SNote> { it.pinned }.thenByDescending { it.favorite }.thenByDescending { it.createdAt }
    ),
    CreatedOldest(
        "Created oldest",
        compareByDescending<SNote> { it.pinned }.thenByDescending { it.favorite }.thenBy { it.createdAt }
    ),
    TitleAscending(
        "Title A-Z",
        compareByDescending<SNote> { it.pinned }.thenByDescending { it.favorite }.thenBy { it.title.lowercase() }
    ),
    TitleDescending(
        "Title Z-A",
        compareByDescending<SNote> { it.pinned }.thenByDescending { it.favorite }.thenByDescending { it.title.lowercase() }
    ),
    FolderAscending(
        "Folder",
        compareByDescending<SNote> { it.pinned }.thenByDescending { it.favorite }.thenBy { it.folder.lowercase() }.thenBy { it.title.lowercase() }
    ),
    ChecklistProgress(
        "Checklist progress",
        compareByDescending<SNote> { it.pinned }
            .thenByDescending { it.favorite }
            .thenByDescending { it.checklistTotalCount() }
            .thenByDescending { it.checklistDoneCount() }
            .thenByDescending { it.updatedAt }
    ),
    MediaHeavy(
        "Media first",
        compareByDescending<SNote> { it.pinned }
            .thenByDescending { it.favorite }
            .thenByDescending { it.mediaBlockCount() }
            .thenByDescending { it.updatedAt }
    )
}

enum class NoteViewMode(val label: String) {
    List("List"),
    Grid("Grid")
}

fun NotesUiState.emptyNotesCopy(): EmptyNotesCopy = when {
    search.isNotBlank() -> EmptyNotesCopy(
        title = "No matching notes",
        subtitle = "Try a different search term or search scope."
    )
    surface == NotesSurface.Trash -> EmptyNotesCopy(
        title = "Trash is empty",
        subtitle = "Deleted notes will stay here until you remove them permanently."
    )
    surface == NotesSurface.Favorites -> EmptyNotesCopy(
        title = "No favorites yet",
        subtitle = "Favorite important notes to find them here quickly.",
        actionLabel = noteDefaults.newNoteKind.title
    )
    surface == NotesSurface.Folders && folderFilter != null -> EmptyNotesCopy(
        title = "No notes in $folderFilter",
        subtitle = "Move notes into this folder or create a new note.",
        actionLabel = noteDefaults.newNoteKind.title
    )
    surface == NotesSurface.Tags && tagFilter != null -> EmptyNotesCopy(
        title = "No notes tagged #$tagFilter",
        subtitle = "Add this tag to a note or start a new one.",
        actionLabel = noteDefaults.newNoteKind.title
    )
    else -> EmptyNotesCopy(
        title = "No notes yet",
        subtitle = "Create a text note, checklist, handwriting page, or imported note.",
        actionLabel = noteDefaults.newNoteKind.title
    )
}

class NotesViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = RoomNoteRepository(application)
    private val settings = application.getSharedPreferences(SETTINGS_STORE, Context.MODE_PRIVATE)
    private val undoStacks = mutableMapOf<String, ArrayDeque<SNote>>()
    private val redoStacks = mutableMapOf<String, ArrayDeque<SNote>>()
    private val _state = MutableStateFlow(
        NotesUiState(
            notes = loadInitialNotes(),
            darkMode = settings.getBoolean(SETTING_DARK_MODE, false),
            noteDefaults = settings.loadNoteDefaults(),
            sortMode = sortModeFromStoredValue(settings.getString(SETTING_SORT_MODE, NoteSortMode.ModifiedNewest.name)),
            viewMode = viewModeFromStoredValue(settings.getString(SETTING_VIEW_MODE, NoteViewMode.List.name)),
            searchScope = searchScopeFromStoredValue(settings.getString(SETTING_SEARCH_SCOPE, SearchScope.All.name)),
            notePinDigest = settings.getString(SETTING_NOTE_PIN_DIGEST, null)
        )
    )
    val state: StateFlow<NotesUiState> = _state

    fun createNote(kind: NewNoteKind) {
        val note = kind.createNoteForState(_state.value)
        _state.update { it.copy(notes = listOf(note) + it.notes, selectedNoteId = note.id) }
        persist()
    }

    fun createSharedTextNote(text: String) {
        createSharedNote(
            title = text.lineSequence().firstOrNull()?.take(48)?.ifBlank { "Shared note" } ?: "Shared note",
            blocks = listOf(NoteBlock.Text(text = text))
        )
    }

    fun createSharedImportNote(sharedText: String?, importedBlocks: List<NoteBlock>) {
        val firstImportedName = importedBlocks.firstOrNull()?.importedName
        val title = sharedText
            ?.lineSequence()
            ?.firstOrNull()
            ?.take(48)
            ?.ifBlank { null }
            ?: firstImportedName?.take(48)
            ?: "Shared file"
        val blocks = buildList {
            if (!sharedText.isNullOrBlank()) add(NoteBlock.Text(text = sharedText))
            addAll(importedBlocks)
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

    fun selectVisibleNotes() {
        _state.update { state ->
            val visibleIds = state.visibleNotes.map { it.id }.toSet()
            state.copy(
                selectedNoteIds = visibleIds,
                selectedNoteId = null,
                statusMessage = "Selected ${visibleIds.size} note${if (visibleIds.size == 1) "" else "s"}"
            )
        }
    }

    fun setSearch(search: String) {
        _state.update { it.copy(search = search) }
    }

    fun setSearchScope(searchScope: SearchScope) {
        _state.update { it.copy(searchScope = searchScope) }
        persistSettings()
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
        persistSettings()
    }

    fun toggleViewMode() {
        _state.update {
            it.copy(viewMode = if (it.viewMode == NoteViewMode.List) NoteViewMode.Grid else NoteViewMode.List)
        }
        persistSettings()
    }

    fun toggleTheme() {
        _state.update { it.copy(darkMode = !it.darkMode) }
        persistSettings()
    }

    fun setDefaultPageTemplate(pageTemplate: PageTemplate) {
        _state.update { it.copy(noteDefaults = it.noteDefaults.copy(pageTemplate = pageTemplate)) }
        persistSettings()
    }

    fun setDefaultNoteKind(newNoteKind: NewNoteKind) {
        _state.update { it.copy(noteDefaults = it.noteDefaults.copy(newNoteKind = newNoteKind)) }
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
            .putString(SETTING_DEFAULT_NOTE_KIND, state.noteDefaults.newNoteKind.name)
            .putString(SETTING_DEFAULT_PAGE_TEMPLATE, state.noteDefaults.pageTemplate.name)
            .putLong(SETTING_DEFAULT_PAPER_COLOR, state.noteDefaults.paperColor)
            .putString(SETTING_NOTE_PIN_DIGEST, state.notePinDigest)
            .putString(SETTING_SEARCH_SCOPE, state.searchScope.name)
            .putString(SETTING_SORT_MODE, state.sortMode.name)
            .putString(SETTING_VIEW_MODE, state.viewMode.name)
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
        val current = _state.value
        val previous = current.notes.firstOrNull { it.id == note.id }
        if (previous != null && !previous.editableContentEquals(note)) {
            undoStacks.getOrPut(note.id) { ArrayDeque() }.apply {
                if (lastOrNull()?.editableContentEquals(previous) != true) addLast(previous)
                while (size > NOTE_HISTORY_LIMIT) removeFirst()
            }
            redoStacks.remove(note.id)
        }
        val updated = note.copy(updatedAt = System.currentTimeMillis())
        _state.update { state ->
            state.copy(
                notes = state.notes.map {
                    if (it.id == note.id) updated else it
                },
                undoAvailableNoteIds = undoStacks.availableNoteIds(),
                redoAvailableNoteIds = redoStacks.availableNoteIds()
            )
        }
        persistNote(updated)
    }

    fun undoNoteEdit(note: SNote) {
        val current = _state.value.notes.firstOrNull { it.id == note.id } ?: return
        val restored = undoStacks[note.id]?.popLastOrNull() ?: return
        redoStacks.getOrPut(note.id) { ArrayDeque() }.addLast(current)
        replaceNoteFromHistory(restored.copy(updatedAt = System.currentTimeMillis()), "Undo")
    }

    fun redoNoteEdit(note: SNote) {
        val current = _state.value.notes.firstOrNull { it.id == note.id } ?: return
        val restored = redoStacks[note.id]?.popLastOrNull() ?: return
        undoStacks.getOrPut(note.id) { ArrayDeque() }.addLast(current)
        replaceNoteFromHistory(restored.copy(updatedAt = System.currentTimeMillis()), "Redo")
    }

    private fun replaceNoteFromHistory(note: SNote, message: String) {
        _state.update { state ->
            state.copy(
                notes = state.notes.map { if (it.id == note.id) note else it },
                selectedNoteId = note.id,
                undoAvailableNoteIds = undoStacks.availableNoteIds(),
                redoAvailableNoteIds = redoStacks.availableNoteIds(),
                statusMessage = message
            )
        }
        persistNote(note)
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

    fun batchDuplicateSelected() {
        _state.update { state ->
            val selected = state.selectedNotes
            if (selected.isEmpty()) {
                state.copy(statusMessage = "No notes selected")
            } else {
                val now = System.currentTimeMillis()
                val duplicates = state.notes.duplicatedNotesByIds(state.selectedNoteIds, now)
                state.copy(
                    notes = duplicates + state.notes,
                    selectedNoteId = duplicates.firstOrNull()?.id,
                    selectedNoteIds = emptySet(),
                    statusMessage = "Duplicated ${duplicates.size} note${if (duplicates.size == 1) "" else "s"}"
                )
            }
        }
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

    fun batchRemoveTagsSelected(tags: String) {
        val parsed = parseTagInput(tags)
        if (parsed.isEmpty()) {
            _state.update { it.copy(statusMessage = "Enter at least one tag") }
            return
        }
        updateSelectedNotes { it.copy(tags = removeTags(it.tags, tags)) }
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

    fun deleteFolder(folder: String) {
        val target = normalizeFolder(folder)
        if (target == "All notes") {
            _state.update { it.copy(statusMessage = "Choose a folder to delete") }
            return
        }
        _state.update { state ->
            state.copy(
                notes = state.notes.removeFolderFromNotes(target),
                surface = NotesSurface.All,
                folderFilter = null,
                selectedNoteIds = emptySet(),
                statusMessage = "Folder $target removed; notes moved to All notes"
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

    fun deleteTag(tag: String) {
        val target = parseTagInput(tag).firstOrNull()
        if (target == null) {
            _state.update { it.copy(statusMessage = "Enter a tag") }
            return
        }
        _state.update { state ->
            state.copy(
                notes = state.notes.removeTagFromNotes(target),
                surface = NotesSurface.All,
                tagFilter = null,
                selectedNoteIds = emptySet(),
                statusMessage = "Tag #$target removed from notes"
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

    fun restoreAllTrash() {
        _state.update { state ->
            state.copy(
                notes = state.notes.restoreTrash(),
                selectedNoteIds = emptySet(),
                statusMessage = "Restored trash"
            )
        }
        persist()
    }

    fun emptyTrash() {
        _state.update { state ->
            state.copy(
                notes = state.notes.deleteTrash(),
                selectedNoteIds = emptySet(),
                selectedNoteId = state.selectedNoteId.takeUnless { selectedId ->
                    state.notes.any { it.id == selectedId && it.deleted }
                },
                statusMessage = "Trash emptied"
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
        val metadata = backupMetadataFromJson(rawBackup)
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
                statusMessage = backupImportStatus(imported.size, metadata)
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

    fun duplicateBlock(note: SNote, block: NoteBlock) {
        updateNote(note.duplicateBlockAfter(block.id))
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

    private fun persistNote(note: SNote) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveNote(note)
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
    Sticky("New sticky note"),
    Drawing("New sketch"),
    Meeting("Meeting note")
}

val NewNoteKind.settingsLabel: String
    get() = when (this) {
        NewNoteKind.Text -> "Text"
        NewNoteKind.Checklist -> "Checklist"
        NewNoteKind.Sticky -> "Sticky"
        NewNoteKind.Drawing -> "Sketch"
        NewNoteKind.Meeting -> "Meeting"
    }

fun NewNoteKind.createNoteWithDefaults(defaults: NoteDefaults = NoteDefaults()): SNote {
    val blocks = when (this) {
        NewNoteKind.Text -> listOf(NoteBlock.Text())
        NewNoteKind.Checklist -> listOf(NoteBlock.Checklist())
        NewNoteKind.Sticky -> listOf(NoteBlock.Sticky())
        NewNoteKind.Drawing -> listOf(NoteBlock.Drawing())
        NewNoteKind.Meeting -> listOf(
            NoteBlock.Text(text = "Date:\nAttendees:\n\nAgenda\n"),
            NoteBlock.Checklist(
                items = listOf(
                    CheckItem(text = "Decisions"),
                    CheckItem(text = "Action items"),
                    CheckItem(text = "Follow up")
                )
            ),
            NoteBlock.Text(text = "Notes\n")
        )
    }
    return SNote(
        title = title,
        blocks = blocks,
        pageTemplate = defaults.pageTemplate,
        paperColor = defaults.paperColor
    )
}

fun NewNoteKind.createNoteForState(state: NotesUiState): SNote {
    val note = createNoteWithDefaults(state.noteDefaults)
    return when {
        state.surface == NotesSurface.Folders && state.folderFilter != null -> note.copy(folder = state.folderFilter)
        state.surface == NotesSurface.Tags && state.tagFilter != null -> note.copy(tags = listOf(state.tagFilter))
        else -> note
    }
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
    is NoteBlock.Sticky -> copy(id = UUID.randomUUID().toString())
    is NoteBlock.Drawing -> copy(
        id = UUID.randomUUID().toString(),
        strokes = strokes.map { stroke -> stroke.copy(id = UUID.randomUUID().toString()) }
    )
    is NoteBlock.Attachment -> copy(id = UUID.randomUUID().toString())
    is NoteBlock.Audio -> copy(
        id = UUID.randomUUID().toString(),
        markers = markers.map { it.copy(id = UUID.randomUUID().toString()) }
    )
}

fun SNote.duplicateBlockAfter(blockId: String): SNote {
    val index = blocks.indexOfFirst { it.id == blockId }
    if (index < 0) return this
    val updated = blocks.toMutableList()
    updated.add(index + 1, blocks[index].duplicateBlock())
    return copy(blocks = updated)
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

fun SNote.details(): NoteDetails = NoteDetails(
    blockCount = blocks.size,
    wordCount = blocks.filterIsInstance<NoteBlock.Text>().sumOf { block ->
        block.text.split(Regex("""\s+""")).count { it.isNotBlank() }
    } + blocks.filterIsInstance<NoteBlock.Sticky>().sumOf { sticky ->
        sticky.text.split(Regex("""\s+""")).count { it.isNotBlank() }
    },
    checklistItems = blocks.filterIsInstance<NoteBlock.Checklist>().sumOf { it.items.size },
    completedChecklistItems = blocks.filterIsInstance<NoteBlock.Checklist>().sumOf { checklist ->
        checklist.items.count { it.checked }
    },
    stickyNotes = blocks.count { it is NoteBlock.Sticky },
    drawingStrokes = blocks.filterIsInstance<NoteBlock.Drawing>().sumOf { it.strokes.size },
    attachments = blocks.count { it is NoteBlock.Attachment },
    audioBlocks = blocks.count { it is NoteBlock.Audio },
    audioMarkers = blocks.filterIsInstance<NoteBlock.Audio>().sumOf { it.markers.size }
)

fun SNote.cardMetaLabel(): String =
    "${formatTimestamp(updatedAt)} • ${blocks.size} block${if (blocks.size == 1) "" else "s"} • $folder"

fun SNote.checklistTotalCount(): Int =
    blocks.filterIsInstance<NoteBlock.Checklist>().sumOf { it.items.size }

fun SNote.checklistDoneCount(): Int =
    blocks.filterIsInstance<NoteBlock.Checklist>().sumOf { checklist ->
        checklist.items.count { it.checked }
    }

fun SNote.mediaBlockCount(): Int =
    blocks.count { it is NoteBlock.Attachment || it is NoteBlock.Audio }

fun SNote.checklistProgressLabel(): String? {
    val total = checklistTotalCount()
    if (total == 0) return null
    val done = checklistDoneCount()
    val itemLabel = if (total == 1) "task" else "tasks"
    return "$done/$total $itemLabel done"
}

fun SNote.mediaCardLabel(): String? {
    val attachments = blocks.count { it is NoteBlock.Attachment }
    val audio = blocks.count { it is NoteBlock.Audio }
    val parts = buildList {
        if (attachments > 0) add("$attachments file${if (attachments == 1) "" else "s"}")
        if (audio > 0) add("$audio audio")
    }
    return parts.takeIf { it.isNotEmpty() }?.joinToString(" • ")
}

fun formatTimestamp(timestamp: Long): String =
    SimpleDateFormat("MMM d, yyyy h:mm a", Locale.getDefault()).format(Date(timestamp))

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
    is NoteBlock.Sticky -> "[Sticky note] ${text.ifBlank { "Empty sticky note" }}"
    is NoteBlock.Checklist -> items.joinToString("\n") { item ->
        "${if (item.checked) "- [x]" else "- [ ]"} ${item.text.ifBlank { "Checklist item" }}"
    }
    is NoteBlock.Drawing -> "[Handwriting: ${strokes.size} stroke${if (strokes.size == 1) "" else "s"}]"
    is NoteBlock.Attachment -> "[Attachment: $name${sizeLabel.takeIf { it.isNotBlank() }?.let { ", $it" }.orEmpty()}]"
    is NoteBlock.Audio -> buildString {
        append("[Audio: $name${formatDuration(durationHintMs).takeIf { it.isNotBlank() }?.let { ", $it" }.orEmpty()}]")
        markers.forEach { marker ->
            appendLine()
            append("- ${formatDuration(marker.timestampMs)} ${marker.label}")
        }
    }
}

val NoteBlock.importedName: String?
    get() = when (this) {
        is NoteBlock.Attachment -> name
        is NoteBlock.Audio -> name
        else -> null
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
        openNoteId = getStringExtra(EXTRA_OPEN_NOTE_ID),
        sharedAttachments = sharedStreamUris().map { uri -> SharedAttachmentRequest(uri.toString(), type) }
    )

fun noteLaunchRequestFrom(
    action: String?,
    mimeType: String?,
    sharedText: String?,
    quickKindName: String?,
    openNoteId: String? = null,
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
    return NoteLaunchRequest(
        sharedText = shared,
        sharedAttachments = attachments,
        quickNoteKind = quickKind,
        openNoteId = openNoteId?.takeIf { it.isNotBlank() }
    )
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
            val importedBlocks = request.sharedAttachments.map { shared ->
                val uri = Uri.parse(shared.uri)
                runCatching {
                    context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                val metadata = queryAttachmentMetadata(context, uri)
                metadata.copy(mimeHint = shared.mimeHint ?: metadata.mimeHint).toNoteBlock(shared.uri)
            }
            viewModel.createSharedImportNote(request.sharedText, importedBlocks)
        } else {
            request.sharedText?.let { viewModel.createSharedTextNote(it) }
        }
        request.quickNoteKind?.let { viewModel.createNote(it) }
        request.openNoteId?.let { viewModel.selectNote(it) }
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
                                text = { Text("Sticky note") },
                                leadingIcon = { Icon(Icons.Default.Description, null) },
                                onClick = {
                                    createMenuOpen = false
                                    viewModel.createNote(NewNoteKind.Sticky)
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
                            DropdownMenuItem(
                                text = { Text("Meeting note") },
                                leadingIcon = { Icon(Icons.Default.Description, null) },
                                onClick = {
                                    createMenuOpen = false
                                    viewModel.createNote(NewNoteKind.Meeting)
                                }
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text(state.noteDefaults.newNoteKind.title) },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                onClick = { viewModel.createNote(state.noteDefaults.newNoteKind) }
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
                trailingIcon = {
                    if (state.search.isNotBlank()) {
                        IconButton(onClick = { viewModel.setSearch("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear search")
                        }
                    }
                },
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
                    state.librarySummaryLabel
                },
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))
            if (state.isSelectionMode) {
                SelectionActionBar(state, viewModel)
                Spacer(Modifier.height(8.dp))
            }
            if (state.visibleNotes.isEmpty()) {
                EmptyNotesState(
                    copy = state.emptyNotesCopy(),
                    onCreateNote = { viewModel.createNote(state.noteDefaults.newNoteKind) }
                )
            } else if (state.viewMode == NoteViewMode.Grid) {
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

@Composable
fun EmptyNotesState(copy: EmptyNotesCopy, onCreateNote: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp, horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            Icons.Default.Description,
            contentDescription = null,
            modifier = Modifier.size(42.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(copy.title, style = MaterialTheme.typography.titleMedium)
        Text(
            copy.subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        copy.actionLabel?.let { label ->
            Button(onClick = onCreateNote) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(6.dp))
                Text(label)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SelectionActionBar(state: NotesUiState, viewModel: NotesViewModel) {
    var moveDialogOpen by remember { mutableStateOf(false) }
    var tagDialogOpen by remember { mutableStateOf(false) }
    var removeTagDialogOpen by remember { mutableStateOf(false) }
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
    if (removeTagDialogOpen) {
        BatchTextActionDialog(
            title = "Remove tags",
            label = "Tags",
            confirmText = "Remove",
            onConfirm = {
                viewModel.batchRemoveTagsSelected(it)
                removeTagDialogOpen = false
            },
            onDismiss = { removeTagDialogOpen = false }
        )
    }
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        if (!state.allVisibleNotesSelected) {
            OutlinedButton(onClick = viewModel::selectVisibleNotes) {
                Text("Select all")
            }
        }
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
            Button(onClick = { removeTagDialogOpen = true }) {
                Text("Untag")
            }
            if (state.selectedNotesIncludeUnpinned) {
                Button(onClick = { viewModel.batchPinSelected(true) }) {
                    Text("Pin")
                }
            }
            if (state.selectedNotesIncludePinned) {
                Button(onClick = { viewModel.batchPinSelected(false) }) {
                    Text("Unpin")
                }
            }
            if (state.selectedNotesIncludeNonFavorite) {
                Button(onClick = { viewModel.batchFavoriteSelected(true) }) {
                    Text("Favorite")
                }
            }
            if (state.selectedNotesIncludeFavorite) {
                Button(onClick = { viewModel.batchFavoriteSelected(false) }) {
                    Text("Unfavorite")
                }
            }
            Button(onClick = viewModel::batchDuplicateSelected) {
                Text("Duplicate")
            }
            if (state.selectedNotesIncludeUnlocked) {
                Button(onClick = { viewModel.batchLockSelected(true) }) {
                    Text("Lock")
                }
            }
            if (state.selectedNotesIncludeLocked) {
                Button(onClick = { viewModel.batchLockSelected(false) }) {
                    Text("Unlock")
                }
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
            state.folderSummaries
        } else {
            state.rootFolderSummaries
        }
        folderChips.forEach { summary ->
            FilterChip(
                selected = state.folderFilter == summary.name,
                onClick = { viewModel.filterFolder(summary.name) },
                label = { Text(summary.label) },
                leadingIcon = { Icon(Icons.Default.Folder, contentDescription = null, modifier = Modifier.size(16.dp)) }
            )
        }
        if (state.surface == NotesSurface.Folders && state.folderFilter != null) {
            val selectedFolder = state.folderFilter
            AssistChip(
                onClick = { folderRenameTarget = selectedFolder },
                label = { Text("Rename folder") },
                leadingIcon = { Icon(Icons.Default.Folder, contentDescription = null, modifier = Modifier.size(16.dp)) }
            )
            AssistChip(
                onClick = { viewModel.deleteFolder(selectedFolder) },
                label = { Text("Delete folder") },
                leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp)) }
            )
        }
        state.tagSummaries.forEach { summary ->
            FilterChip(
                selected = state.tagFilter == summary.name,
                onClick = { viewModel.filterTag(summary.name) },
                label = { Text("#${summary.label}") },
                leadingIcon = { Icon(Icons.Default.Tag, contentDescription = null, modifier = Modifier.size(16.dp)) }
            )
        }
        if (state.surface == NotesSurface.Tags && state.tagFilter != null) {
            val selectedTag = state.tagFilter
            AssistChip(
                onClick = { tagRenameTarget = selectedTag },
                label = { Text("Rename tag") },
                leadingIcon = { Icon(Icons.Default.Tag, contentDescription = null, modifier = Modifier.size(16.dp)) }
            )
            AssistChip(
                onClick = { viewModel.deleteTag(selectedTag) },
                label = { Text("Delete tag") },
                leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp)) }
            )
        }
        if (state.surface == NotesSurface.Trash && state.trashCount > 0) {
            AssistChip(
                onClick = viewModel::restoreAllTrash,
                label = { Text("Restore all") },
                leadingIcon = { Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(16.dp)) }
            )
            AssistChip(
                onClick = viewModel::emptyTrash,
                label = { Text("Empty trash") },
                leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp)) }
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
            Spacer(Modifier.height(4.dp))
            Text(
                note.cardMetaLabel(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelSmall
            )
            if (!note.locked) {
                note.checklistProgressLabel()?.let { progress ->
                    Spacer(Modifier.height(4.dp))
                    Text(
                        progress,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
                note.mediaCardLabel()?.let { media ->
                    Spacer(Modifier.height(4.dp))
                    Text(
                        media,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
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
        if (blocks.any { it is NoteBlock.Sticky }) Icon(Icons.Default.Description, "Sticky note", modifier = Modifier.size(18.dp))
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
    var pendingCameraCapture by remember { mutableStateOf<CameraCaptureTarget?>(null) }
    var detailsOpen by remember { mutableStateOf(false) }
    var editorSearchOpen by remember { mutableStateOf(false) }
    var editorSearchQuery by remember(note.id) { mutableStateOf("") }
    var activeSearchMatch by remember(note.id) { mutableStateOf(0) }
    val editorSearchMatches = remember(note, editorSearchQuery) { note.editorSearchMatches(editorSearchQuery) }
    val editorListState = rememberLazyListState()
    val activeSearchBlockId = editorSearchMatches.getOrNull(activeSearchMatch)?.blockId
    LaunchedEffect(editorSearchQuery, editorSearchMatches.size) {
        activeSearchMatch = activeSearchMatch.coerceIn(0, (editorSearchMatches.size - 1).coerceAtLeast(0))
    }
    LaunchedEffect(editorSearchOpen, activeSearchMatch, editorSearchMatches, note.blocks) {
        if (editorSearchOpen && editorSearchMatches.isNotEmpty()) {
            val targetIndex = editorSearchTargetItemIndex(
                blocks = note.blocks,
                match = editorSearchMatches.getOrNull(activeSearchMatch),
                searchPanelVisible = editorSearchOpen
            )
            editorListState.animateScrollToItem(targetIndex)
        }
    }
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
            metadata.toNoteBlock(uri.toString())
        )
    }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { captured ->
        val target = pendingCameraCapture
        pendingCameraCapture = null
        if (captured && target != null) {
            viewModel.addBlock(note, target.toAttachmentBlock())
            viewModel.setStatus("Photo added")
        } else {
            viewModel.setStatus("Photo capture cancelled")
        }
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
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        enabled = state.selectedNoteCanUndo,
                        onClick = { viewModel.undoNoteEdit(note) }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Undo, contentDescription = "Undo note edit")
                    }
                    IconButton(
                        enabled = state.selectedNoteCanRedo,
                        onClick = { viewModel.redoNoteEdit(note) }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Redo, contentDescription = "Redo note edit")
                    }
                    IconButton(onClick = { editorSearchOpen = !editorSearchOpen }) {
                        Icon(Icons.Default.Search, contentDescription = "Search in note")
                    }
                    IconButton(onClick = { detailsOpen = true }) {
                        Icon(Icons.Default.Info, contentDescription = "Note details")
                    }
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
                onAddSticky = { viewModel.addBlock(note, NoteBlock.Sticky()) },
                onAddDrawing = { viewModel.addBlock(note, NoteBlock.Drawing()) },
                onAddAttachment = { attachmentLauncher.launch(arrayOf("*/*")) },
                onCaptureImage = {
                    val target = createCameraCaptureTarget(context)
                    pendingCameraCapture = target
                    cameraLauncher.launch(target.uri)
                },
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
        if (detailsOpen) {
            NoteDetailsDialog(note = note, onDismiss = { detailsOpen = false })
        }
        LazyColumn(
            state = editorListState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                NoteMetaEditor(note, state, viewModel)
            }
            if (editorSearchOpen) {
                item {
                    NoteSearchPanel(
                        query = editorSearchQuery,
                        matches = editorSearchMatches,
                        activeMatchIndex = activeSearchMatch,
                        onQueryChange = {
                            editorSearchQuery = it
                            activeSearchMatch = 0
                        },
                        onPrevious = {
                            if (editorSearchMatches.isNotEmpty()) {
                                activeSearchMatch =
                                    (activeSearchMatch - 1 + editorSearchMatches.size) % editorSearchMatches.size
                            }
                        },
                        onNext = {
                            if (editorSearchMatches.isNotEmpty()) {
                                activeSearchMatch = (activeSearchMatch + 1) % editorSearchMatches.size
                            }
                        },
                        onClose = {
                            editorSearchOpen = false
                            editorSearchQuery = ""
                            activeSearchMatch = 0
                        }
                    )
                }
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
                    Box(
                        Modifier
                            .weight(1f)
                            .then(
                                if (block.id == activeSearchBlockId && editorSearchOpen) {
                                    Modifier
                                        .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(10.dp))
                                        .padding(2.dp)
                                } else {
                                    Modifier
                                }
                            )
                    ) {
                        when (block) {
                            is NoteBlock.Text -> TextBlockEditor(note, block, viewModel)
                            is NoteBlock.Checklist -> ChecklistBlockEditor(note, block, viewModel)
                            is NoteBlock.Sticky -> StickyBlockEditor(note, block, viewModel)
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
            Column(
                modifier = Modifier
                    .heightIn(max = 520.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
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
                    Text("Default note", style = MaterialTheme.typography.labelLarge)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        NewNoteKind.entries.forEach { kind ->
                            FilterChip(
                                selected = state.noteDefaults.newNoteKind == kind,
                                onClick = { viewModel.setDefaultNoteKind(kind) },
                                label = { Text(kind.settingsLabel) }
                            )
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

@Composable
fun NoteDetailsDialog(note: SNote, onDismiss: () -> Unit) {
    val details = note.details()
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Note details") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                DetailRow("Folder", note.folder)
                DetailRow("Tags", note.tags.joinToString(", ").ifBlank { "None" })
                DetailRow("Created", formatTimestamp(note.createdAt))
                DetailRow("Modified", formatTimestamp(note.updatedAt))
                DetailRow("Blocks", details.blockLabel)
                DetailRow("Words", details.wordLabel)
                DetailRow("Checklist", details.checklistLabel)
                DetailRow("Sticky notes", details.stickyNotes.toString())
                DetailRow("Ink", details.inkLabel)
                DetailRow("Attachments", details.attachmentLabel)
                DetailRow("Audio", details.audioLabel)
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
fun DetailRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(label, modifier = Modifier.weight(0.4f), color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            value,
            modifier = Modifier.weight(0.6f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.End
        )
    }
}

@Composable
fun NoteSearchPanel(
    query: String,
    matches: List<EditorSearchMatch>,
    activeMatchIndex: Int,
    onQueryChange: (String) -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onClose: () -> Unit
) {
    val activeMatch = matches.getOrNull(activeMatchIndex)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = query,
                    onValueChange = onQueryChange,
                    singleLine = true,
                    label = { Text("Find in note") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
                )
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Close note search")
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    when {
                        query.isBlank() -> "Enter text to search this note"
                        matches.isEmpty() -> "No matches"
                        else -> "${activeMatchIndex + 1} of ${matches.size}"
                    },
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                IconButton(enabled = matches.isNotEmpty(), onClick = onPrevious) {
                    Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Previous note match")
                }
                IconButton(enabled = matches.isNotEmpty(), onClick = onNext) {
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Next note match")
                }
            }
            if (activeMatch != null) {
                Text(activeMatch.label, style = MaterialTheme.typography.labelLarge)
                Text(
                    activeMatch.snippet,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
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
    onAddSticky: () -> Unit,
    onAddDrawing: () -> Unit,
    onAddAttachment: () -> Unit,
    onCaptureImage: () -> Unit,
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
            IconButton(onClick = onAddSticky) { Icon(Icons.Default.Description, "Sticky note") }
            IconButton(onClick = onAddDrawing) { Icon(Icons.Default.Brush, "Handwriting") }
            IconButton(onClick = onAddAttachment) { Icon(Icons.Default.AttachFile, "Attachment") }
            IconButton(onClick = onCaptureImage) { Icon(Icons.Default.CameraAlt, "Capture image") }
            FilledIconButton(onClick = onRecord) {
                Icon(Icons.Default.Mic, if (isRecording) "Stop recording" else "Record audio")
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TextBlockEditor(note: SNote, block: NoteBlock.Text, viewModel: NotesViewModel) {
    var colorMenuOpen by remember { mutableStateOf(false) }
    var highlightMenuOpen by remember { mutableStateOf(false) }
    val textStyle = TextStyle(
        color = Color(block.color),
        fontSize = block.sizeSp.sp,
        fontWeight = if (block.bold) FontWeight.Bold else FontWeight.Normal,
        fontStyle = if (block.italic) FontStyle.Italic else FontStyle.Normal,
        fontFamily = block.fontFamily.toComposeFontFamily(),
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
                IconButton(onClick = { viewModel.duplicateBlock(note, block) }) {
                    Icon(Icons.Default.ContentCopy, "Duplicate text block")
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
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                NoteFontFamily.entries.forEach { fontFamily ->
                    FilterChip(
                        selected = block.fontFamily == fontFamily,
                        onClick = { viewModel.updateBlock(note, block.copy(fontFamily = fontFamily)) },
                        label = { Text(fontFamily.label) }
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
                                    TextAlignment.Start -> Icons.AutoMirrored.Filled.FormatAlignLeft
                                    TextAlignment.Center -> Icons.Default.FormatAlignCenter
                                    TextAlignment.End -> Icons.AutoMirrored.Filled.FormatAlignRight
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
fun StickyBlockEditor(note: SNote, block: NoteBlock.Sticky, viewModel: NotesViewModel) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(block.color))
    ) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Icon(Icons.Default.Description, contentDescription = null, tint = Color(0xFF2B2A27))
                Text(
                    "Sticky note",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.labelLarge,
                    color = Color(0xFF2B2A27)
                )
                TextButton(onClick = { viewModel.updateBlock(note, block.copy(collapsed = !block.collapsed)) }) {
                    Text(if (block.collapsed) "Expand" else "Minimize", color = Color(0xFF2B2A27))
                }
                IconButton(onClick = { viewModel.duplicateBlock(note, block) }) {
                    Icon(Icons.Default.ContentCopy, "Duplicate sticky note", tint = Color(0xFF2B2A27))
                }
                IconButton(onClick = { viewModel.removeBlock(note, block) }) {
                    Icon(Icons.Default.Delete, "Delete sticky note", tint = Color(0xFF2B2A27))
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                STICKY_NOTE_COLORS.forEach { color ->
                    Box(
                        Modifier
                            .size(28.dp)
                            .background(Color(color), CircleShape)
                            .border(
                                width = if (block.color == color) 3.dp else 1.dp,
                                color = Color(0xFF2B2A27),
                                shape = CircleShape
                            )
                            .clickable { viewModel.updateBlock(note, block.copy(color = color)) }
                    )
                }
            }
            if (!block.collapsed) {
                BasicTextField(
                    value = block.text,
                    onValueChange = { viewModel.updateBlock(note, block.copy(text = it)) },
                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFF2B2A27)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .background(Color.White.copy(alpha = 0.35f), RoundedCornerShape(6.dp))
                        .border(1.dp, Color(0x802B2A27), RoundedCornerShape(6.dp))
                        .padding(10.dp),
                    decorationBox = { inner ->
                        if (block.text.isBlank()) Text("Sticky note text", color = Color(0xFF5F5A4D))
                        inner()
                    }
                )
            } else {
                Text(
                    block.text.ifBlank { "Collapsed sticky note" },
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF2B2A27),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
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
                IconButton(onClick = { viewModel.duplicateBlock(note, block) }) {
                    Icon(Icons.Default.ContentCopy, "Duplicate checklist")
                }
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
                HorizontalDivider()
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
                IconButton(onClick = { viewModel.duplicateBlock(note, block) }) {
                    Icon(Icons.Default.ContentCopy, "Duplicate drawing")
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
    val context = LocalContext.current
    Card(shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            if (block.isImageAttachment) {
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
                IconButton(
                    onClick = {
                        runCatching {
                            context.startActivity(Intent.createChooser(block.toViewIntent(), "Open attachment"))
                        }.onSuccess {
                            viewModel.setStatus("Opening attachment")
                        }.onFailure {
                            viewModel.setStatus("No app can open attachment")
                        }
                    }
                ) {
                    Icon(Icons.AutoMirrored.Filled.OpenInNew, "Open attachment")
                }
                IconButton(onClick = { viewModel.duplicateBlock(note, block) }) {
                    Icon(Icons.Default.ContentCopy, "Duplicate attachment")
                }
                IconButton(onClick = { viewModel.removeBlock(note, block) }) {
                    Icon(Icons.Default.Delete, "Delete attachment")
                }
            }
        }
    }
}

data class AttachmentMetadata(val name: String, val mimeHint: String, val sizeBytes: Long)

data class CameraCaptureTarget(val uri: Uri, val file: File) {
    fun toAttachmentBlock(): NoteBlock.Attachment =
        NoteBlock.Attachment(
            uri = uri.toString(),
            name = file.name,
            mimeHint = "image/jpeg",
            sizeBytes = file.length()
        )
}

fun AttachmentMetadata.toNoteBlock(uri: String): NoteBlock =
    if (mimeHint.startsWith("audio/")) {
        NoteBlock.Audio(path = uri, name = name)
    } else {
        NoteBlock.Attachment(
            uri = uri,
            name = name,
            mimeHint = mimeHint,
            sizeBytes = sizeBytes
        )
    }

fun createCameraCaptureTarget(context: Context): CameraCaptureTarget {
    val captureDir = File(context.filesDir, "captures").apply { mkdirs() }
    val file = File(captureDir, "capture-${System.currentTimeMillis()}.jpg")
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    return CameraCaptureTarget(uri, file)
}

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

val NoteBlock.Attachment.viewMimeType: String
    get() = mimeHint.ifBlank { "*/*" }

fun NoteBlock.Attachment.toViewIntent(): Intent =
    Intent(Intent.ACTION_VIEW)
        .setDataAndType(Uri.parse(uri), viewMimeType)
        .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

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
    val context = LocalContext.current
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
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
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
                                player.setAudioSource(context, block.path)
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
                TextButton(
                    onClick = {
                        val timestamp = runCatching { player.currentPosition.toLong() }.getOrDefault(0L)
                        viewModel.updateBlock(note, block.addMarker(timestamp))
                    }
                ) {
                    Text("Marker")
                }
                IconButton(onClick = { viewModel.duplicateBlock(note, block) }) {
                    Icon(Icons.Default.ContentCopy, "Duplicate audio")
                }
                IconButton(onClick = { viewModel.removeBlock(note, block) }) {
                    Icon(Icons.Default.Delete, "Delete audio")
                }
            }
            if (block.markers.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    block.markers.sortedBy { it.timestampMs }.forEach { marker ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                formatDuration(marker.timestampMs).ifBlank { "0:00" },
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.width(48.dp)
                            )
                            Text(marker.label, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                            IconButton(onClick = { viewModel.updateBlock(note, block.removeMarker(marker.id)) }) {
                                Icon(Icons.Default.Delete, "Delete audio marker")
                            }
                        }
                    }
                }
            }
        }
    }
}

fun MediaPlayer.setAudioSource(context: Context, path: String) {
    val uri = Uri.parse(path)
    if (uri.scheme == "content") {
        setDataSource(context, uri)
    } else {
        setDataSource(path)
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

fun SNote.editorSearchMatches(query: String): List<EditorSearchMatch> {
    val normalized = query.trim()
    if (normalized.isBlank()) return emptyList()
    return buildList {
        if (title.contains(normalized, ignoreCase = true)) {
            add(EditorSearchMatch(null, "Title", title.searchSnippet(normalized)))
        }
        if (folder.contains(normalized, ignoreCase = true)) {
            add(EditorSearchMatch(null, "Folder", folder.searchSnippet(normalized)))
        }
        tags.filter { it.contains(normalized, ignoreCase = true) }
            .forEach { tag -> add(EditorSearchMatch(null, "Tag", "#${tag.searchSnippet(normalized)}")) }
        blocks.forEach { block ->
            when (block) {
                is NoteBlock.Text -> if (block.text.contains(normalized, ignoreCase = true)) {
                    add(EditorSearchMatch(block.id, "Text block", block.text.searchSnippet(normalized)))
                }
                is NoteBlock.Checklist -> block.items
                    .filter { it.text.contains(normalized, ignoreCase = true) }
                    .forEach { item ->
                        val status = if (item.checked) "done" else "open"
                        add(EditorSearchMatch(block.id, "Checklist item ($status)", item.text.searchSnippet(normalized)))
                    }
                is NoteBlock.Sticky -> if (block.text.contains(normalized, ignoreCase = true)) {
                    add(EditorSearchMatch(block.id, "Sticky note", block.text.searchSnippet(normalized)))
                }
                is NoteBlock.Attachment -> if (
                    block.name.contains(normalized, ignoreCase = true) ||
                    block.mimeHint.contains(normalized, ignoreCase = true)
                ) {
                    add(EditorSearchMatch(block.id, "Attachment", block.name.searchSnippet(normalized)))
                }
                is NoteBlock.Audio -> {
                    if (
                        block.name.contains(normalized, ignoreCase = true) ||
                        block.path.contains(normalized, ignoreCase = true)
                    ) {
                        add(EditorSearchMatch(block.id, "Audio", block.name.searchSnippet(normalized)))
                    }
                    block.markers
                        .filter { marker -> marker.label.contains(normalized, ignoreCase = true) }
                        .forEach { marker ->
                            add(EditorSearchMatch(block.id, "Audio marker ${formatDuration(marker.timestampMs).ifBlank { "0:00" }}", marker.label.searchSnippet(normalized)))
                        }
                }
                is NoteBlock.Drawing -> Unit
            }
        }
    }
}

fun editorSearchTargetItemIndex(
    blocks: List<NoteBlock>,
    match: EditorSearchMatch?,
    searchPanelVisible: Boolean
): Int {
    val blockIndex = match?.blockId?.let { id -> blocks.indexOfFirst { it.id == id } } ?: -1
    return if (blockIndex >= 0) {
        1 + (if (searchPanelVisible) 1 else 0) + blockIndex
    } else {
        0
    }
}

fun NoteBlock.contentSearchLabels(query: String): List<String> = when (this) {
    is NoteBlock.Text -> if (text.contains(query, ignoreCase = true)) listOf("Text: ${text.searchSnippet(query)}") else emptyList()
    is NoteBlock.Sticky -> if (text.contains(query, ignoreCase = true)) listOf("Sticky: ${text.searchSnippet(query)}") else emptyList()
    is NoteBlock.Checklist -> items
        .filter { it.text.contains(query, ignoreCase = true) }
        .map { "Checklist: ${it.text.searchSnippet(query)}" }
    is NoteBlock.Audio -> markers
        .filter { it.label.contains(query, ignoreCase = true) }
        .map { "Audio marker: ${formatDuration(it.timestampMs).ifBlank { "0:00" }} ${it.label.searchSnippet(query)}" }
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

fun List<SNote>.deleteTrash(): List<SNote> =
    filterNot { it.deleted }

fun List<SNote>.restoreTrash(): List<SNote> =
    map { note -> if (note.deleted) note.copy(deleted = false) else note }

fun List<SNote>.duplicatedNotesByIds(ids: Set<String>, now: Long = System.currentTimeMillis()): List<SNote> =
    filter { it.id in ids }.mapIndexed { index, note -> note.duplicate(now = now + index) }

fun List<SNote>.duplicateByIds(ids: Set<String>, now: Long = System.currentTimeMillis()): List<SNote> =
    duplicatedNotesByIds(ids, now) + this

fun Map<String, ArrayDeque<SNote>>.availableNoteIds(): Set<String> =
    filterValues { it.isNotEmpty() }.keys

fun ArrayDeque<SNote>.popLastOrNull(): SNote? =
    if (isEmpty()) null else removeLast()

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

fun NoteBlock.Audio.addMarker(timestampMs: Long): NoteBlock.Audio {
    val timestamp = timestampMs.coerceAtLeast(0L)
    return copy(markers = markers + AudioMarker(label = "Marker ${markers.size + 1}", timestampMs = timestamp))
}

fun NoteBlock.Audio.removeMarker(markerId: String): NoteBlock.Audio =
    copy(markers = markers.filterNot { it.id == markerId })

class AudioRecorder(private val context: Context) {
    private var recorder: MediaRecorder? = null
    private var outputFile: File? = null

    fun start(): File? = runCatching {
        val audioDir = File(context.filesDir, "audio").apply { mkdirs() }
        val file = File(audioDir, "recording-${System.currentTimeMillis()}.m4a")
        recorder = createMediaRecorder(context).apply {
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

@Suppress("DEPRECATION")
fun createMediaRecorder(context: Context): MediaRecorder =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) MediaRecorder(context) else MediaRecorder()

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
    .put("schemaVersion", BACKUP_SCHEMA_VERSION)
    .put("appId", BACKUP_APP_ID)
    .put("exportedAt", System.currentTimeMillis())
    .put("noteCount", notes.size)
    .put("notes", JSONArray().also { array -> notes.forEach { array.put(it.toBackupJson()) } })
    .toString(2)

data class BackupMetadata(
    val schemaVersion: Int,
    val appId: String,
    val exportedAt: Long,
    val noteCount: Int
)

fun backupMetadataFromJson(rawBackup: String): BackupMetadata? = runCatching {
    val trimmed = rawBackup.trim()
    if (trimmed.startsWith("[")) return@runCatching BackupMetadata(
        schemaVersion = 0,
        appId = "legacy-array",
        exportedAt = 0L,
        noteCount = JSONArray(trimmed).length()
    )
    val json = JSONObject(trimmed)
    BackupMetadata(
        schemaVersion = json.optInt("schemaVersion", 0),
        appId = json.optString("appId", "unknown"),
        exportedAt = json.optLong("exportedAt", 0L),
        noteCount = json.optInt("noteCount", json.optJSONArray("notes")?.length() ?: 0)
    )
}.getOrNull()

fun backupImportStatus(importedCount: Int, metadata: BackupMetadata?): String {
    val noteLabel = if (importedCount == 1) "note" else "notes"
    val source = when (metadata?.appId) {
        BACKUP_APP_ID -> " from S Notes Style backup v${metadata.schemaVersion}"
        "legacy-array" -> " from legacy backup"
        null, "unknown" -> ""
        else -> " from ${metadata.appId} backup v${metadata.schemaVersion}"
    }
    return "Imported $importedCount $noteLabel$source"
}

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
            .put("fontFamily", fontFamily.name)
            .put("alignment", alignment.name)

        is NoteBlock.Checklist -> json
            .put("type", "checklist")
            .put("items", JSONArray().also { array ->
                items.forEach { item ->
                    array.put(JSONObject().put("id", item.id).put("text", item.text).put("checked", item.checked))
                }
            })

        is NoteBlock.Sticky -> json
            .put("type", "sticky")
            .put("text", text)
            .put("color", color)
            .put("collapsed", collapsed)

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
            .put("markers", JSONArray().also { markerArray ->
                markers.forEach { marker ->
                    markerArray.put(
                        JSONObject()
                            .put("id", marker.id)
                            .put("label", marker.label)
                            .put("timestampMs", marker.timestampMs)
                    )
                }
            })
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

    "sticky" -> NoteBlock.Sticky(
        id = optString("id", UUID.randomUUID().toString()),
        text = optString("text"),
        color = optLong("color", STICKY_NOTE_COLORS.first()),
        collapsed = optBoolean("collapsed", false)
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
        durationHintMs = optLong("durationHintMs", 0L),
        markers = optJSONArray("markers").toAudioMarkers()
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
        fontFamily = optString("fontFamily").toNoteFontFamily(NoteFontFamily.Default),
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

fun JSONArray?.toAudioMarkers(): List<AudioMarker> {
    if (this == null) return emptyList()
    return buildList {
        for (i in 0 until length()) {
            val marker = optJSONObject(i) ?: continue
            val label = marker.optString("label", "Marker ${i + 1}").ifBlank { "Marker ${i + 1}" }
            add(
                AudioMarker(
                    id = marker.optString("id", UUID.randomUUID().toString()),
                    label = label,
                    timestampMs = marker.optLong("timestampMs", 0L).coerceAtLeast(0L)
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

fun String.toNoteFontFamily(default: NoteFontFamily): NoteFontFamily =
    NoteFontFamily.entries.firstOrNull { it.name.equals(this, ignoreCase = true) } ?: default

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
