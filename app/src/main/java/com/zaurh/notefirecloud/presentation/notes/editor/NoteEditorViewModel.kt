package com.zaurh.notefirecloud.presentation.notes.editor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaurh.notefirecloud.R
import com.zaurh.notefirecloud.core.mvi.MviViewModel
import com.zaurh.notefirecloud.domain.model.Note
import com.zaurh.notefirecloud.domain.model.toAppError
import com.zaurh.notefirecloud.domain.repository.NoteRepository
import com.zaurh.notefirecloud.presentation.navigation.NoteEditorRoute
import com.zaurh.notefirecloud.presentation.util.messageRes
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.launch

@HiltViewModel
class NoteEditorViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val noteRepository: NoteRepository,
) : MviViewModel<NoteEditorState, NoteEditorIntent, NoteEditorEffect>(
    initialState(savedStateHandle, noteRepository)
) {
    /** The note as it was when the editor opened; used to detect real changes. */
    private var original: Note? = null
    private var isClosing = false

    init {
        if (currentState.isExisting) loadNote()
    }

    override fun onIntent(intent: NoteEditorIntent) {
        when (intent) {
            is NoteEditorIntent.TitleChanged -> {
                savedStateHandle[KEY_TITLE] = intent.value
                setState { copy(title = intent.value) }
            }
            is NoteEditorIntent.ContentChanged -> {
                savedStateHandle[KEY_CONTENT] = intent.value
                setState { copy(content = intent.value) }
            }
            is NoteEditorIntent.ColorSelected -> setState { copy(color = intent.color) }
            NoteEditorIntent.ToggleColorPicker -> setState { copy(isColorPickerVisible = !isColorPickerVisible) }
            NoteEditorIntent.TogglePin -> setState { copy(isPinned = !isPinned) }
            NoteEditorIntent.Close -> saveAndClose()
            NoteEditorIntent.Delete -> {
                if (isClosing) return
                isClosing = true
                sendEffect(NoteEditorEffect.CloseAndDelete(currentState.noteId))
            }
        }
    }

    private fun loadNote() {
        setState { copy(isLoading = true) }
        viewModelScope.launch {
            val note = try {
                noteRepository.getNote(currentState.noteId)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                setState { copy(isLoading = false) }
                sendEffect(NoteEditorEffect.ShowError(e.toAppError().messageRes()))
                isClosing = true
                sendEffect(NoteEditorEffect.Close)
                return@launch
            }
            if (note == null) {
                sendEffect(NoteEditorEffect.ShowError(R.string.note_not_found))
                isClosing = true
                sendEffect(NoteEditorEffect.Close)
                return@launch
            }
            original = note
            setState {
                copy(
                    isLoading = false,
                    // A restored draft (after process death) wins over the stored note.
                    title = savedStateHandle.get<String>(KEY_TITLE) ?: note.title,
                    content = savedStateHandle.get<String>(KEY_CONTENT) ?: note.content,
                    color = note.color,
                    isPinned = note.isPinned,
                    updatedAt = note.updatedAt,
                )
            }
        }
    }

    private fun saveAndClose() {
        if (isClosing) return
        isClosing = true
        val state = currentState
        if (state.isLoading) {
            sendEffect(NoteEditorEffect.Close)
            return
        }
        val note = Note(
            id = state.noteId,
            title = state.title.trimEnd(),
            content = state.content.trimEnd(),
            color = state.color,
            isPinned = state.isPinned,
            updatedAt = state.updatedAt,
        )
        val before = original
        when {
            note.isBlank && !state.isExisting -> sendEffect(NoteEditorEffect.Close)
            note.isBlank -> sendEffect(NoteEditorEffect.CloseAndDelete(note.id))
            before == null -> {
                noteRepository.saveNote(note)
                sendEffect(NoteEditorEffect.Close)
            }
            else -> {
                val textChanged = note.title != before.title || note.content != before.content
                val metaChanged = note.color != before.color || note.isPinned != before.isPinned
                if (textChanged || metaChanged) {
                    noteRepository.saveNote(note, updateTimestamp = textChanged)
                }
                sendEffect(NoteEditorEffect.Close)
            }
        }
    }

    companion object {
        private const val KEY_TITLE = "draft_title"
        private const val KEY_CONTENT = "draft_content"
        private const val KEY_NEW_ID = "new_note_id"

        private fun initialState(handle: SavedStateHandle, repository: NoteRepository): NoteEditorState {
            val routeId = NoteEditorRoute.from(handle).noteId
            return if (routeId != null) {
                NoteEditorState(noteId = routeId, isExisting = true)
            } else {
                // Keep the generated id across process death so a note is never saved twice.
                val id = handle.get<String>(KEY_NEW_ID) ?: repository.newNoteId().also { handle[KEY_NEW_ID] = it }
                NoteEditorState(
                    noteId = id,
                    isExisting = false,
                    title = handle.get<String>(KEY_TITLE).orEmpty(),
                    content = handle.get<String>(KEY_CONTENT).orEmpty(),
                )
            }
        }
    }
}
