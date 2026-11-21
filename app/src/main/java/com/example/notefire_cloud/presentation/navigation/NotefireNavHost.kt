package com.example.notefire_cloud.presentation.navigation

import android.widget.Toast
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.notefire_cloud.presentation.auth.forgot.ForgotPasswordRoute as ForgotPasswordScreenRoute
import com.example.notefire_cloud.presentation.auth.signin.SignInRoute as SignInScreenRoute
import com.example.notefire_cloud.presentation.auth.signup.SignUpRoute as SignUpScreenRoute
import com.example.notefire_cloud.presentation.notes.editor.NoteEditorRoute as NoteEditorScreenRoute
import com.example.notefire_cloud.presentation.notes.list.NotesRoute as NotesScreenRoute

private const val ANIM_MS = 350

/** Back-stack result key: the editor asks the notes list to delete a note. */
private const val DELETE_NOTE_RESULT_KEY = "delete_note_id"

@Composable
fun NotefireNavHost(
    navController: NavHostController = rememberNavController(),
    sessionViewModel: SessionViewModel = hiltViewModel(),
) {
    val isSignedIn by sessionViewModel.isSignedIn.collectAsStateWithLifecycle()
    // Decided once; afterwards the LaunchedEffect below switches graphs on sign-in / sign-out.
    val startDestination: Any = remember { if (isSignedIn) NotesGraph else AuthGraph }
    val context = LocalContext.current

    LaunchedEffect(isSignedIn) {
        val target: Any = if (isSignedIn) NotesGraph else AuthGraph
        val alreadyThere = navController.currentBackStackEntry?.destination?.hierarchy
            ?.any { if (isSignedIn) it.hasRoute<NotesGraph>() else it.hasRoute<AuthGraph>() } == true
        if (!alreadyThere) {
            navController.navigate(target) {
                popUpTo(navController.graph.id) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = { slideIntoContainer(SlideDirection.Start, tween(ANIM_MS)) + fadeIn(tween(ANIM_MS)) },
        exitTransition = { slideOutOfContainer(SlideDirection.Start, tween(ANIM_MS), targetOffset = { it / 4 }) + fadeOut(tween(ANIM_MS)) },
        popEnterTransition = { slideIntoContainer(SlideDirection.End, tween(ANIM_MS), initialOffset = { it / 4 }) + fadeIn(tween(ANIM_MS)) },
        popExitTransition = { slideOutOfContainer(SlideDirection.End, tween(ANIM_MS)) + fadeOut(tween(ANIM_MS)) },
    ) {
        navigation<AuthGraph>(startDestination = SignInRoute) {
            composable<SignInRoute> {
                SignInScreenRoute(
                    onNavigateToSignUp = { navController.navigate(SignUpRoute) { launchSingleTop = true } },
                    onNavigateToForgotPassword = { email ->
                        navController.navigate(ForgotPasswordRoute(email)) { launchSingleTop = true }
                    },
                )
            }
            composable<SignUpRoute> {
                SignUpScreenRoute(onNavigateBack = { navController.popBackStack() })
            }
            composable<ForgotPasswordRoute> {
                ForgotPasswordScreenRoute(onNavigateBack = { navController.popBackStack() })
            }
        }

        navigation<NotesGraph>(startDestination = NotesRoute) {
            composable<NotesRoute> { entry ->
                val deleteRequestId by entry.savedStateHandle
                    .getStateFlow<String?>(DELETE_NOTE_RESULT_KEY, null)
                    .collectAsStateWithLifecycle()
                NotesScreenRoute(
                    onOpenNote = { id -> navController.navigate(NoteEditorRoute(id)) { launchSingleTop = true } },
                    deleteRequestId = deleteRequestId,
                    onDeleteRequestHandled = { entry.savedStateHandle[DELETE_NOTE_RESULT_KEY] = null },
                )
            }
            composable<NoteEditorRoute> {
                NoteEditorScreenRoute(
                    onClose = { navController.popBackStack() },
                    onCloseAndDelete = { id ->
                        navController.previousBackStackEntry?.savedStateHandle
                            ?.set(DELETE_NOTE_RESULT_KEY, id)
                        navController.popBackStack()
                    },
                    onShowMessage = { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() },
                )
            }
        }
    }
}
