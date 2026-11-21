package com.example.notefire_cloud.domain.model

/** Backend-agnostic error categories the UI knows how to explain to the user. */
enum class AppError {
    InvalidCredentials,
    EmailInUse,
    WeakPassword,
    InvalidEmail,
    Network,
    TooManyRequests,
    PermissionDenied,
    NotFound,
    Unknown,
}

class AppException(val error: AppError, cause: Throwable? = null) : Exception(error.name, cause)

fun Throwable.toAppError(): AppError = (this as? AppException)?.error ?: AppError.Unknown
