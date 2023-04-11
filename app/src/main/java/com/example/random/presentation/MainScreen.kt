package com.example.random.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.random.presentation.components.MyAlert
import com.example.random.presentation.components.NoteListItem

@Composable
fun MainScreen(
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val noteData = authViewModel.noteData.value
    var alert by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Main Page")
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(noteData) { note ->
                    NoteListItem(noteData = note)
                }
            }
            Button(onClick = {
                authViewModel.signOut()
                navController.navigate("sign_in")
            }) {
                Text(text = "Sign Out")
            }
            Button(onClick = {
                authViewModel.deleteUser()
                navController.navigate("sign_in")
            }) {
                Text(text = "Delete")
            }
            IconButton(onClick = {
                alert = true
            }) {
                Icon(imageVector = Icons.Default.AddCircle, contentDescription = "")
            }
        }
    }
    var tf by remember {
        mutableStateOf(TextFieldValue(""))
    }
    if (alert) {
        MyAlert(
            onDismiss = { alert = false },
            textFieldValue = tf,
            onValueChange = { tf = it },
            onButtonClick = { authViewModel.addNote(tf.text) }
        )
    }
}