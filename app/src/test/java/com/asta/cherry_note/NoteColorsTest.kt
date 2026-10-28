package com.asta.cherry_note

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import com.asta.cherry_note.ui.theme.NoteColorNames
import com.asta.cherry_note.ui.theme.noteBackground
import org.junit.Assert.*
import org.junit.Test

class NoteColorsTest {
    @Test fun noteColorsRemainDistinctInBothThemes() {
        for (dark in listOf(false, true)) {
            val colors = NoteColorNames.indices.map { noteBackground(it, dark) }
            assertEquals(5, colors.toSet().size)
            NoteColorNames.indices.forEach { index ->
                assertNotEquals(noteBackground(index, false), noteBackground(index, true))
            }
        }
    }

    @Test fun allNoteColorsKeepReadableTextContrast() {
        for (dark in listOf(false, true)) {
            val text = if (dark) Color(0xFFF9E8EE) else Color(0xFF422731)
            NoteColorNames.indices.forEach { index ->
                val background = noteBackground(index, dark)
                val light = maxOf(text.luminance(), background.luminance())
                val dim = minOf(text.luminance(), background.luminance())
                assertTrue("${NoteColorNames[index]} dark=$dark", (light + .05f) / (dim + .05f) >= 4.5f)
            }
        }
    }
}
