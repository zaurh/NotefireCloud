package com.example.notefire_cloud.domain.repository

import com.example.notefire_cloud.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    /** Live list of the signed-in user's notes; switches automatically when the user changes. */
    fun observeNotes(): Flow<List<Note>>

    suspend fun getNote(id: String): Note?

    fun newNoteId(): String

    /**
     * Writes are fire-and-forget so they work offline: the local cache (and [observeNotes])
     * updates immediately and the server syncs when possible. Failures arrive on [writeErrors].
     */
    /** @param updateTimestamp false keeps the note's edit time (e.g. pinning, undoing a delete). */
    fun saveNote(note: Note, updateTimestamp: Boolean = true)
    fun deleteNote(id: String)

    val writeErrors: Flow<Throwable>

    /** Deletes every note of the signed-in user and waits for the server to confirm. */
    suspend fun deleteAllNotes(): Result<Unit>
}
