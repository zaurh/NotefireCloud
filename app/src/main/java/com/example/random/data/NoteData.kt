package com.example.random.data

import com.google.firebase.Timestamp

data class NoteData(
    val noteId: String? = null,
    val noteTitle: String? = null,
    val noteDescription: String? = null,
    val userId: String? = null,
    val time: Timestamp = Timestamp.now()
){
    fun toMap() = mapOf(
        "noteTitle" to noteTitle,
        "noteDescription" to noteDescription
    )
}
