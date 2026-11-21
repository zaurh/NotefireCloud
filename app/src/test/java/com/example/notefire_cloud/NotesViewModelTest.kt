package com.example.notefire_cloud

import com.example.notefire_cloud.domain.model.Note
import com.example.notefire_cloud.domain.usecase.DeleteAccountUseCase
import com.example.notefire_cloud.presentation.notes.list.NotesEffect
import com.example.notefire_cloud.presentation.notes.list.NotesIntent
import com.example.notefire_cloud.presentation.notes.list.NotesViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class NotesViewModelTest {

    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    private val notes = listOf(
        Note("1", "Groceries", "milk", isPinned = true),
        Note("2", "Ideas", "Buy a guitar"),
        Note("3", "Work", "Standup at 10"),
    )

    private fun create(
        repo: FakeNoteRepository = FakeNoteRepository(notes),
        auth: FakeAuthRepository = FakeAuthRepository(),
    ) = NotesViewModel(repo, auth, DeleteAccountUseCase(auth, repo))

    @Test
    fun `notes are split into pinned and others`() {
        val state = create().state.value
        assertFalse(state.isLoading)
        assertEquals(listOf("1"), state.pinnedNotes.map { it.id })
        assertEquals(listOf("2", "3"), state.otherNotes.map { it.id })
    }

    @Test
    fun `search filters without losing notes added meanwhile`() {
        val repo = FakeNoteRepository(notes)
        val vm = create(repo)

        vm.onIntent(NotesIntent.QueryChanged("buy"))
        assertEquals(listOf("2"), vm.state.value.otherNotes.map { it.id })

        repo.saveNote(Note("4", "Buy shoes", ""))
        assertEquals(setOf("2", "4"), vm.state.value.otherNotes.map { it.id }.toSet())

        vm.onIntent(NotesIntent.ClearQuery)
        assertEquals(4, vm.state.value.allNotes.size)
        assertEquals(3, vm.state.value.otherNotes.size)
    }

    @Test
    fun `delete then undo restores the note`() = runTest {
        val repo = FakeNoteRepository(notes)
        val vm = create(repo)

        vm.onIntent(NotesIntent.DeleteNote(notes[1]))
        val effect = vm.effects.first() as NotesEffect.ShowNoteDeleted
        assertTrue(vm.state.value.allNotes.none { it.id == "2" })

        vm.onIntent(NotesIntent.UndoDelete(effect.note))
        assertTrue(vm.state.value.allNotes.any { it.id == "2" })
        assertFalse(repo.saved.last().second) // edit time preserved
    }

    @Test
    fun `delete requested by the editor is performed with undo`() = runTest {
        val repo = FakeNoteRepository(notes)
        val vm = create(repo)

        vm.onIntent(NotesIntent.DeleteNoteById("3"))

        assertEquals(listOf("3"), repo.deleted)
        assertEquals("3", (vm.effects.first() as NotesEffect.ShowNoteDeleted).note.id)
    }

    @Test
    fun `account deletion with wrong password shows an error`() {
        val vm = create()
        vm.onIntent(NotesIntent.DeleteAccountClicked)
        vm.onIntent(NotesIntent.DeleteAccountPasswordChanged("wrong"))
        vm.onIntent(NotesIntent.ConfirmDeleteAccount)

        val dialog = vm.state.value.deleteAccountDialog!!
        assertFalse(dialog.isDeleting)
        assertEquals(R.string.error_invalid_credentials, dialog.errorRes)
    }

    @Test
    fun `account deletion wipes notes and signs out`() {
        val repo = FakeNoteRepository(notes)
        val auth = FakeAuthRepository()
        val vm = create(repo, auth)
        vm.onIntent(NotesIntent.DeleteAccountClicked)
        vm.onIntent(NotesIntent.DeleteAccountPasswordChanged(auth.password))
        vm.onIntent(NotesIntent.ConfirmDeleteAccount)

        assertNull(vm.state.value.deleteAccountDialog)
        assertTrue(repo.notes.value.isEmpty())
        assertNull(auth.currentUser)
    }
}
