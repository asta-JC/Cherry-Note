package com.asta.cherry_note.data

import android.app.Application
import android.content.Context
import androidx.room.*
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.asta.cherry_note.data.local.CherryDatabase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey val id: String = java.util.UUID.randomUUID().toString(),
    val title: String = "", val body: String = "", val tags: String = "",
    val folder: String = "Personal", val color: Int = 0, val pinned: Boolean = false,
    val checklist: Boolean = false, val bold: Boolean = false,
    val updated: Long = System.currentTimeMillis()
)
@Entity(tableName = "folders")
data class Folder(@PrimaryKey val name: String, val icon: String = "✦", val color: Int = 0)
data class Appearance(val theme: String = "System", val type: String = "Modern")

@Dao interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY pinned DESC, updated DESC") fun observe(): Flow<List<Note>>
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun put(note: Note)
    @Delete suspend fun delete(note: Note)
    @Query("SELECT * FROM folders ORDER BY name") fun folders(): Flow<List<Folder>>
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun putFolder(folder: Folder)
}
@Database(entities = [Note::class, Folder::class], version = 1, exportSchema = false)
abstract class NoteDatabase : RoomDatabase() { abstract fun dao(): NoteDao }
private val Context.preferences by preferencesDataStore("appearance")

class NoteRepository(private val context: Context) {
    private val db = Room.databaseBuilder(context, NoteDatabase::class.java, "cherry.db").build()
    private val dao = db.dao()
    private val store = context.preferences
    val notes = dao.observe()
    val folders = dao.folders()
    val appearance = store.data.map {
        Appearance(theme = it[stringPreferencesKey("theme")] ?: "System", type = it[stringPreferencesKey("type")] ?: "Modern")
    }
    suspend fun preference(key: String, value: String) { store.edit { it[stringPreferencesKey(key)] = value } }
    suspend fun save(note: Note) = dao.put(note)
    suspend fun delete(note: Note) = dao.delete(note)
    suspend fun folder(folder: Folder) = dao.putFolder(folder)

    suspend fun seed() {
        recoverReplacementNotes()
        if (store.data.first()[booleanPreferencesKey("initialized")] == true) return
        db.withTransaction {
            listOf(Folder("Personal", "♡", 0), Folder("Work", "↗", 2), Folder("Ideas", "✦", 3), Folder("Journal", "☾", 1)).forEach { dao.putFolder(it) }
            if (dao.observe().first().isEmpty()) samples.forEach { dao.put(it) }
        }
        store.edit { it[booleanPreferencesKey("initialized")] = true }
    }

    // The replacement build used a different database. Import once without deleting
    // either database or replacing the notes from the original Cherry Note build.
    private suspend fun recoverReplacementNotes() {
        val key = booleanPreferencesKey("replacement_recovered")
        if (store.data.first()[key] == true) return
        if (context.getDatabasePath("cherry_note.db").exists()) {
            val previous = CherryDatabase.getInstance(context)
            val collections = previous.collectionDao().observeAllCollections().first()
            val notes = previous.noteDao().observeAllNotes().first()
            db.withTransaction {
                val existingFolders = dao.folders().first().map { it.name }.toSet()
                collections.forEach { collection ->
                    if (collection.name !in existingFolders) dao.putFolder(Folder(collection.name, when (collection.iconName) {
                        "favorite" -> "♡"; "work" -> "↗"; "lightbulb" -> "✦"; "auto_stories" -> "☾"; "folder" -> "✦"; else -> collection.iconName
                    }, recoveredColor(collection.colorHex)))
                }
                notes.filterNot { it.isDeleted }.forEach { old ->
                    val items = old.checklistItems.joinToString("\n") { (if (it.isDone) "☑ " else "☐ ") + it.text }
                    dao.put(Note(
                        id = "recovered-${old.id}", title = old.title,
                        body = listOf(old.content, items).filter { it.isNotBlank() }.joinToString("\n"),
                        tags = old.tags.joinToString(", "),
                        folder = collections.find { it.id == old.collectionId }?.name ?: "Personal",
                        color = recoveredColor(old.colorHex), pinned = old.isPinned,
                        checklist = old.checklistItems.isNotEmpty(), updated = old.updatedAt
                    ))
                }
            }
        }
        store.edit { it[key] = true }
    }
}
internal fun recoveredColor(hex: String) = when (hex.uppercase()) {
    "#E8F5E9", "#E0F7FA", "#00BCD4", "#3F51B5" -> 2
    "#F3E5F5", "#EDE7F6", "#9C27B0" -> 3
    "#FFF3E0" -> 4
    "#F3EBDF" -> 1
    else -> 0
}

class CherryViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = NoteRepository(app)
    val notes = repo.notes.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val folders = repo.folders.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val appearance = repo.appearance.map<Appearance, Appearance?> { it }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    init { viewModelScope.launch { repo.seed() } }
    fun save(note: Note, onSaved: () -> Unit = {}) { viewModelScope.launch { repo.save(note); onSaved() } }
    fun delete(note: Note) { viewModelScope.launch { repo.delete(note) } }
    fun folder(folder: Folder) { viewModelScope.launch { repo.folder(folder) } }
    fun preference(key: String, value: String) { viewModelScope.launch { repo.preference(key, value) } }
}
private val samples = listOf(
    Note(title = "Little things, lately", body = "Sunlight on the kitchen floor.\nA really good cup of coffee.\nSomeone remembering the little things.\n\nMore of this, please.", tags = "gratitude, slow living", folder = "Journal", color = 0, pinned = true),
    Note(title = "A softer kind of Sunday", body = "No alarms. Fresh flowers. A long walk with nowhere in particular to go.\n\nA reminder: rest is productive, too.", tags = "weekend", color = 1, pinned = true),
    Note(title = "Ideas worth keeping", body = "A tiny bookshop with a coffee corner.\nA playlist for every season.\nAn app that feels like a deep breath.", tags = "inspiration", folder = "Ideas", color = 3),
    Note(title = "The slow morning club", body = "☑ Open the windows\n☑ Make something warm\n☐ Ten minutes of journaling\n☐ Leave my phone behind", tags = "rituals", color = 2, checklist = true),
    Note(title = "Make room for wonder", body = "Take the long way home. Look up at the sky. Notice what makes you feel alive.", tags = "words to keep", folder = "Journal", color = 4),
    Note(title = "Next chapter", body = "Less rushing, more noticing.\nLess perfect, more present.\n\nStart where you are. Let it be small.", tags = "intentions", color = 0),
    Note(title = "Studio notes", body = "Explore warmer colors for the new identity. Keep the details intentional.\n\nReview concepts on Friday.", tags = "design", folder = "Work", color = 2)
)
