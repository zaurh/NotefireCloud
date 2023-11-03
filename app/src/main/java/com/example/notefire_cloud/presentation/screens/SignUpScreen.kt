package com.example.notefire_cloud.presentation.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
//import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.notefire_cloud.R
import com.example.notefire_cloud.common.MyCheckSignedIn
import com.example.notefire_cloud.presentation.components.MyProgressBar
import com.example.notefire_cloud.presentation.screens.auth.components.AuthErrorMessage
import com.example.notefire_cloud.presentation.screens.auth.components.AuthPassTrailingIcon
import com.example.notefire_cloud.presentation.screens.auth.components.AuthTextField
import com.example.notefire_cloud.presentation.screens.viewmodel.AuthViewModel

@Composable
fun SignUpScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    MyCheckSignedIn(navController = navController, authViewModel = authViewModel)

    val context = LocalContext.current
    val isLoading = authViewModel.isAuthLoading.value
    val focus = LocalFocusManager.current

    var emailTf by remember { mutableStateOf("") }
    var emailTfError by remember { mutableStateOf(false) }

    var passwordTf by remember { mutableStateOf("") }
    var passwordTfError by remember { mutableStateOf(false) }

    var confirmPasswordTf by remember { mutableStateOf("") }
    var confirmPasswordTfError by remember { mutableStateOf(false) }

    var passwordVisibility by remember { mutableStateOf(false) }
    var confirmPasswordVisibility by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = Color.White
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
                painter = painterResource(id = R.drawable.notefire_logo),
                contentDescription = "",
                modifier = Modifier.size(150.dp)
            )
            Spacer(modifier = Modifier.size(40.dp))
            Text(
                text = "Sign up",
                fontSize = 30.sp,
                fontFamily = FontFamily.Serif,
                color = Color.DarkGray
            )
            Spacer(modifier = Modifier.size(40.dp))

            AuthTextField(
                errorTf = emailTfError,
                value = emailTf,
                onValueChange = { emailTf = it },
                onDone = {
                    focus.clearFocus()
                },
                placeHolder = "Email",
                leadingIcon = Icons.Default.Email
            )
            if (emailTfError) {
                AuthErrorMessage(text = "Please enter email.")
            }
            Spacer(modifier = Modifier.size(8.dp))
            AuthTextField(
                errorTf = passwordTfError,
                value = passwordTf,
                onValueChange = { passwordTf = it },
                onDone = {
                    focus.clearFocus()
                },
                placeHolder = "Password",
                leadingIcon = Icons.Default.Lock,
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
            AuthTextField(
                errorTf = confirmPasswordTfError,
                value = confirmPasswordTf,
                onValueChange = { confirmPasswordTf = it },
                onDone = {
                    focus.clearFocus()
                },
                placeHolder = "Confirm Password",
                leadingIcon = Icons.Default.Lock,
                passwordVisibility = if (confirmPasswordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    AuthPassTrailingIcon(
                        error = confirmPasswordTfError,
                        onClick = {
                            confirmPasswordVisibility = !confirmPasswordVisibility
                        },
                        visibility = confirmPasswordVisibility
                    )
                }
            )
            if (confirmPasswordTfError) {
                AuthErrorMessage(text = "Please enter password again.")
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
                    confirmPasswordTfError = confirmPasswordTf.isEmpty()
                    if (!emailTfError && !passwordTfError && !confirmPasswordTfError) {
                        authViewModel.signUp(
                            email = emailTf,
                            password = passwordTf,
                            confirmPassword = confirmPasswordTf,
                            context = context
                        )
                    }
                    focus.clearFocus()
                }) {
                Text(
                    text = "Sign up",
                    color = Color.White,
                    modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
                )
            }
            Spacer(modifier = Modifier.size(32.dp))
            Row {
                Text(text = "Already have an account? ", color = Color.DarkGray)
                Text(
                    text = "Sign In!",
                    textDecoration = TextDecoration.Underline,
                    color = colorResource(id = R.color.blue),
                    modifier = Modifier.clickable {
                        navController.popBackStack()
                    })
            }




        }

        if (isLoading) {
            MyProgressBar()
        }

    }
}
