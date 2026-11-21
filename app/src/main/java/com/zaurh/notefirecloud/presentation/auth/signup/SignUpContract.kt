package com.zaurh.notefirecloud.presentation.auth.signup

import androidx.annotation.StringRes
import com.zaurh.notefirecloud.domain.validation.EmailError
import com.zaurh.notefirecloud.domain.validation.PasswordError

data class SignUpState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val emailError: EmailError? = null,
    val passwordError: PasswordError? = null,
    val confirmPasswordError: PasswordError? = null,
    val isLoading: Boolean = false,
)

sealed interface SignUpIntent {
    data class EmailChanged(val value: String) : SignUpIntent
    data class PasswordChanged(val value: String) : SignUpIntent
    data class ConfirmPasswordChanged(val value: String) : SignUpIntent
    data object Submit : SignUpIntent
    data object SignInClicked : SignUpIntent
}

sealed interface SignUpEffect {
    data class ShowError(@StringRes val messageRes: Int) : SignUpEffect
    data object NavigateBack : SignUpEffect
}
