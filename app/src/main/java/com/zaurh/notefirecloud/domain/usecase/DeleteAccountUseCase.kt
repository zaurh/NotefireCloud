package com.zaurh.notefirecloud.domain.usecase

import com.zaurh.notefirecloud.domain.repository.AuthRepository
import com.zaurh.notefirecloud.domain.repository.NoteRepository
import javax.inject.Inject

/** Re-authenticates, wipes the user's notes, then deletes the Firebase account itself. */
class DeleteAccountUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val noteRepository: NoteRepository,
) {
    suspend operator fun invoke(password: String): Result<Unit> =
        authRepository.reauthenticate(password)
            .mapCatching { noteRepository.deleteAllNotes().getOrThrow() }
            .mapCatching { authRepository.deleteCurrentUser().getOrThrow() }
}
