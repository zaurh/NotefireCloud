package com.example.notefire_cloud.common


import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.navigation.NavController
import com.example.notefire_cloud.presentation.screens.viewmodel.AuthViewModel
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

//Check If Already Signed In or Not
@Composable
fun MyCheckSignedIn(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val alreadyLoggedIn = remember { mutableStateOf(false) }
    val signedIn = authViewModel.isSignedIn.value
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


data class NavParam(
    val name: String,
    val value: Parcelable
)

fun navigateTo(navController: NavController, dest: String, vararg params: NavParam) {
    for (param in params) {
        navController.currentBackStackEntry?.savedStateHandle?.set(param.name, param.value)
    }
    navController.navigate(dest) {
        popUpTo(dest)
        launchSingleTop = true
    }
}


