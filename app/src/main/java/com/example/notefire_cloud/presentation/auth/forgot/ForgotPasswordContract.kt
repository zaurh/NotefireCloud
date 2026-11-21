package com.example.notefire_cloud.presentation.auth.forgot

import androidx.annotation.StringRes
import com.example.notefire_cloud.domain.validation.EmailError

data class ForgotPasswordState(
    val email: String = "",
    val emailError: EmailError? = null,
    val isLoading: Boolean = false,
)

sealed interface ForgotPasswordIntent {
    data class EmailChanged(val value: String) : ForgotPasswordIntent
    data object Submit : ForgotPasswordIntent
}

sealed interface ForgotPasswordEffect {
    data class ShowMessage(@StringRes val messageRes: Int) : ForgotPasswordEffect
}
