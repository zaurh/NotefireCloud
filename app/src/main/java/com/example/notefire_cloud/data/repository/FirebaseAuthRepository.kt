package com.example.notefire_cloud.data.repository

import com.example.notefire_cloud.data.remote.toAppException
import com.example.notefire_cloud.domain.model.AppError
import com.example.notefire_cloud.domain.model.AppException
import com.example.notefire_cloud.domain.model.AuthUser
import com.example.notefire_cloud.domain.repository.AuthRepository
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.tasks.await

@Singleton
class FirebaseAuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
) : AuthRepository {

    override val currentUser: AuthUser?
        get() = auth.currentUser?.toAuthUser()

    override fun observeAuthState(): Flow<AuthUser?> = callbackFlow {
        trySend(auth.currentUser?.toAuthUser())
        val listener = FirebaseAuth.AuthStateListener { trySend(it.currentUser?.toAuthUser()) }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }.distinctUntilChanged()

    override suspend fun signIn(email: String, password: String) = firebaseCall {
        auth.signInWithEmailAndPassword(email.trim(), password).await()
    }

    override suspend fun signUp(email: String, password: String) = firebaseCall {
        auth.createUserWithEmailAndPassword(email.trim(), password).await()
    }

    override suspend fun sendPasswordReset(email: String) = firebaseCall {
        auth.sendPasswordResetEmail(email.trim()).await()
    }

    override suspend fun reauthenticate(password: String) = firebaseCall {
        val user = auth.currentUser ?: throw AppException(AppError.PermissionDenied)
        val email = user.email ?: throw AppException(AppError.PermissionDenied)
        user.reauthenticate(EmailAuthProvider.getCredential(email, password)).await()
    }

    override suspend fun deleteCurrentUser() = firebaseCall {
        val user = auth.currentUser ?: throw AppException(AppError.PermissionDenied)
        user.delete().await()
    }

    override fun signOut() = auth.signOut()

    private fun FirebaseUser.toAuthUser() = AuthUser(uid = uid, email = email)
}

internal suspend fun firebaseCall(block: suspend () -> Unit): Result<Unit> = try {
    block()
    Result.success(Unit)
} catch (e: CancellationException) {
    throw e
} catch (e: Exception) {
    Result.failure(e.toAppException())
}
