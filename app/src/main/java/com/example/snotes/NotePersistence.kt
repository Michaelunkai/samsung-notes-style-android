package com.example.snotes

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Transaction
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import java.io.File
import java.util.Locale
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.json.JSONArray
import org.json.JSONObject

@Entity(
    tableName = "notes",
    indices = [
        Index("folder"),
        Index("updatedAt"),
        Index("deleted"),
        Index("deletedAt"),
        Index("pinned"),
        Index("favorite"),
        Index("archived"),
        Index("reminderAt")
    ]
)
data class NoteEntity(
    @androidx.room.PrimaryKey val id: String,
    val title: String,
    val folder: String,
    val tagsJson: String,
    val preview: String,
    val blocksJson: String,
    val pinned: Boolean = false,
    val favorite: Boolean,
    val locked: Boolean,
    val archived: Boolean = false,
    val deleted: Boolean,
    val deletedAt: Long? = null,
    val reminderAt: Long? = null,
    val pageTemplate: String = PageTemplate.Plain.name,
    val paperColor: Long = 0xFFFFFBF0,
    val createdAt: Long,
    val updatedAt: Long,
    val schemaVersion: Int = 6
)

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY pinned DESC, favorite DESC, updatedAt DESC")
    suspend fun loadAll(): List<NoteEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(notes: List<NoteEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(note: NoteEntity)

    @Query("DELETE FROM notes")
    suspend fun deleteAll()

    @Query("DELETE FROM notes WHERE id IN (:ids)")
    suspend fun deleteByIds(ids: List<String>)

    @Transaction
    suspend fun replaceAll(notes: List<NoteEntity>) {
        deleteAll()
        upsertAll(notes)
    }
}

@Database(entities = [NoteEntity::class], version = 6, exportSchema = true)
abstract class NotesDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
}

class RoomNoteRepository(context: Context) {
    private val appContext = context.applicationContext
    private val database = NotesDatabaseProvider.get(appContext)
    private val legacyFile = File(appContext.filesDir, "notes.json")
    private val mutex = Mutex()

    suspend fun load(): List<SNote> = mutex.withLock {
        val roomNotes = database.noteDao().loadAll().map { it.toNote() }
        if (roomNotes.isNotEmpty()) return@withLock roomNotes

        val legacyNotes = loadLegacyJson()
        if (legacyNotes.isNotEmpty()) {
            database.noteDao().replaceAll(legacyNotes.map { it.toEntity() })
        }
        legacyNotes
    }

    suspend fun save(notes: List<SNote>) {
        mutex.withLock {
            database.noteDao().replaceAll(notes.map { it.toEntity() })
            writeAutoBackupSnapshot(appContext.filesDir, notes)
        }
    }

    suspend fun saveNote(note: SNote) {
        mutex.withLock {
            val updatedNotes = database.noteDao().loadAll()
                .map { it.toNote() }
                .replaceOrAdd(note)
            database.noteDao().upsert(note.toEntity())
            writeAutoBackupSnapshot(appContext.filesDir, updatedNotes)
        }
    }

    suspend fun deleteNotes(ids: Collection<String>) {
        if (ids.isEmpty()) return
        mutex.withLock {
            database.noteDao().deleteByIds(ids.toList())
            val remainingNotes = database.noteDao().loadAll().map { it.toNote() }
            writeAutoBackupSnapshot(appContext.filesDir, remainingNotes)
        }
    }

    fun loadLatestAutoBackupText(): String? = readLatestAutoBackupText(appContext.filesDir)

    private fun loadLegacyJson(): List<SNote> = runCatching {
        if (!legacyFile.exists()) return emptyList()
        val array = JSONArray(legacyFile.readText())
        buildList {
            for (i in 0 until array.length()) {
                add(array.getJSONObject(i).toNote())
            }
        }
    }.getOrDefault(emptyList())
}

object NotesDatabaseProvider {
    @Volatile
    private var instance: NotesDatabase? = null

    fun get(context: Context): NotesDatabase =
        instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                NotesDatabase::class.java,
                "snotes.db"
            )
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6)
                .fallbackToDestructiveMigration(false)
                .build()
                .also { instance = it }
        }

    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE notes ADD COLUMN pageTemplate TEXT NOT NULL DEFAULT 'Plain'")
            db.execSQL("ALTER TABLE notes ADD COLUMN paperColor INTEGER NOT NULL DEFAULT 4294966256")
        }
    }

    private val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE notes ADD COLUMN pinned INTEGER NOT NULL DEFAULT 0")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_notes_pinned ON notes(pinned)")
        }
    }

    private val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE notes ADD COLUMN reminderAt INTEGER")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_notes_reminderAt ON notes(reminderAt)")
        }
    }

    private val MIGRATION_4_5 = object : Migration(4, 5) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE notes ADD COLUMN deletedAt INTEGER")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_notes_deletedAt ON notes(deletedAt)")
        }
    }

    private val MIGRATION_5_6 = object : Migration(5, 6) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE notes ADD COLUMN archived INTEGER NOT NULL DEFAULT 0")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_notes_archived ON notes(archived)")
        }
    }
}

fun SNote.toEntity(): NoteEntity = NoteEntity(
    id = id,
    title = title,
    folder = folder,
    tagsJson = JSONArray(tags).toString(),
    preview = preview,
    blocksJson = JSONArray().also { array -> blocks.forEach { array.put(it.toJson()) } }.toString(),
    pinned = pinned,
    favorite = favorite,
    locked = locked,
    archived = archived,
    deleted = deleted,
    deletedAt = deletedAt,
    reminderAt = reminderAt,
    pageTemplate = pageTemplate.name,
    paperColor = paperColor,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun NoteEntity.toNote(): SNote = SNote(
    id = id,
    title = title,
    folder = folder,
    tags = JSONArray(tagsJson).toStringList(),
    blocks = JSONArray(blocksJson).toBlocks().ifEmpty { listOf(NoteBlock.Text()) },
    pinned = pinned,
    favorite = favorite,
    locked = locked,
    archived = archived,
    deleted = deleted,
    deletedAt = deletedAt,
    reminderAt = reminderAt,
    pageTemplate = pageTemplate.toPageTemplate(PageTemplate.Plain),
    paperColor = paperColor,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun SNote.toBackupJson(): JSONObject = toJson()

const val AUTO_BACKUP_DIR = "auto_backups"
const val AUTO_BACKUP_LATEST_FILE = "latest.json"
const val AUTO_BACKUP_MAX_SNAPSHOTS = 5
const val AUTO_BACKUP_MIN_INTERVAL_MS = 10 * 60 * 1000L

fun writeAutoBackupSnapshot(filesDir: File, notes: List<SNote>, now: Long = System.currentTimeMillis()) {
    val backupDir = File(filesDir, AUTO_BACKUP_DIR).apply { mkdirs() }
    val payload = notesToBackupJson(notes)
    File(backupDir, AUTO_BACKUP_LATEST_FILE).writeTextAtomically(payload).setLastModified(now)
    val snapshots = backupDir.autoBackupSnapshots()
    val newestSnapshot = snapshots.maxByOrNull { it.lastModified() }
    if (newestSnapshot == null || now - newestSnapshot.lastModified() >= AUTO_BACKUP_MIN_INTERVAL_MS) {
        File(backupDir, "snapshot-${String.format(Locale.US, "%013d", now)}.json")
            .writeTextAtomically(payload)
            .setLastModified(now)
    }
    backupDir.pruneAutoBackupSnapshots()
}

fun readLatestAutoBackupText(filesDir: File): String? =
    latestAutoBackupFile(filesDir)
        .takeIf { it.isFile }
        ?.readText()

fun latestAutoBackupFile(filesDir: File): File =
    File(File(filesDir, AUTO_BACKUP_DIR), AUTO_BACKUP_LATEST_FILE)

fun File.autoBackupSnapshots(): List<File> =
    listFiles { file -> file.isFile && file.name.startsWith("snapshot-") && file.name.endsWith(".json") }
        ?.toList()
        .orEmpty()

fun File.pruneAutoBackupSnapshots(maxSnapshots: Int = AUTO_BACKUP_MAX_SNAPSHOTS) {
    autoBackupSnapshots()
        .sortedByDescending { it.name }
        .drop(maxSnapshots)
        .forEach { it.delete() }
}

private fun File.writeTextAtomically(text: String): File {
    val temp = File(parentFile, "$name.tmp")
    temp.writeText(text)
    if (exists()) delete()
    temp.renameTo(this)
    return this
}

private fun List<SNote>.replaceOrAdd(note: SNote): List<SNote> =
    if (any { it.id == note.id }) {
        map { if (it.id == note.id) note else it }
    } else {
        listOf(note) + this
    }
