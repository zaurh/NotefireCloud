package com.example.notefire_cloud.presentation.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notefire_cloud.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/** App-wide session: the single source of truth for "is someone signed in?". */
@HiltViewModel
class SessionViewModel @Inject constructor(
    authRepository: AuthRepository,
) : ViewModel() {

    val isSignedIn: StateFlow<Boolean> = authRepository.observeAuthState()
        .map { it != null }
        .stateIn(viewModelScope, SharingStarted.Eagerly, authRepository.currentUser != null)
}
