package com.example.notefire_cloud.presentation.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.notefire_cloud.R
import com.example.notefire_cloud.common.MyCheckSignedIn
import com.example.notefire_cloud.presentation.components.MyProgressBar
import com.example.notefire_cloud.presentation.screens.auth.components.AuthErrorMessage
import com.example.notefire_cloud.presentation.screens.auth.components.AuthPassTrailingIcon
import com.example.notefire_cloud.presentation.screens.auth.components.AuthTextField
import com.example.notefire_cloud.presentation.screens.viewmodel.AuthViewModel

@Composable
fun SignInScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {

    MyCheckSignedIn(navController = navController, authViewModel = authViewModel)

    val isLoading = authViewModel.isAuthLoading.value
    val focus = LocalFocusManager.current
    var emailTf by remember { mutableStateOf("") }
    var emailTfError by remember { mutableStateOf(false) }
    var passwordTf by remember { mutableStateOf("") }
    var passwordTfError by remember { mutableStateOf(false) }

    var passwordVisibility by remember { mutableStateOf(false) }
    val context = LocalContext.current


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Color.White
            )

    ) {

        Column(
            Modifier
                .fillMaxSize()
                .padding(30.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier.size(150.dp),
                painter = painterResource(id = R.drawable.notefire_logo),
                contentDescription = ""
            )
            Spacer(modifier = Modifier.size(40.dp))
            Text(
                text = "Welcome",
                fontSize = 30.sp,
                fontFamily = FontFamily.Serif,
                color = Color.DarkGray
            )
            Spacer(modifier = Modifier.size(40.dp))

            AuthTextField(
                value = emailTf,
                errorTf = emailTfError,
                onValueChange = { emailTf = it },
                onDone = {
                    focus.clearFocus()
                },
                placeHolder = "Email",
                leadingIcon = Icons.Default.Email,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                )
            )
            if (emailTfError) {
                AuthErrorMessage(text = "Please enter email.")
            }
            Spacer(modifier = Modifier.size(8.dp))

            AuthTextField(
                value = passwordTf,
                errorTf = passwordTfError,
                onValueChange = { passwordTf = it },
                onDone = {
                    emailTfError = emailTf.isEmpty()
                    passwordTfError = passwordTf.isEmpty()

                    if (!emailTfError && !passwordTfError) {
                        authViewModel.signIn(emailTf, passwordTf, context)
                    }
                    focus.clearFocus()
                },
                placeHolder = "Password",
                leadingIcon = Icons.Default.Lock,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                ),
                passwordVisibility = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    AuthPassTrailingIcon(
                        error = passwordTfError,
                        onClick = {
                            passwordVisibility = !passwordVisibility
                        },
                        visibility = passwordVisibility
                    )
                }
            )
            if (passwordTfError) {
                AuthErrorMessage(text = "Please enter password.")
            }

            Spacer(modifier = Modifier.size(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Text(
                    text = "Forgot password?",
                    color = colorResource(id = R.color.blue),
                    modifier = Modifier.clickable {
                        navController.navigate("forgot_password")
                    })
            }
            Spacer(modifier = Modifier.size(30.dp))
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(50)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.blue)
                ),
                onClick = {
                    emailTfError = emailTf.isEmpty()
                    passwordTfError = passwordTf.isEmpty()

                    if (!emailTfError && !passwordTfError) {
                        authViewModel.signIn(emailTf, passwordTf, context)
                    }
                    focus.clearFocus()
                }) {
                Text(
                    text = "Sign in",
                    color = Color.White,
                    modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
                )
            }
            Spacer(modifier = Modifier.size(30.dp))
            Row {
                Text(text = "Don't have an account?")
                Text(text = " Create one!",
                    textDecoration = TextDecoration.Underline,
                    color = colorResource(
                        id = R.color.blue
                    ),
                    modifier = Modifier.clickable {
                        navController.navigate("sign_up")
                    })
            }

        }

        if (isLoading) {
            MyProgressBar()
        }
    }
}