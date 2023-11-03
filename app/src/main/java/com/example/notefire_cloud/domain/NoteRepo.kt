package com.example.notefire_cloud.domain

import androidx.compose.runtime.mutableStateOf
import com.example.notefire_cloud.data.NoteData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObjects
import java.util.UUID
import javax.inject.Inject

class NoteRepo @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    val noteData = mutableStateOf<List<NoteData>>(listOf())

    init {
        getNotes()
    }


    fun addNote(noteData: NoteData) {
        val randomId = UUID.randomUUID().toString()
        val noteDataSet = noteData.copy(
            noteId = randomId
        )
        firestore.collection("note").document(randomId).set(noteDataSet)
    }


    fun getNotes() {
        val currentUserId = auth.currentUser?.uid
        if (currentUserId != null) {
            firestore.collection("note")
                .whereEqualTo("userId", currentUserId)
                .addSnapshotListener { value, _ ->
                    value?.let { it ->
                        noteData.value = it.toObjects<NoteData>().sortedByDescending { it.time }
                    }
                }
        }
    }

    fun deleteNote(noteId: String) {
        firestore.collection("note").document(noteId).delete()
    }

}