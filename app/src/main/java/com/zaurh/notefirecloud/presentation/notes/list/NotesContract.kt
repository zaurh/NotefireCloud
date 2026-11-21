package com.zaurh.notefirecloud.presentation.notes.list

import androidx.annotation.StringRes
import com.zaurh.notefirecloud.domain.model.Note

data class NotesState(
    val isLoading: Boolean = true,
    val allNotes: List<Note> = emptyList(),
    val query: String = "",
    val pinnedNotes: List<Note> = emptyList(),
    val otherNotes: List<Note> = emptyList(),
    val userEmail: String? = null,
    val isAccountSheetVisible: Boolean = false,
    val deleteAccountDialog: DeleteAccountDialogState? = null,
) {
    val isSearching: Boolean get() = query.isNotBlank()
    val isEmpty: Boolean get() = pinnedNotes.isEmpty() && otherNotes.isEmpty()

    /** Re-derives the visible sections whenever the notes or the query change. */
    fun withNotes(notes: List<Note> = allNotes, query: String = this.query): NotesState {
        val visible = notes.filter { it.matches(query) }
        return copy(
            allNotes = notes,
            query = query,
            pinnedNotes = visible.filter { it.isPinned },
            otherNotes = visible.filterNot { it.isPinned },
        )
    }
}

data class DeleteAccountDialogState(
    val password: String = "",
    @StringRes val errorRes: Int? = null,
    val isDeleting: Boolean = false,
)

sealed interface NotesIntent {
    data class QueryChanged(val query: String) : NotesIntent
    data object ClearQuery : NotesIntent
    data object NewNoteClicked : NotesIntent
    data class NoteClicked(val noteId: String) : NotesIntent
    data class TogglePin(val note: Note) : NotesIntent
    data class DeleteNote(val note: Note) : NotesIntent
    /** Sent when the editor asks for its note to be deleted (so "Undo" lives on this screen). */
    data class DeleteNoteById(val noteId: String) : NotesIntent
    data class UndoDelete(val note: Note) : NotesIntent

    data object AccountClicked : NotesIntent
    data object DismissAccountSheet : NotesIntent
    data object SignOutClicked : NotesIntent
    data object DeleteAccountClicked : NotesIntent
    data class DeleteAccountPasswordChanged(val password: String) : NotesIntent
    data object ConfirmDeleteAccount : NotesIntent
    data object DismissDeleteAccount : NotesIntent
}

sealed interface NotesEffect {
    data object OpenNewNote : NotesEffect
    data class OpenNote(val noteId: String) : NotesEffect
    data class ShowNoteDeleted(val note: Note) : NotesEffect
    data class ShowError(@StringRes val messageRes: Int) : NotesEffect
}
