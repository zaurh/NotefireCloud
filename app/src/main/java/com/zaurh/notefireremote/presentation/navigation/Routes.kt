package com.zaurh.notefireremote.presentation.navigation

import androidx.lifecycle.SavedStateHandle
import kotlinx.serialization.Serializable

// Type-safe navigation destinations.
//
// ViewModels read arguments with the `from(handle)` helpers instead of `toRoute()`:
// Navigation stores each argument in the SavedStateHandle under its property name, and
// reading it directly keeps ViewModels unit-testable on the plain JVM (no Bundle needed).

@Serializable data object AuthGraph
@Serializable data object SignInRoute
@Serializable data object SignUpRoute

@Serializable
data class ForgotPasswordRoute(val email: String = "") {
    companion object {
        fun from(handle: SavedStateHandle) = ForgotPasswordRoute(handle.get<String>("email").orEmpty())
    }
}

@Serializable data object NotesGraph
@Serializable data object NotesRoute

/** [noteId] is null for a brand-new note. */
@Serializable
data class NoteEditorRoute(val noteId: String? = null) {
    companion object {
        fun from(handle: SavedStateHandle) = NoteEditorRoute(handle.get<String>("noteId"))
    }
}
