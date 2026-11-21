package com.example.notefire_cloud.presentation.auth.signin

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
class SignInViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : MviViewModel<SignInState, SignInIntent, SignInEffect>(SignInState()) {

    override fun onIntent(intent: SignInIntent) {
        when (intent) {
            is SignInIntent.EmailChanged -> setState { copy(email = intent.value, emailError = null) }
            is SignInIntent.PasswordChanged -> setState { copy(password = intent.value, passwordError = null) }
            SignInIntent.Submit -> submit()
            SignInIntent.ForgotPasswordClicked ->
                sendEffect(SignInEffect.NavigateToForgotPassword(currentState.email.trim()))
            SignInIntent.SignUpClicked -> sendEffect(SignInEffect.NavigateToSignUp)
        }
    }

    private fun submit() {
        val state = currentState
        if (state.isLoading) return
        val emailError = AuthValidator.validateEmail(state.email)
        val passwordError = AuthValidator.validateExistingPassword(state.password)
        if (emailError != null || passwordError != null) {
            setState { copy(emailError = emailError, passwordError = passwordError) }
            return
        }
        setState { copy(isLoading = true) }
        viewModelScope.launch {
            authRepository.signIn(state.email, state.password)
                .onFailure { sendEffect(SignInEffect.ShowError(it.toAppError().messageRes())) }
            setState { copy(isLoading = false) }
        }
    }
}
