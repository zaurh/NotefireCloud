package com.example.random.common


import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.navigation.NavController
import com.example.random.presentation.SharedViewModel
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

//Check If Already Signed In or Not
@Composable
fun MyCheckSignedIn(
    navController: NavController,
    viewModel: SharedViewModel
) {
    val alreadyLoggedIn = remember { mutableStateOf(false) }
    val signedIn = viewModel.isSignedIn.value
    if (signedIn && !alreadyLoggedIn.value) {
        alreadyLoggedIn.value = true
        navController.navigate("main") {
            popUpTo(0)
        }
    }
}


//Formatting Date

fun myFormatDate(timestamp: Timestamp): String {
    val sdf = SimpleDateFormat("dd MMMM  HH:mm", Locale.getDefault())
    return sdf.format(timestamp.toDate())
}


