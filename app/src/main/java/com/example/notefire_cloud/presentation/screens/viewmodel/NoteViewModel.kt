package com.example.notefire_cloud.presentation.screens.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notefire_cloud.data.NoteData
import com.example.notefire_cloud.domain.NoteRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val noteRepo: NoteRepo
) : ViewModel() {

    val noteData = noteRepo.noteData

    private var initialNoteList = listOf<NoteData>()
    private var isSearchStarting = true

    fun getNotes(){
        noteRepo.getNotes()
    }

    fun addNote(noteData: NoteData) {
        noteRepo.addNote(noteData)
    }


    fun deleteNote(noteId: String) {
        noteRepo.deleteNote(noteId)
    }

    fun searchNotes(query: String) {
        val listToSearch = if (isSearchStarting) {
            noteData.value
        } else {
            initialNoteList
        }
        viewModelScope.launch(Dispatchers.Default) {
            if (query.isEmpty()) {
                noteData.value = initialNoteList
                isSearchStarting = true
                return@launch
            }

            val results = listToSearch.filter {
                it.noteTitle?.contains(
                    query.trim(),
                    ignoreCase = true
                ) == true || it.noteDescription!!.contains(query.trim(), ignoreCase = true)

            }

            if (isSearchStarting) {
                initialNoteList = noteData.value
                isSearchStarting = false
            }
            noteData.value = results
        }
    }

    fun clearSearch() {
        noteData.value = initialNoteList
        isSearchStarting = true
    }
}