package com.example.notefire_cloud.presentation.auth.signup

import androidx.lifecycle.viewModelScope
import com.example.notefire_cloud.core.mvi.MviViewModel
import com.example.notefire_cloud.domain.model.toAppError
import com.example.notefire_cloud.domain.repository.AuthRepository
import com.example.notefire_cloud.domain.validation.AuthValidator
import com.example.notefire_cloud.presentation.util.messageRes
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : MviViewModel<SignUpState, SignUpIntent, SignUpEffect>(SignUpState()) {

    override fun onIntent(intent: SignUpIntent) {
        when (intent) {
            is SignUpIntent.EmailChanged -> setState { copy(email = intent.value, emailError = null) }
            is SignUpIntent.PasswordChanged -> setState { copy(password = intent.value, passwordError = null) }
            is SignUpIntent.ConfirmPasswordChanged ->
                setState { copy(confirmPassword = intent.value, confirmPasswordError = null) }
            SignUpIntent.Submit -> submit()
            SignUpIntent.SignInClicked -> sendEffect(SignUpEffect.NavigateBack)
        }
    }

    private fun submit() {
        val state = currentState
        if (state.isLoading) return
        val emailError = AuthValidator.validateEmail(state.email)
        val passwordError = AuthValidator.validateNewPassword(state.password)
        val confirmError = AuthValidator.validateConfirmation(state.password, state.confirmPassword)
        if (emailError != null || passwordError != null || confirmError != null) {
            setState {
                copy(emailError = emailError, passwordError = passwordError, confirmPasswordError = confirmError)
            }
            return
        }
        setState { copy(isLoading = true) }
        viewModelScope.launch {
            // On success Firebase signs the new user in and the app navigates to the notes.
            authRepository.signUp(state.email, state.password)
                .onFailure { sendEffect(SignUpEffect.ShowError(it.toAppError().messageRes())) }
            setState { copy(isLoading = false) }
        }
    }
}
