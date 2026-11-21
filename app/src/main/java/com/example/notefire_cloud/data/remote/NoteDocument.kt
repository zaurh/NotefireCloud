package com.example.notefire_cloud.data.remote

import com.example.notefire_cloud.domain.model.Note
import com.example.notefire_cloud.domain.model.NoteColor
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import java.util.Date

/**
 * Firestore schema for a note. Field names match the documents written by v1 of the app,
 * so existing notes keep working. Mapping is done by hand (no reflection), which keeps it
 * safe under R8 minification.
 */
object NoteDocument {
    const val COLLECTION = "note"
    const val ID = "noteId"
    const val TITLE = "noteTitle"
    const val CONTENT = "noteDescription"
    const val USER_ID = "userId"
    const val TIME = "time"
    const val COLOR = "color"
    const val PINNED = "pinned"
}

fun DocumentSnapshot.toNote(): Note? {
    if (!exists()) return null
    val time = getTimestamp(NoteDocument.TIME, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
    return Note(
        id = id,
        title = getString(NoteDocument.TITLE).orEmpty(),
        content = getString(NoteDocument.CONTENT).orEmpty(),
        color = NoteColor.fromName(getString(NoteDocument.COLOR)),
        isPinned = getBoolean(NoteDocument.PINNED) ?: false,
        updatedAt = time?.toDate()?.time,
    )
}

fun Note.toDocument(userId: String, updateTimestamp: Boolean): Map<String, Any> = mapOf(
    NoteDocument.ID to id,
    NoteDocument.USER_ID to userId,
    NoteDocument.TITLE to title,
    NoteDocument.CONTENT to content,
    NoteDocument.COLOR to color.name,
    NoteDocument.PINNED to isPinned,
    NoteDocument.TIME to when {
        updateTimestamp || updatedAt == null -> FieldValue.serverTimestamp()
        else -> Timestamp(Date(updatedAt))
    },
)
