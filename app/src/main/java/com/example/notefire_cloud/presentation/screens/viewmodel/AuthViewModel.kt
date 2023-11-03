package com.example.notefire_cloud.presentation.screens.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.notefire_cloud.domain.AuthRepo
import com.example.notefire_cloud.domain.NoteRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepo: AuthRepo,
    private val noteRepo: NoteRepo
) : ViewModel() {

    val isAuthLoading = authRepo.isAuthLoading
    val isSignedIn = authRepo.isSignedIn
    val currentUserId = authRepo.currentUserId
    val loginSuccess = mutableStateOf(false)


    fun signUp(email: String, password: String, confirmPassword: String, context: Context) {
        authRepo.signUp(
            email, password, confirmPassword, context
        )
    }

    fun signIn(email: String, password: String, context: Context) {
        authRepo.signIn(email, password, context){
            loginSuccess.value = true
        }
    }

    fun signOut() {
        authRepo.signOut()
    }

    fun forgotPassword(email: String, context: Context) {
        authRepo.forgotPassword(email, context)
    }


}

