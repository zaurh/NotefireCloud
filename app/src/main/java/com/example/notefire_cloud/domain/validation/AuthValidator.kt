package com.example.notefire_cloud.domain.validation

enum class EmailError { Empty, Invalid }
enum class PasswordError { Empty, TooShort, Mismatch }

object AuthValidator {
    const val MIN_PASSWORD_LENGTH = 8

    private val emailRegex = Regex("^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,}$")

    fun validateEmail(email: String): EmailError? = when {
        email.isBlank() -> EmailError.Empty
        !emailRegex.matches(email.trim()) -> EmailError.Invalid
        else -> null
    }

    /** Sign-in only needs a non-empty password; length rules apply to new passwords. */
    fun validateExistingPassword(password: String): PasswordError? =
        if (password.isEmpty()) PasswordError.Empty else null

    fun validateNewPassword(password: String): PasswordError? = when {
        password.isEmpty() -> PasswordError.Empty
        password.length < MIN_PASSWORD_LENGTH -> PasswordError.TooShort
        else -> null
    }

    fun validateConfirmation(password: String, confirmation: String): PasswordError? = when {
        confirmation.isEmpty() -> PasswordError.Empty
        password != confirmation -> PasswordError.Mismatch
        else -> null
    }
}
