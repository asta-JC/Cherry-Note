package com.asta.cherry_note

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.asta.cherry_note.data.Folder
import com.asta.cherry_note.data.Note
import com.asta.cherry_note.data.NoteDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class NotePersistenceTest {
    @Test fun creatingEditingAndReopeningPreservesAllNoteFields() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val filename = "persistence-test-${UUID.randomUUID()}.db"
        fun open() = Room.databaseBuilder(context, NoteDatabase::class.java, filename).build()
        var db = open()
        try {
            val note = Note(title = "Original", body = "A thought")
            db.dao().put(note)
            assertEquals(note.id, db.dao().observe().first().single().id)
            val edited = note.copy(title = "Edited", body = "☑ Remember\n☐ Write", tags = "ideas, daily", folder = "Test journal", color = 3, pinned = true, checklist = true, bold = true)
            db.dao().putFolder(Folder("Test journal", "☾", 3))
            db.dao().put(edited)
            assertEquals(1, db.dao().observe().first().size)
            db.close()
            db = open()
            assertEquals(edited, db.dao().observe().first().single())
            assertEquals(Folder("Test journal", "☾", 3), db.dao().folders().first().single())
            db.dao().delete(edited)
            assertTrue(db.dao().observe().first().isEmpty())
        } finally {
            db.close()
            context.deleteDatabase(filename)
        }
    }
}
