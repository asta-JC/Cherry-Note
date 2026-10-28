package com.asta.cherry_note.ui.theme
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import com.asta.cherry_note.data.Appearance
@Composable fun CherryNoteTheme(appearance: Appearance = Appearance(), content: @Composable () -> Unit) {
    val dark = appearance.theme == "Dark" || (appearance.theme == "System" && isSystemInDarkTheme())
    val accent = Color(0xFFAF294A)
    val nightAccent = Color(0xFFFF9DB5)
    val colors = if(dark) darkColorScheme(primary=nightAccent, onPrimary=Color(0xFF3B1528), background=Color(0xFF1C151D), surface=Color(0xFF261D27), onSurface=Color(0xFFF9E8EE), onBackground=Color(0xFFF9E8EE), surfaceVariant=Color(0xFF392A36), onSurfaceVariant=Color(0xFFCAB6C0)) else lightColorScheme(primary=accent, background=Color(0xFFFCF8F5), surface=Color(0xFFFFFCFA), onSurface=Color(0xFF422731), onBackground=Color(0xFF422731), surfaceVariant=Color(0xFFF1E7E7), onSurfaceVariant=Color(0xFF78616B), outlineVariant=Color(0xFFE9DDDF))
    val family = if(appearance.type == "Literary") androidx.compose.ui.text.font.FontFamily.Serif else androidx.compose.ui.text.font.FontFamily.SansSerif
    val base = Typography()
    CompositionLocalProvider(LocalCherryDarkTheme provides dark) {
        MaterialTheme(colorScheme=colors, typography=Typography(bodyLarge=base.bodyLarge.copy(fontFamily=family),bodyMedium=base.bodyMedium.copy(fontFamily=family)),content=content)
    }
}
