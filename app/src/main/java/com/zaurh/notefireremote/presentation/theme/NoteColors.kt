package com.zaurh.notefireremote.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import com.zaurh.notefireremote.domain.model.NoteColor

/** Soft pastels in light mode, deep muted tones in dark mode — text stays readable on both. */
@Composable
@ReadOnlyComposable
fun NoteColor.containerColor(): Color {
    val dark = LocalDarkTheme.current
    return when (this) {
        NoteColor.Default -> MaterialTheme.colorScheme.surfaceContainerLow
        NoteColor.Red -> if (dark) Color(0xFF5C2B2E) else Color(0xFFFFD9D6)
        NoteColor.Orange -> if (dark) Color(0xFF5E3A1F) else Color(0xFFFFE0C2)
        NoteColor.Yellow -> if (dark) Color(0xFF574B1A) else Color(0xFFFFF3B8)
        NoteColor.Green -> if (dark) Color(0xFF2C4A33) else Color(0xFFD9F2D6)
        NoteColor.Teal -> if (dark) Color(0xFF1F4A4A) else Color(0xFFCCEEEA)
        NoteColor.Blue -> if (dark) Color(0xFF263D63) else Color(0xFFD8E5FF)
        NoteColor.Purple -> if (dark) Color(0xFF45305E) else Color(0xFFEADCFB)
        NoteColor.Pink -> if (dark) Color(0xFF5C2B4A) else Color(0xFFFCDDEC)
    }
}
