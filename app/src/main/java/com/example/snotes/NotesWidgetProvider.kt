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

fun notesWidgetSummary(notes: List<SNote>): NotesWidgetSummary {
    val visible = notes.filterNot { it.deleted }.sortedWith(NoteSortMode.ModifiedNewest.comparator)
    val latest = visible.firstOrNull()
    return NotesWidgetSummary(
        title = latest?.title ?: "S Notes Style",
        subtitle = when {
            latest == null -> "No notes yet"
            latest.locked -> "Locked note • ${visible.size} note${if (visible.size == 1) "" else "s"}"
            latest.preview.isNotBlank() -> "${latest.preview} • ${visible.size} note${if (visible.size == 1) "" else "s"}"
            else -> "${latest.folder} • ${visible.size} note${if (visible.size == 1) "" else "s"}"
        },
        noteId = latest?.id
    )
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
            val quickTextIntent = Intent(context, MainActivity::class.java)
                .setAction(ACTION_QUICK_NOTE)
                .putExtra(EXTRA_QUICK_NOTE_KIND, NewNoteKind.Text.name)
            val openPendingIntent = PendingIntent.getActivity(
                context,
                appWidgetId,
                openIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val quickTextPendingIntent = PendingIntent.getActivity(
                context,
                appWidgetId + 10_000,
                quickTextIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val views = RemoteViews(context.packageName, R.layout.notes_widget).apply {
                setTextViewText(R.id.widget_title, summary.title)
                setTextViewText(R.id.widget_summary, summary.subtitle)
                setOnClickPendingIntent(R.id.widget_root, openPendingIntent)
                setOnClickPendingIntent(R.id.widget_quick_note, quickTextPendingIntent)
            }
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
