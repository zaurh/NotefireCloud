package com.example.notefire_cloud.presentation.notes.list

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.automirrored.outlined.StickyNote2
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.notefire_cloud.R
import com.example.notefire_cloud.domain.model.Note
import com.example.notefire_cloud.domain.model.NoteColor
import com.example.notefire_cloud.presentation.components.EmptyState
import com.example.notefire_cloud.presentation.components.NoteCard
import com.example.notefire_cloud.presentation.theme.NotefireTheme
import com.example.notefire_cloud.presentation.util.ObserveEffects

@Composable
fun NotesRoute(
    onOpenNote: (noteId: String?) -> Unit,
    deleteRequestId: String?,
    onDeleteRequestHandled: () -> Unit,
    viewModel: NotesViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    // Result from the editor ("delete this note"), turned into a regular intent.
    LaunchedEffect(deleteRequestId) {
        if (deleteRequestId != null) {
            viewModel.onIntent(NotesIntent.DeleteNoteById(deleteRequestId))
            onDeleteRequestHandled()
        }
    }

    ObserveEffects(viewModel.effects) { effect ->
        when (effect) {
            NotesEffect.OpenNewNote -> onOpenNote(null)
            is NotesEffect.OpenNote -> onOpenNote(effect.noteId)
            is NotesEffect.ShowError -> snackbarHostState.showSnackbar(context.getString(effect.messageRes))
            is NotesEffect.ShowNoteDeleted -> {
                snackbarHostState.currentSnackbarData?.dismiss()
                val result = snackbarHostState.showSnackbar(
                    message = context.getString(R.string.note_deleted),
                    actionLabel = context.getString(R.string.undo),
                    duration = SnackbarDuration.Long,
                )
                if (result == SnackbarResult.ActionPerformed) {
                    viewModel.onIntent(NotesIntent.UndoDelete(effect.note))
                }
            }
        }
    }

    NotesScreen(state = state, onIntent = viewModel::onIntent, snackbarHostState = snackbarHostState)

    if (state.isAccountSheetVisible) {
        AccountSheet(
            email = state.userEmail,
            onDismiss = { viewModel.onIntent(NotesIntent.DismissAccountSheet) },
            onSignOut = { viewModel.onIntent(NotesIntent.SignOutClicked) },
            onDeleteAccount = { viewModel.onIntent(NotesIntent.DeleteAccountClicked) },
        )
    }
    state.deleteAccountDialog?.let { dialog ->
        DeleteAccountDialog(
            state = dialog,
            onPasswordChange = { viewModel.onIntent(NotesIntent.DeleteAccountPasswordChanged(it)) },
            onConfirm = { viewModel.onIntent(NotesIntent.ConfirmDeleteAccount) },
            onDismiss = { viewModel.onIntent(NotesIntent.DismissDeleteAccount) },
        )
    }
}

@Composable
fun NotesScreen(
    state: NotesState,
    onIntent: (NotesIntent) -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    val gridState = rememberLazyStaggeredGridState()
    val fabExpanded by remember { derivedStateOf { gridState.firstVisibleItemIndex == 0 } }
    val focusManager = LocalFocusManager.current

    BackHandler(enabled = state.isSearching) {
        focusManager.clearFocus()
        onIntent(NotesIntent.ClearQuery)
    }

    Scaffold(
        topBar = {
            NotesTopBar(
                query = state.query,
                email = state.userEmail,
                onQueryChange = { onIntent(NotesIntent.QueryChanged(it)) },
                onClearQuery = {
                    focusManager.clearFocus()
                    onIntent(NotesIntent.ClearQuery)
                },
                onAccountClick = { onIntent(NotesIntent.AccountClicked) },
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text(stringResource(R.string.new_note)) },
                icon = { Icon(Icons.Outlined.Edit, contentDescription = null) },
                onClick = {
                    focusManager.clearFocus()
                    onIntent(NotesIntent.NewNoteClicked)
                },
                expanded = fabExpanded,
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                state.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                state.isEmpty && state.isSearching -> EmptyState(
                    icon = Icons.Outlined.SearchOff,
                    title = stringResource(R.string.empty_search_title),
                    message = stringResource(R.string.empty_search_message),
                    modifier = Modifier.align(Alignment.Center),
                )
                state.isEmpty -> EmptyState(
                    icon = Icons.AutoMirrored.Outlined.StickyNote2,
                    title = stringResource(R.string.empty_notes_title),
                    message = stringResource(R.string.empty_notes_message),
                    modifier = Modifier.align(Alignment.Center),
                )
                else -> NotesGrid(state = state, gridState = gridState, onIntent = onIntent)
            }
        }
    }
}

@Composable
private fun NotesGrid(
    state: NotesState,
    gridState: LazyStaggeredGridState,
    onIntent: (NotesIntent) -> Unit,
) {
    val showHeaders = state.pinnedNotes.isNotEmpty()
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Adaptive(minSize = 160.dp),
        state = gridState,
        contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 4.dp, bottom = 96.dp),
        verticalItemSpacing = 10.dp,
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        if (showHeaders) {
            item(key = "header_pinned", span = StaggeredGridItemSpan.FullLine) {
                SectionHeader(stringResource(R.string.pinned))
            }
        }
        noteItems(state.pinnedNotes, onIntent)
        if (showHeaders && state.otherNotes.isNotEmpty()) {
            item(key = "header_others", span = StaggeredGridItemSpan.FullLine) {
                SectionHeader(stringResource(R.string.others))
            }
        }
        noteItems(state.otherNotes, onIntent)
    }
}

private fun androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope.noteItems(
    notes: List<Note>,
    onIntent: (NotesIntent) -> Unit,
) {
    items(notes, key = { it.id }) { note ->
        NoteCard(
            note = note,
            onClick = { onIntent(NotesIntent.NoteClicked(note.id)) },
            onTogglePin = { onIntent(NotesIntent.TogglePin(note)) },
            onDelete = { onIntent(NotesIntent.DeleteNote(note)) },
            modifier = Modifier.animateItem(),
        )
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(start = 4.dp, top = 12.dp, bottom = 2.dp),
    )
}

@Composable
private fun NotesTopBar(
    query: String,
    email: String?,
    onQueryChange: (String) -> Unit,
    onClearQuery: () -> Unit,
    onAccountClick: () -> Unit,
) {
    val focusManager = LocalFocusManager.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text(stringResource(R.string.search_notes)) },
            leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = onClearQuery) {
                        Icon(Icons.Outlined.Close, contentDescription = stringResource(R.string.clear_search))
                    }
                }
            },
            singleLine = true,
            shape = CircleShape,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
            modifier = Modifier.weight(1f),
        )
        Spacer(Modifier.width(8.dp))
        UserAvatar(
            email = email,
            size = 40,
            modifier = Modifier
                .clip(CircleShape)
                .clickable(onClickLabel = stringResource(R.string.account), onClick = onAccountClick),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NotesScreenPreview() {
    val notes = listOf(
        Note("1", "Groceries", "Milk, eggs, bread, coffee beans", NoteColor.Yellow, isPinned = true, updatedAt = 0),
        Note("2", "Ideas", "A note app that syncs everywhere.\nWith colors!", NoteColor.Blue, updatedAt = 0),
        Note("3", "", "Call mom on Sunday", updatedAt = 0),
        Note("4", "Books to read", "1. Clean Architecture\n2. Kotlin in Action", NoteColor.Green, updatedAt = 0),
    )
    NotefireTheme {
        NotesScreen(
            state = NotesState(isLoading = false, userEmail = "you@example.com").withNotes(notes),
            onIntent = {},
            snackbarHostState = SnackbarHostState(),
        )
    }
}
