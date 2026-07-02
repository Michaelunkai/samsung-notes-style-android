package com.example.snotes

import android.Manifest
import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

const val NOTE_REMINDER_ACTION = "com.example.snotes.action.NOTE_REMINDER"
const val EXTRA_REMINDER_NOTE_ID = "com.example.snotes.extra.REMINDER_NOTE_ID"
const val EXTRA_REMINDER_NOTE_TITLE = "com.example.snotes.extra.REMINDER_NOTE_TITLE"
const val EXTRA_REMINDER_NOTE_LOCKED = "com.example.snotes.extra.REMINDER_NOTE_LOCKED"
const val NOTE_REMINDER_CHANNEL_ID = "note_reminders"

fun scheduleAllNoteReminders(context: Context, notes: List<SNote>) {
    notes.forEach { scheduleNoteReminder(context, it) }
}

fun scheduleNoteReminder(context: Context, note: SNote) {
    val triggerAt = note.reminderAt
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val pendingIntent = reminderPendingIntent(context, note)
    if (triggerAt == null || note.deleted || triggerAt <= System.currentTimeMillis()) {
        alarmManager.cancel(pendingIntent)
        return
    }
    alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
}

fun cancelNoteReminder(context: Context, noteId: String) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.cancel(reminderPendingIntent(context, noteId))
}

fun SNote.reminderNotificationTitle(): String =
    if (locked) "Locked note reminder" else displayTitle()

fun SNote.reminderNotificationText(): String =
    if (locked) "Unlock the note to view its contents" else "Tap to open this note"

private fun reminderPendingIntent(context: Context, note: SNote): PendingIntent =
    PendingIntent.getBroadcast(
        context,
        note.id.hashCode(),
        reminderIntent(context, note),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

private fun reminderPendingIntent(context: Context, noteId: String): PendingIntent {
    val intent = Intent(context, NoteReminderReceiver::class.java)
        .setAction(NOTE_REMINDER_ACTION)
        .putExtra(EXTRA_REMINDER_NOTE_ID, noteId)
    return PendingIntent.getBroadcast(
        context,
        noteId.hashCode(),
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
}

private fun reminderIntent(context: Context, note: SNote): Intent =
    Intent(context, NoteReminderReceiver::class.java)
        .setAction(NOTE_REMINDER_ACTION)
        .putExtra(EXTRA_REMINDER_NOTE_ID, note.id)
        .putExtra(EXTRA_REMINDER_NOTE_TITLE, note.reminderNotificationTitle())
        .putExtra(EXTRA_REMINDER_NOTE_LOCKED, note.locked)

class NoteReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != NOTE_REMINDER_ACTION) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        val noteId = intent.getStringExtra(EXTRA_REMINDER_NOTE_ID).orEmpty()
        if (noteId.isBlank()) return
        val title = intent.getStringExtra(EXTRA_REMINDER_NOTE_TITLE).orEmpty().ifBlank { "Note reminder" }
        val locked = intent.getBooleanExtra(EXTRA_REMINDER_NOTE_LOCKED, false)
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(
            NotificationChannel(
                NOTE_REMINDER_CHANNEL_ID,
                "Note reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            )
        )
        val openIntent = Intent(context, MainActivity::class.java)
            .putExtra(EXTRA_OPEN_NOTE_ID, noteId)
            .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val openPendingIntent = PendingIntent.getActivity(
            context,
            noteId.hashCode(),
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = Notification.Builder(context, NOTE_REMINDER_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(if (locked) "Unlock the note to view its contents" else "Tap to open this note")
            .setContentIntent(openPendingIntent)
            .setAutoCancel(true)
            .setShowWhen(true)
            .build()
        manager.notify(noteId.hashCode(), notification)
    }
}

class ReminderBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED && intent.action != Intent.ACTION_LOCKED_BOOT_COMPLETED) return
        runBlocking {
            val notes = withContext(Dispatchers.IO) {
                RoomNoteRepository(context.applicationContext).load()
            }
            scheduleAllNoteReminders(context.applicationContext, notes)
        }
    }
}
