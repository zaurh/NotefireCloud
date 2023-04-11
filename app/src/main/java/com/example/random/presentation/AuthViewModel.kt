package com.example.random.presentation

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.random.data.NoteData
import com.example.random.data.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val fireStore: FirebaseFirestore
) : ViewModel() {

    val isLoading = mutableStateOf(false)
    val isSignedIn = mutableStateOf(false)
    val userData = mutableStateOf<UserData?>(null)
    val noteData = mutableStateOf<List<NoteData>>(listOf())

    init {
        isSignedIn.value = auth.currentUser != null
        getNotes()
    }

    fun signUp(email: String, password: String) {
        isLoading.value = true
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                isLoading.value = false
                isSignedIn.value = true
                addUser()
            }
            .addOnFailureListener {
                isLoading.value = false
            }
    }

    fun signIn(email: String, password: String) {
        isLoading.value = true
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                isLoading.value = false
                isSignedIn.value = true
            }
            .addOnFailureListener {
                isLoading.value = false
            }
    }

    fun signOut() {
        auth.signOut()
        isSignedIn.value = false
    }

    fun deleteUser() {
        auth.currentUser?.delete()
        isSignedIn.value = false
    }

    private fun addUser() {
        val userId = auth.currentUser?.uid
        val userdata = UserData(
            userId = userId
        )
        auth.currentUser?.uid?.let { uid ->
            fireStore.collection("user").document(uid).set(userdata)
                .addOnSuccessListener {
                    isLoading.value = false
                }
        }
    }

    fun addNote(title: String) {
        val userId = auth.currentUser?.uid
        val randomId = UUID.randomUUID().toString()
        val noteData = NoteData(
            noteId = randomId,
            noteTitle = title,
            userId = userId,
            time = System.currentTimeMillis()
        )
        fireStore.collection("note").document(randomId).set(noteData)
    }

    private fun getNotes() {
        val currentUserId = auth.currentUser?.uid
        if (currentUserId != null) {
            fireStore.collection("note")
                .whereEqualTo("userId", currentUserId)
                .addSnapshotListener { value, error ->
                    value?.let { it ->
                        noteData.value = it.toObjects<NoteData>().sortedByDescending { it.time }
                    }
                }
        }
    }
}

