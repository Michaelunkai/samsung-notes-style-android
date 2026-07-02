package com.example.snotes

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import kotlinx.coroutines.runBlocking

data class NotesWidgetSummary(val title: String, val subtitle: String, val noteId: String? = null)

fun widgetQuickNoteKinds(): List<NewNoteKind> =
    listOf(
        NewNoteKind.Text,
        NewNoteKind.Checklist,
        NewNoteKind.Sticky,
        NewNoteKind.Drawing,
        NewNoteKind.Meeting
    )

fun notesWidgetSummary(notes: List<SNote>, now: Long = System.currentTimeMillis()): NotesWidgetSummary {
    val visible = notes.filter { !it.deleted && !it.archived }.sortedWith(NoteSortMode.ModifiedNewest.comparator)
    val latest = visible.firstOrNull()
    val status = notesWidgetStatus(visible, now)
    return NotesWidgetSummary(
        title = latest?.title ?: "S Notes Style",
        subtitle = when {
            latest == null -> "No notes yet"
            latest.locked -> "Locked note • $status"
            latest.reminderAt != null -> "${latest.reminderLabel(now)} • $status"
            latest.preview.isNotBlank() -> "${latest.preview} • $status"
            else -> "${latest.folder} • $status"
        },
        noteId = latest?.id
    )
}

fun notesWidgetStatus(notes: List<SNote>, now: Long = System.currentTimeMillis()): String {
    val noteCount = "${notes.size} note${if (notes.size == 1) "" else "s"}"
    val overdue = notes.count { (it.reminderAt ?: Long.MAX_VALUE) < now }
    val upcomingReminders = notes.count { (it.reminderAt ?: 0L) >= now }
    val checklistItems = notes.flatMap { it.blocks }.filterIsInstance<NoteBlock.Checklist>().sumOf { it.items.size }
    val completedChecklistItems = notes.flatMap { it.blocks }
        .filterIsInstance<NoteBlock.Checklist>()
        .sumOf { checklist -> checklist.items.count { it.checked } }
    val mediaBlocks = notes.sumOf { it.mediaBlockCount() }
    return buildList {
        add(noteCount)
        when {
            overdue > 0 -> add("$overdue overdue")
            upcomingReminders > 0 -> add("$upcomingReminders reminder${if (upcomingReminders == 1) "" else "s"}")
        }
        if (checklistItems > 0) add("$completedChecklistItems/$checklistItems tasks")
        if (mediaBlocks > 0) add("$mediaBlocks media")
    }.joinToString(" • ")
}

fun refreshNotesWidgets(context: Context) {
    val appContext = context.applicationContext
    val manager = AppWidgetManager.getInstance(appContext)
    val ids = manager.getAppWidgetIds(ComponentName(appContext, NotesWidgetProvider::class.java))
    if (ids.isNotEmpty()) NotesWidgetProvider().onUpdate(appContext, manager, ids)
}

class NotesWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        val summary = runCatching {
            runBlocking { notesWidgetSummary(RoomNoteRepository(context).load()) }
        }.getOrDefault(notesWidgetSummary(emptyList()))
        appWidgetIds.forEach { appWidgetId ->
            val openIntent = Intent(context, MainActivity::class.java).apply {
                summary.noteId?.let { putExtra(EXTRA_OPEN_NOTE_ID, it) }
            }
            val openPendingIntent = PendingIntent.getActivity(
                context,
                appWidgetId,
                openIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val views = RemoteViews(context.packageName, R.layout.notes_widget).apply {
                setTextViewText(R.id.widget_title, summary.title)
                setTextViewText(R.id.widget_summary, summary.subtitle)
                setOnClickPendingIntent(R.id.widget_root, openPendingIntent)
                setOnClickPendingIntent(
                    R.id.widget_quick_text_note,
                    quickNotePendingIntent(context, appWidgetId + 10_000, NewNoteKind.Text)
                )
                setOnClickPendingIntent(
                    R.id.widget_quick_checklist_note,
                    quickNotePendingIntent(context, appWidgetId + 20_000, NewNoteKind.Checklist)
                )
                setOnClickPendingIntent(
                    R.id.widget_quick_sticky_note,
                    quickNotePendingIntent(context, appWidgetId + 30_000, NewNoteKind.Sticky)
                )
                setOnClickPendingIntent(
                    R.id.widget_quick_drawing_note,
                    quickNotePendingIntent(context, appWidgetId + 40_000, NewNoteKind.Drawing)
                )
                setOnClickPendingIntent(
                    R.id.widget_quick_meeting_note,
                    quickNotePendingIntent(context, appWidgetId + 50_000, NewNoteKind.Meeting)
                )
            }
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    private fun quickNotePendingIntent(
        context: Context,
        requestCode: Int,
        kind: NewNoteKind
    ): PendingIntent {
        val intent = Intent(context, MainActivity::class.java)
            .setAction(ACTION_QUICK_NOTE)
            .putExtra(EXTRA_QUICK_NOTE_KIND, kind.name)
        return PendingIntent.getActivity(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
