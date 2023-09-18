package com.example.random.presentation

import android.content.Context
import android.util.Patterns
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.random.data.NoteData
import com.example.random.data.UserData
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObjects
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val fireStore: FirebaseFirestore
) : ViewModel() {

    val isLoading = mutableStateOf(false)
    val isSignedIn = mutableStateOf(false)
    val noteData = mutableStateOf<List<NoteData>>(listOf())

    private var initialNoteList = listOf<NoteData>()
    private var isSearchStarting = true

    init {
        isSignedIn.value = auth.currentUser != null
        getNotes()
    }

    //***********************  Firebase AUTH  ***********************//


    fun signUp(email: String, password: String, confirmPassword: String, context: Context) {
        isLoading.value = true
        if (!checkValidEmail(email)) {
            Toast.makeText(context, "Email is not valid.", Toast.LENGTH_SHORT).show()
            isLoading.value = false
        } else if (password.length < 8) {
            Toast.makeText(context, "Password should be at least 8 characters.", Toast.LENGTH_SHORT)
                .show()
            isLoading.value = false
        } else if (password != confirmPassword) {
            Toast.makeText(context, "Passwords don't match!", Toast.LENGTH_SHORT).show()
            isLoading.value = false
        } else {
            checkEmailExistence(email = email, context)
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    isLoading.value = false
                    isSignedIn.value = true
                    addUser(email)
                }
                .addOnFailureListener {
                    isLoading.value = false
                }
        }

    }

    fun signIn(email: String, password: String, context: Context) {
        isLoading.value = true
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                isLoading.value = false
                isSignedIn.value = true
            }
            .addOnFailureListener {
                isLoading.value = false
                Toast.makeText(context, "Email or password is incorrect", Toast.LENGTH_SHORT).show()
            }
    }

    fun signOut() {
        auth.signOut()
        isSignedIn.value = false
    }

    fun forgotPassword(email: String, context: Context) {
        auth.sendPasswordResetEmail(email).addOnSuccessListener {
            Toast.makeText(context, "Sent. Please check your email.", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(
                context,
                "Problem occurred. Please enter valid email.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    //*****************   Firebase AUTH Catching errors   *********************

    private fun checkEmailExistence(email: String, context: Context) {
        auth.fetchSignInMethodsForEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val result = task.result
                    if (result?.signInMethods?.isEmpty() == true) {
                        // Email does not exist
                    } else {
                        Toast.makeText(context, "Email is already registered.", Toast.LENGTH_SHORT)
                            .show()
                        isLoading.value = false
                    }
                } else {
                    // Error occurred
                }
            }
    }

    private fun checkValidEmail(email: String): Boolean {
        val emailPattern = Patterns.EMAIL_ADDRESS
        return emailPattern.matcher(email).matches()
    }


    //***********************    Firebase Firestore   **********************

    private fun addUser(email: String) {
        val userId = auth.currentUser?.uid
        val userdata = UserData(
            userId = userId,
            email = email
        )
        userId?.let { uid ->
            fireStore.collection("user").document(uid).set(userdata)
                .addOnSuccessListener {
                    isLoading.value = false
                }
        }
    }


    fun addNote(title: String, description: String) {
        val userId = auth.currentUser?.uid
        val randomId = UUID.randomUUID().toString()
        val noteData = NoteData(
            noteId = randomId,
            noteTitle = title,
            noteDescription = description,
            userId = userId,
            time = Timestamp.now()
        )
        fireStore.collection("note").document(randomId).set(noteData)
    }


    private fun getNotes() {
        val currentUserId = auth.currentUser?.uid
        if (currentUserId != null) {
            fireStore.collection("note")
                .whereEqualTo("userId", currentUserId)
                .addSnapshotListener { value, _ ->
                    value?.let { it ->
                        noteData.value = it.toObjects<NoteData>().sortedByDescending { it.time }
                    }
                }
        }
    }

    fun deleteNote(noteId: String) {
        fireStore.collection("note").document(noteId).delete()
    }

    fun searchNotes(query: String) {
        val listToSearch = if (isSearchStarting) {
            noteData.value
        } else {
            initialNoteList
        }
        viewModelScope.launch(Dispatchers.Default) {
            if (query.isEmpty()) {
                noteData.value = initialNoteList
                isSearchStarting = true
                return@launch
            }

            val results = listToSearch.filter {
                it.noteTitle!!.contains(
                    query.trim(),
                    ignoreCase = true
                ) || it.noteDescription!!.contains(query.trim(), ignoreCase = true)

            }

            if (isSearchStarting) {
                initialNoteList = noteData.value
                isSearchStarting = false
            }
            noteData.value = results
        }
    }

    fun clearSearch() {
        noteData.value = initialNoteList
        isSearchStarting = true
    }

}

