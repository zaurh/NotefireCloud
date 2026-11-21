package com.zaurh.notefirecloud.presentation.notes.list

import androidx.lifecycle.viewModelScope
import com.zaurh.notefirecloud.R
import com.zaurh.notefirecloud.core.mvi.MviViewModel
import com.zaurh.notefirecloud.domain.model.AppError
import com.zaurh.notefirecloud.domain.model.Note
import com.zaurh.notefirecloud.domain.model.toAppError
import com.zaurh.notefirecloud.domain.repository.AuthRepository
import com.zaurh.notefirecloud.domain.repository.NoteRepository
import com.zaurh.notefirecloud.domain.usecase.DeleteAccountUseCase
import com.zaurh.notefirecloud.presentation.util.messageRes
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val noteRepository: NoteRepository,
    private val authRepository: AuthRepository,
    private val deleteAccount: DeleteAccountUseCase,
) : MviViewModel<NotesState, NotesIntent, NotesEffect>(NotesState()) {

    init {
        noteRepository.observeNotes()
            .onEach { notes -> setState { withNotes(notes = notes).copy(isLoading = false) } }
            .catch { error ->
                setState { copy(isLoading = false) }
                sendEffect(NotesEffect.ShowError(error.toAppError().loadMessage()))
            }
            .launchIn(viewModelScope)

        noteRepository.writeErrors
            .onEach { sendEffect(NotesEffect.ShowError(it.toAppError().saveMessage())) }
            .launchIn(viewModelScope)

        authRepository.observeAuthState()
            .onEach { user -> setState { copy(userEmail = user?.email) } }
            .launchIn(viewModelScope)
    }

    override fun onIntent(intent: NotesIntent) {
        when (intent) {
            is NotesIntent.QueryChanged -> setState { withNotes(query = intent.query) }
            NotesIntent.ClearQuery -> setState { withNotes(query = "") }
            NotesIntent.NewNoteClicked -> sendEffect(NotesEffect.OpenNewNote)
            is NotesIntent.NoteClicked -> sendEffect(NotesEffect.OpenNote(intent.noteId))
            is NotesIntent.TogglePin ->
                noteRepository.saveNote(intent.note.copy(isPinned = !intent.note.isPinned), updateTimestamp = false)
            is NotesIntent.DeleteNote -> delete(intent.note)
            is NotesIntent.DeleteNoteById -> deleteById(intent.noteId)
            is NotesIntent.UndoDelete -> noteRepository.saveNote(intent.note, updateTimestamp = false)

            NotesIntent.AccountClicked -> setState { copy(isAccountSheetVisible = true) }
            NotesIntent.DismissAccountSheet -> setState { copy(isAccountSheetVisible = false) }
            NotesIntent.SignOutClicked -> {
                setState { copy(isAccountSheetVisible = false) }
                authRepository.signOut() // navigation reacts to the auth state change
            }
            NotesIntent.DeleteAccountClicked ->
                setState { copy(isAccountSheetVisible = false, deleteAccountDialog = DeleteAccountDialogState()) }
            is NotesIntent.DeleteAccountPasswordChanged -> setState {
                copy(deleteAccountDialog = deleteAccountDialog?.copy(password = intent.password, errorRes = null))
            }
            NotesIntent.ConfirmDeleteAccount -> confirmDeleteAccount()
            NotesIntent.DismissDeleteAccount -> setState {
                if (deleteAccountDialog?.isDeleting == true) this else copy(deleteAccountDialog = null)
            }
        }
    }

    private fun deleteById(id: String) {
        val note = currentState.allNotes.firstOrNull { it.id == id }
        if (note != null) delete(note) else noteRepository.deleteNote(id)
    }

    private fun delete(note: Note) {
        noteRepository.deleteNote(note.id)
        sendEffect(NotesEffect.ShowNoteDeleted(note))
    }

    private fun confirmDeleteAccount() {
        val dialog = currentState.deleteAccountDialog ?: return
        if (dialog.isDeleting) return
        if (dialog.password.isEmpty()) {
            setState { copy(deleteAccountDialog = dialog.copy(errorRes = R.string.error_password_empty)) }
            return
        }
        setState { copy(deleteAccountDialog = dialog.copy(isDeleting = true, errorRes = null)) }
        viewModelScope.launch {
            deleteAccount(dialog.password)
                .onSuccess { setState { copy(deleteAccountDialog = null) } } // navigation follows auth state
                .onFailure { error ->
                    setState {
                        copy(
                            deleteAccountDialog = deleteAccountDialog?.copy(
                                isDeleting = false,
                                errorRes = error.toAppError().messageRes(),
                            )
                        )
                    }
                }
        }
    }

    private fun AppError.loadMessage() = if (this == AppError.Unknown) R.string.notes_load_failed else messageRes()
    private fun AppError.saveMessage() = if (this == AppError.Unknown) R.string.note_save_failed else messageRes()
}
