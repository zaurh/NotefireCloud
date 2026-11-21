package com.zaurh.notefireremote.data.remote

import com.zaurh.notefireremote.domain.model.AppError
import com.zaurh.notefireremote.domain.model.AppException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestoreException

fun Throwable.toAppException(): AppException {
    if (this is AppException) return this
    val error = when (this) {
        // WeakPassword extends InvalidCredentials, so it must be checked first.
        is FirebaseAuthWeakPasswordException -> AppError.WeakPassword
        is FirebaseAuthInvalidCredentialsException ->
            if (errorCode == "ERROR_INVALID_EMAIL") AppError.InvalidEmail else AppError.InvalidCredentials
        is FirebaseAuthInvalidUserException -> AppError.InvalidCredentials
        is FirebaseAuthUserCollisionException -> AppError.EmailInUse
        is FirebaseNetworkException -> AppError.Network
        is FirebaseTooManyRequestsException -> AppError.TooManyRequests
        is FirebaseFirestoreException -> when (code) {
            FirebaseFirestoreException.Code.PERMISSION_DENIED,
            FirebaseFirestoreException.Code.UNAUTHENTICATED -> AppError.PermissionDenied
            FirebaseFirestoreException.Code.UNAVAILABLE,
            FirebaseFirestoreException.Code.DEADLINE_EXCEEDED -> AppError.Network
            FirebaseFirestoreException.Code.NOT_FOUND -> AppError.NotFound
            else -> AppError.Unknown
        }
        else -> AppError.Unknown
    }
    return AppException(error, this)
}
