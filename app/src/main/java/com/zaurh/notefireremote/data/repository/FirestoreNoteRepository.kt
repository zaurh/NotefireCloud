package com.zaurh.notefireremote.data.repository

import com.zaurh.notefireremote.core.di.ApplicationScope
import com.zaurh.notefireremote.data.remote.NoteDocument
import com.zaurh.notefireremote.data.remote.toAppException
import com.zaurh.notefireremote.data.remote.toDocument
import com.zaurh.notefireremote.data.remote.toNote
import com.zaurh.notefireremote.domain.model.AppError
import com.zaurh.notefireremote.domain.model.AppException
import com.zaurh.notefireremote.domain.model.Note
import com.zaurh.notefireremote.domain.repository.AuthRepository
import com.zaurh.notefireremote.domain.repository.NoteRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Singleton
class FirestoreNoteRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val authRepository: AuthRepository,
    @ApplicationScope private val appScope: CoroutineScope,
) : NoteRepository {

    private val notes get() = firestore.collection(NoteDocument.COLLECTION)

    private val _writeErrors = MutableSharedFlow<Throwable>(extraBufferCapacity = 8)
    override val writeErrors: Flow<Throwable> = _writeErrors.asSharedFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeNotes(): Flow<List<Note>> = authRepository.observeAuthState()
        .map { it?.uid }
        .distinctUntilChanged()
        // A fresh Firestore listener per user: no stale or duplicate listeners after
        // sign-out / sign-in, and nothing is ever queried without a user.
        .flatMapLatest { uid -> if (uid == null) flowOf(emptyList()) else observeNotesOf(uid) }

    private fun observeNotesOf(uid: String): Flow<List<Note>> = callbackFlow {
        val registration = notes
            .whereEqualTo(NoteDocument.USER_ID, uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error.toAppException())
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    trySend(
                        snapshot.documents
                            .mapNotNull { it.toNote() }
                            .sortedByDescending { it.updatedAt ?: Long.MAX_VALUE }
                    )
                }
            }
        awaitClose { registration.remove() }
    }

    override suspend fun getNote(id: String): Note? = try {
        notes.document(id).get().await().toNote()
    } catch (e: Exception) {
        throw e.toAppException()
    }

    override fun newNoteId(): String = notes.document().id

    override fun saveNote(note: Note, updateTimestamp: Boolean) {
        val uid = authRepository.currentUser?.uid
        if (uid == null) {
            _writeErrors.tryEmit(AppException(AppError.PermissionDenied))
            return
        }
        notes.document(note.id)
            .set(note.toDocument(userId = uid, updateTimestamp = updateTimestamp), SetOptions.merge())
            .reportFailures()
    }

    override fun deleteNote(id: String) {
        notes.document(id).delete().reportFailures()
    }

    override suspend fun deleteAllNotes(): Result<Unit> = firebaseCall {
        val uid = authRepository.currentUser?.uid ?: throw AppException(AppError.PermissionDenied)
        val documents = notes.whereEqualTo(NoteDocument.USER_ID, uid).get().await().documents
        // Firestore batches are limited to 500 operations.
        documents.chunked(450).forEach { chunk ->
            firestore.batch().apply { chunk.forEach { delete(it.reference) } }.commit().await()
        }
    }

    /**
     * The write is already applied to the local cache (so the UI updates instantly, even
     * offline); here we only wait for the server's verdict to surface rejections.
     */
    private fun Task<Void>.reportFailures() {
        appScope.launch {
            try {
                await()
            } catch (e: Exception) {
                _writeErrors.emit(e.toAppException())
            }
        }
    }
}
