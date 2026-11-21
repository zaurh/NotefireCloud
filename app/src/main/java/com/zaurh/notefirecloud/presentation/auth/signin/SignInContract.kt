package com.zaurh.notefirecloud.presentation.auth.signin

import androidx.annotation.StringRes
import com.zaurh.notefirecloud.domain.validation.EmailError
import com.zaurh.notefirecloud.domain.validation.PasswordError

data class SignInState(
    val email: String = "",
    val password: String = "",
    val emailError: EmailError? = null,
    val passwordError: PasswordError? = null,
    val isLoading: Boolean = false,
)

sealed interface SignInIntent {
    data class EmailChanged(val value: String) : SignInIntent
    data class PasswordChanged(val value: String) : SignInIntent
    data object Submit : SignInIntent
    data object ForgotPasswordClicked : SignInIntent
    data object SignUpClicked : SignInIntent
}

sealed interface SignInEffect {
    data class ShowError(@StringRes val messageRes: Int) : SignInEffect
    data class NavigateToForgotPassword(val email: String) : SignInEffect
    data object NavigateToSignUp : SignInEffect
    // No "signed in" effect: the app's navigation reacts to the auth state itself.
}
