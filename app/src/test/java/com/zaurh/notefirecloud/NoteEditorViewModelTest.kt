package com.zaurh.notefirecloud

import androidx.lifecycle.SavedStateHandle
import com.zaurh.notefirecloud.domain.model.Note
import com.zaurh.notefirecloud.domain.model.NoteColor
import com.zaurh.notefirecloud.presentation.notes.editor.NoteEditorEffect
import com.zaurh.notefirecloud.presentation.notes.editor.NoteEditorIntent
import com.zaurh.notefirecloud.presentation.notes.editor.NoteEditorViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class NoteEditorViewModelTest {

    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    // Navigation stores route arguments in the SavedStateHandle under their property names.
    private fun viewModel(repo: FakeNoteRepository, noteId: String? = null) =
        NoteEditorViewModel(SavedStateHandle(mapOf("noteId" to noteId)), repo)

    @Test
    fun `new note is saved once with its user id-independent generated id`() = runTest {
        val repo = FakeNoteRepository()
        val vm = viewModel(repo)

        vm.onIntent(NoteEditorIntent.TitleChanged("Shopping"))
        vm.onIntent(NoteEditorIntent.ContentChanged("Milk"))
        vm.onIntent(NoteEditorIntent.Close)
        vm.onIntent(NoteEditorIntent.Close) // double back must not save twice

        assertEquals(1, repo.saved.size)
        val (note, touched) = repo.saved.single()
        assertEquals("new-0", note.id)
        assertEquals("Shopping", note.title)
        assertTrue(touched)
        assertEquals(NoteEditorEffect.Close, vm.effects.first())
    }

    @Test
    fun `empty new note is discarded`() = runTest {
        val repo = FakeNoteRepository()
        val vm = viewModel(repo)

        vm.onIntent(NoteEditorIntent.TitleChanged("   "))
        vm.onIntent(NoteEditorIntent.Close)

        assertTrue(repo.saved.isEmpty())
        assertEquals(NoteEditorEffect.Close, vm.effects.first())
    }

    @Test
    fun `editing keeps the same id instead of delete plus add`() = runTest {
        val existing = Note("abc", "Old", "Body", updatedAt = 1L)
        val repo = FakeNoteRepository(listOf(existing))
        val vm = viewModel(repo, noteId = "abc")

        assertEquals("Old", vm.state.value.title)
        vm.onIntent(NoteEditorIntent.TitleChanged("New"))
        vm.onIntent(NoteEditorIntent.Close)

        assertTrue(repo.deleted.isEmpty())
        assertEquals("abc", repo.saved.single().first.id)
        assertEquals("New", repo.saved.single().first.title)
    }

    @Test
    fun `changing only the color keeps the edit time`() = runTest {
        val repo = FakeNoteRepository(listOf(Note("abc", "T", "C", updatedAt = 1L)))
        val vm = viewModel(repo, noteId = "abc")

        vm.onIntent(NoteEditorIntent.ColorSelected(NoteColor.Green))
        vm.onIntent(NoteEditorIntent.Close)

        val (note, touched) = repo.saved.single()
        assertEquals(NoteColor.Green, note.color)
        assertFalse(touched)
    }

    @Test
    fun `unchanged note is not rewritten`() = runTest {
        val repo = FakeNoteRepository(listOf(Note("abc", "T", "C")))
        val vm = viewModel(repo, noteId = "abc")

        vm.onIntent(NoteEditorIntent.Close)

        assertTrue(repo.saved.isEmpty())
    }

    @Test
    fun `clearing an existing note asks the list to delete it`() = runTest {
        val repo = FakeNoteRepository(listOf(Note("abc", "T", "C")))
        val vm = viewModel(repo, noteId = "abc")

        vm.onIntent(NoteEditorIntent.TitleChanged(""))
        vm.onIntent(NoteEditorIntent.ContentChanged(""))
        vm.onIntent(NoteEditorIntent.Close)

        assertEquals(NoteEditorEffect.CloseAndDelete("abc"), vm.effects.first())
    }
}
