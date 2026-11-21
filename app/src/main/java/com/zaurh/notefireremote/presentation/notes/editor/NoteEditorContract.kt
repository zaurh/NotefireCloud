package com.zaurh.notefireremote.presentation.notes.editor

import androidx.annotation.StringRes
import com.zaurh.notefireremote.domain.model.NoteColor

data class NoteEditorState(
    val noteId: String,
    val isExisting: Boolean,
    val isLoading: Boolean = false,
    val title: String = "",
    val content: String = "",
    val color: NoteColor = NoteColor.Default,
    val isPinned: Boolean = false,
    val updatedAt: Long? = null,
    val isColorPickerVisible: Boolean = false,
)

sealed interface NoteEditorIntent {
    data class TitleChanged(val value: String) : NoteEditorIntent
    data class ContentChanged(val value: String) : NoteEditorIntent
    data class ColorSelected(val color: NoteColor) : NoteEditorIntent
    data object ToggleColorPicker : NoteEditorIntent
    data object TogglePin : NoteEditorIntent
    /** Back arrow or system back: saves changes, then closes. */
    data object Close : NoteEditorIntent
    data object Delete : NoteEditorIntent
}

sealed interface NoteEditorEffect {
    data object Close : NoteEditorEffect
    /** The notes list performs the delete so it can offer "Undo". */
    data class CloseAndDelete(val noteId: String) : NoteEditorEffect
    data class ShowError(@StringRes val messageRes: Int) : NoteEditorEffect
}
