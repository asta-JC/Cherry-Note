package com.asta.cherry_note.data.local

import androidx.room.Dao
import androidx.room.Query
import com.asta.cherry_note.data.model.NoteEntity
import kotlinx.coroutines.flow.Flow

/** Read-only access to recover notes from the experimental database. */
@Dao
interface NoteDao {
    @Query("SELECT * FROM notes WHERE isDeleted = 0 ORDER BY isPinned DESC, updatedAt DESC")
    fun observeAllNotes(): Flow<List<NoteEntity>>
}
