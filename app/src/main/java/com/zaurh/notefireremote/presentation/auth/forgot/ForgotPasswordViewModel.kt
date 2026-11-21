package com.zaurh.notefireremote.presentation.auth.forgot

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaurh.notefireremote.R
import com.zaurh.notefireremote.core.mvi.MviViewModel
import com.zaurh.notefireremote.domain.model.toAppError
import com.zaurh.notefireremote.domain.repository.AuthRepository
import com.zaurh.notefireremote.domain.validation.AuthValidator
import com.zaurh.notefireremote.presentation.navigation.ForgotPasswordRoute
import com.zaurh.notefireremote.presentation.util.messageRes
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val authRepository: AuthRepository,
) : MviViewModel<ForgotPasswordState, ForgotPasswordIntent, ForgotPasswordEffect>(
    ForgotPasswordState(email = ForgotPasswordRoute.from(savedStateHandle).email)
) {

    override fun onIntent(intent: ForgotPasswordIntent) {
        when (intent) {
            is ForgotPasswordIntent.EmailChanged -> setState { copy(email = intent.value, emailError = null) }
            ForgotPasswordIntent.Submit -> submit()
        }
    }

    private fun submit() {
        val state = currentState
        if (state.isLoading) return
        val emailError = AuthValidator.validateEmail(state.email)
        if (emailError != null) {
            setState { copy(emailError = emailError) }
            return
        }
        setState { copy(isLoading = true) }
        viewModelScope.launch {
            authRepository.sendPasswordReset(state.email)
                .onSuccess { sendEffect(ForgotPasswordEffect.ShowMessage(R.string.reset_email_sent)) }
                .onFailure { sendEffect(ForgotPasswordEffect.ShowMessage(it.toAppError().messageRes())) }
            setState { copy(isLoading = false) }
        }
    }
}
