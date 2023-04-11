package com.example.random.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
//import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.random.common.MyCheckSignedIn
import com.example.random.common.MyProgressBar

@Composable
fun SignInScreen(
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel()
){

    MyCheckSignedIn(navController = navController, viewModel = authViewModel)

    val isLoading = authViewModel.isLoading.value
    var emailtf by remember { mutableStateOf("zaur@gmail.com") }
    var passwordtf by remember { mutableStateOf("zaur1234") }
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Sign In")
        TextField(value = emailtf, onValueChange = {emailtf = it})
        TextField(value = passwordtf, onValueChange = {passwordtf = it})
        Button(onClick = {
            authViewModel.signIn(emailtf, passwordtf)
        }) {
            Text(text = "Click")
        }
        Text(text = "Go to Sign Up", modifier = Modifier.clickable {
            navController.navigate("sign_up")
        })
    }

    if (isLoading){
        MyProgressBar()
    }
}