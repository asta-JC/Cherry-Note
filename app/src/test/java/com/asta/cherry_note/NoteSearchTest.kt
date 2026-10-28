package com.asta.cherry_note
import com.asta.cherry_note.data.Note
import org.junit.Assert.*
import org.junit.Test
class NoteSearchTest {
 private val note = Note(title="Sunday ritual", body="A quiet morning with coffee", tags="journal, gratitude")
 @Test fun searchMatchesTitleBodyAndTagsIgnoringCase() {
  assertTrue(matches(note,"SUNDAY")); assertTrue(matches(note,"coffee")); assertTrue(matches(note," gratitude "))
 }
 @Test fun blankQueryIncludesNotesAndUnmatchedQueryExcludesThem() {
  assertTrue(matches(note,"  ")); assertFalse(matches(note,"meeting"))
 }
}
