package com.example.notefire_cloud.presentation.util

import androidx.annotation.StringRes
import com.example.notefire_cloud.R
import com.example.notefire_cloud.domain.model.AppError
import com.example.notefire_cloud.domain.validation.EmailError
import com.example.notefire_cloud.domain.validation.PasswordError

@StringRes
fun AppError.messageRes(): Int = when (this) {
    AppError.InvalidCredentials -> R.string.error_invalid_credentials
    AppError.EmailInUse -> R.string.error_email_in_use
    AppError.WeakPassword -> R.string.error_weak_password
    AppError.InvalidEmail -> R.string.error_email_invalid
    AppError.Network -> R.string.error_network
    AppError.TooManyRequests -> R.string.error_too_many_requests
    AppError.PermissionDenied -> R.string.error_permission_denied
    AppError.NotFound -> R.string.note_not_found
    AppError.Unknown -> R.string.error_unknown
}

@StringRes
fun EmailError.messageRes(): Int = when (this) {
    EmailError.Empty -> R.string.error_email_empty
    EmailError.Invalid -> R.string.error_email_invalid
}

@StringRes
fun PasswordError.messageRes(): Int = when (this) {
    PasswordError.Empty -> R.string.error_password_empty
    PasswordError.TooShort -> R.string.error_password_short
    PasswordError.Mismatch -> R.string.error_password_mismatch
}
