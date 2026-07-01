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
        Index("pinned"),
        Index("favorite")
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
    val deleted: Boolean,
    val pageTemplate: String = PageTemplate.Plain.name,
    val paperColor: Long = 0xFFFFFBF0,
    val createdAt: Long,
    val updatedAt: Long,
    val schemaVersion: Int = 3
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

    @Transaction
    suspend fun replaceAll(notes: List<NoteEntity>) {
        deleteAll()
        upsertAll(notes)
    }
}

@Database(entities = [NoteEntity::class], version = 3, exportSchema = true)
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
        }
    }

    suspend fun saveNote(note: SNote) {
        mutex.withLock {
            database.noteDao().upsert(note.toEntity())
        }
    }

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
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
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
    deleted = deleted,
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
    deleted = deleted,
    pageTemplate = pageTemplate.toPageTemplate(PageTemplate.Plain),
    paperColor = paperColor,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun SNote.toBackupJson(): JSONObject = toJson()
