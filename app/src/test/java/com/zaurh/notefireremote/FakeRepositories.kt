package com.zaurh.notefireremote

import com.zaurh.notefireremote.domain.model.AuthUser
import com.zaurh.notefireremote.domain.model.Note
import com.zaurh.notefireremote.domain.repository.AuthRepository
import com.zaurh.notefireremote.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class FakeNoteRepository(initial: List<Note> = emptyList()) : NoteRepository {
    val notes = MutableStateFlow(initial)
    val saved = mutableListOf<Pair<Note, Boolean>>()
    val deleted = mutableListOf<String>()
    private var nextId = 0

    override fun observeNotes(): Flow<List<Note>> = notes
    override suspend fun getNote(id: String): Note? = notes.value.firstOrNull { it.id == id }
    override fun newNoteId(): String = "new-${nextId++}"

    override fun saveNote(note: Note, updateTimestamp: Boolean) {
        saved += note to updateTimestamp
        notes.update { list -> list.filterNot { it.id == note.id } + note }
    }

    override fun deleteNote(id: String) {
        deleted += id
        notes.update { list -> list.filterNot { it.id == id } }
    }

    override val writeErrors = MutableSharedFlow<Throwable>()
    override suspend fun deleteAllNotes(): Result<Unit> = Result.success(Unit).also { notes.value = emptyList() }
}

class FakeAuthRepository(user: AuthUser? = AuthUser("uid", "you@example.com")) : AuthRepository {
    val state = MutableStateFlow(user)
    var password = "password123"

    override val currentUser: AuthUser? get() = state.value
    override fun observeAuthState(): Flow<AuthUser?> = state
    override suspend fun signIn(email: String, password: String) = Result.success(Unit)
    override suspend fun signUp(email: String, password: String) = Result.success(Unit)
    override suspend fun sendPasswordReset(email: String) = Result.success(Unit)
    override suspend fun reauthenticate(password: String): Result<Unit> =
        if (password == this.password) Result.success(Unit)
        else Result.failure(com.zaurh.notefireremote.domain.model.AppException(
            com.zaurh.notefireremote.domain.model.AppError.InvalidCredentials))
    override suspend fun deleteCurrentUser() = Result.success(Unit).also { state.value = null }
    override fun signOut() { state.value = null }
}
