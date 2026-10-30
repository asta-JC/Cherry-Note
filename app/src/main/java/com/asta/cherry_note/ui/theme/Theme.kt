package com.asta.cherry_note.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import com.asta.cherry_note.data.Appearance

private val LightColors = lightColorScheme(
    primary = Color(0xFFAF294A), onPrimary = Color.White,
    primaryContainer = Color(0xFFF9DEE5), onPrimaryContainer = Color(0xFF652039),
    secondary = Color(0xFF78616B), onSecondary = Color.White,
    secondaryContainer = Color(0xFFF1E7E7), onSecondaryContainer = Color(0xFF422731),
    tertiary = Color(0xFF526C52), onTertiary = Color.White,
    tertiaryContainer = Color(0xFFE7ECE3), onTertiaryContainer = Color(0xFF283A32),
    background = Color(0xFFFCF8F5), onBackground = Color(0xFF422731),
    surface = Color(0xFFFFFCFA), onSurface = Color(0xFF422731),
    surfaceVariant = Color(0xFFF1E7E7), onSurfaceVariant = Color(0xFF78616B),
    surfaceDim = Color(0xFFE9DDDF), surfaceBright = Color(0xFFFFFCFA),
    surfaceContainerLowest = Color(0xFFFFFFFF), surfaceContainerLow = Color(0xFFFCF8F5),
    surfaceContainer = Color(0xFFF7EFF0), surfaceContainerHigh = Color(0xFFF1E7E7),
    surfaceContainerHighest = Color(0xFFE9DDDF),
    outline = Color(0xFF927780), outlineVariant = Color(0xFFE9DDDF),
    inverseSurface = Color(0xFF392A36), inverseOnSurface = Color(0xFFF9E8EE),
    inversePrimary = Color(0xFFFF9DB5), surfaceTint = Color(0xFFAF294A),
    error = Color(0xFFBA1A1A), onError = Color.White,
    errorContainer = Color(0xFFFFDAD6), onErrorContainer = Color(0xFF410002)
)
private val DarkColors = darkColorScheme(
    primary = Color(0xFFFF9DB5), onPrimary = Color(0xFF3B1528),
    primaryContainer = Color(0xFF502A3B), onPrimaryContainer = Color(0xFFFFD9E3),
    secondary = Color(0xFFCAB6C0), onSecondary = Color(0xFF392A36),
    secondaryContainer = Color(0xFF392A36), onSecondaryContainer = Color(0xFFF9E8EE),
    tertiary = Color(0xFFB7D0AC), onTertiary = Color(0xFF283A32),
    tertiaryContainer = Color(0xFF283A32), onTertiaryContainer = Color(0xFFE7ECE3),
    background = Color(0xFF1C151D), onBackground = Color(0xFFF9E8EE),
    surface = Color(0xFF261D27), onSurface = Color(0xFFF9E8EE),
    surfaceVariant = Color(0xFF392A36), onSurfaceVariant = Color(0xFFCAB6C0),
    surfaceDim = Color(0xFF1C151D), surfaceBright = Color(0xFF443540),
    surfaceContainerLowest = Color(0xFF160F17), surfaceContainerLow = Color(0xFF211922),
    surfaceContainer = Color(0xFF261D27), surfaceContainerHigh = Color(0xFF302430),
    surfaceContainerHighest = Color(0xFF392A36),
    outline = Color(0xFF9B828F), outlineVariant = Color(0xFF4B3845),
    inverseSurface = Color(0xFFF9E8EE), inverseOnSurface = Color(0xFF422731),
    inversePrimary = Color(0xFFAF294A), surfaceTint = Color(0xFFFF9DB5),
    error = Color(0xFFFFB4AB), onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A), onErrorContainer = Color(0xFFFFDAD6)
)

@Composable
fun CherryNoteTheme(appearance: Appearance = Appearance(), content: @Composable () -> Unit) {
    val dark = appearance.theme == "Dark" || (appearance.theme == "System" && isSystemInDarkTheme())
    CompositionLocalProvider(LocalCherryDarkTheme provides dark) {
        MaterialTheme(colorScheme = if (dark) DarkColors else LightColors, typography = Typography(), content = content)
    }
}
