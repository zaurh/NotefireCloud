package com.example.notefire_cloud

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.notefire_cloud.data.NoteData
import com.example.notefire_cloud.presentation.*
import com.example.notefire_cloud.presentation.screens.*
import com.example.notefire_cloud.presentation.screens.viewmodel.AuthViewModel
import com.example.notefire_cloud.presentation.screens.viewmodel.NoteViewModel
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyAnimatedNavigation()
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MyAnimatedNavigation() {

    val authViewModel = hiltViewModel<AuthViewModel>()
    val noteViewModel = hiltViewModel<NoteViewModel>()

    val navController = rememberAnimatedNavController()
    AnimatedNavHost(navController = navController, startDestination = "splash_screen") {
        composable("splash_screen") {
            SplashScreen(navController = navController)
        }
        composable("main",
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -300 },
                    animationSpec = tween(
                        durationMillis = 600,
                        easing = FastOutSlowInEasing
                    )
                ) +
                        fadeOut(animationSpec = tween(600))
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -300 },
                    animationSpec = tween(
                        durationMillis = 600,
                        easing = FastOutSlowInEasing
                    )
                ) + fadeIn(animationSpec = tween(600))
            }, enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -300 },
                    animationSpec = tween(
                        durationMillis = 600,
                        easing = FastOutSlowInEasing
                    )
                ) +
                        fadeIn(animationSpec = tween(600))
            }
        ) {
            MainScreen(
                navController = navController,
                noteViewModel = noteViewModel,
                authViewModel = authViewModel
            )
        }
        composable(
            "sign_up",
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { 300 },
                    animationSpec = tween(
                        durationMillis = 600,
                        easing = FastOutSlowInEasing
                    )
                ) +
                        fadeIn(animationSpec = tween(600))
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { 300 },
                    animationSpec = tween(
                        durationMillis = 600,
                        easing = FastOutSlowInEasing
                    )
                ) +
                        fadeOut(animationSpec = tween(600))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -300 },
                    animationSpec = tween(
                        durationMillis = 600,
                        easing = FastOutSlowInEasing
                    )
                ) +
                        fadeOut(animationSpec = tween(600))
            },
        ) {
            SignUpScreen(navController = navController, authViewModel = authViewModel)
        }
        composable("sign_in",
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -300 },
                    animationSpec = tween(
                        durationMillis = 600,
                        easing = FastOutSlowInEasing
                    )
                ) +
                        fadeOut(animationSpec = tween(600))
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -300 },
                    animationSpec = tween(
                        durationMillis = 600,
                        easing = FastOutSlowInEasing
                    )
                ) + fadeIn(animationSpec = tween(600))
            }, enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -300 },
                    animationSpec = tween(
                        durationMillis = 600,
                        easing = FastOutSlowInEasing
                    )
                ) +
                        fadeIn(animationSpec = tween(600))
            }) {
            SignInScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }
        composable(
            "add_note",
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { 300 },
                    animationSpec = tween(
                        durationMillis = 600,
                        easing = FastOutSlowInEasing
                    )
                ) +
                        fadeIn(animationSpec = tween(600))
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { 300 },
                    animationSpec = tween(
                        durationMillis = 600,
                        easing = FastOutSlowInEasing
                    )
                ) +
                        fadeOut(animationSpec = tween(600))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -300 },
                    animationSpec = tween(
                        durationMillis = 600,
                        easing = FastOutSlowInEasing
                    )
                ) +
                        fadeOut(animationSpec = tween(600))
            },
        ) {
            AddEditNoteScreen(
                navController = navController,
                noteViewModel = noteViewModel,
                authViewModel = authViewModel
            )
        }
        composable("edit_note", enterTransition = {
            slideInHorizontally(
                initialOffsetX = { 300 },
                animationSpec = tween(
                    durationMillis = 600,
                    easing = FastOutSlowInEasing
                )
            ) +
                    fadeIn(animationSpec = tween(600))
        },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { 300 },
                    animationSpec = tween(
                        durationMillis = 600,
                        easing = FastOutSlowInEasing
                    )
                ) +
                        fadeOut(animationSpec = tween(600))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -300 },
                    animationSpec = tween(
                        durationMillis = 600,
                        easing = FastOutSlowInEasing
                    )
                ) +
                        fadeOut(animationSpec = tween(600))
            }) {
            val noteData = navController.previousBackStackEntry?.savedStateHandle?.get<NoteData>("noteData")
            noteData?.let {
                AddEditNoteScreen(
                    navController = navController,
                    noteData = noteData,
                    noteViewModel = noteViewModel,
                    authViewModel = authViewModel
                )
            }
        }

        composable(
            "forgot_password",
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { 300 },
                    animationSpec = tween(
                        durationMillis = 600,
                        easing = FastOutSlowInEasing
                    )
                ) +
                        fadeIn(animationSpec = tween(600))
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { 300 },
                    animationSpec = tween(
                        durationMillis = 600,
                        easing = FastOutSlowInEasing
                    )
                ) +
                        fadeOut(animationSpec = tween(600))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -300 },
                    animationSpec = tween(
                        durationMillis = 600,
                        easing = FastOutSlowInEasing
                    )
                ) +
                        fadeOut(animationSpec = tween(600))
            },
        ) {
            ForgotPasswordScreen(
                authViewModel = authViewModel
            )
        }


    }
}