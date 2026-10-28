package com.asta.cherry_note.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val LocalCherryDarkTheme = staticCompositionLocalOf { false }
val NoteColorNames = listOf("Cherry", "Sand", "Sage", "Lavender", "Honey")

private val lightNoteColors = listOf(
    Color(0xFFF7E4E7), Color(0xFFF3EBDF), Color(0xFFE7ECE3),
    Color(0xFFECE6F1), Color(0xFFF8EDD9)
)
private val darkNoteColors = listOf(
    Color(0xFF442733), Color(0xFF383026), Color(0xFF283A32),
    Color(0xFF353047), Color(0xFF443720)
)

// One palette is shared by cards, editors, collections and color pickers.
internal fun noteBackground(index: Int, dark: Boolean): Color {
    val palette = if (dark) darkNoteColors else lightNoteColors
    return palette[index.mod(palette.size)]
}
