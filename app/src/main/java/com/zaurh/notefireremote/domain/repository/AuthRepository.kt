package com.zaurh.notefireremote.domain.repository

import com.zaurh.notefireremote.domain.model.AuthUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: AuthUser?

    /** Emits the signed-in user (or null) immediately and on every change. */
    fun observeAuthState(): Flow<AuthUser?>

    suspend fun signIn(email: String, password: String): Result<Unit>
    suspend fun signUp(email: String, password: String): Result<Unit>
    suspend fun sendPasswordReset(email: String): Result<Unit>
    suspend fun reauthenticate(password: String): Result<Unit>
    suspend fun deleteCurrentUser(): Result<Unit>
    fun signOut()
}
