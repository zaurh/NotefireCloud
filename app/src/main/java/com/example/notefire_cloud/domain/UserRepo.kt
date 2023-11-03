package com.example.notefire_cloud.domain

import androidx.compose.runtime.mutableStateOf
import com.example.notefire_cloud.data.UserData
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class UserRepo @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    val isUserLoading = mutableStateOf(false)

    fun addUser(userData: UserData) {
        userData.userId?.let {
            firestore.collection("user").document(it).set(userData)
                .addOnSuccessListener {
                    isUserLoading.value = false
                }
        }

    }
}