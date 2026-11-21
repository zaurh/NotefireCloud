package com.zaurh.notefireremote.presentation.notes.editor

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zaurh.notefireremote.R
import com.zaurh.notefireremote.domain.model.NoteColor
import com.zaurh.notefireremote.presentation.theme.NotefireTheme
import com.zaurh.notefireremote.presentation.theme.containerColor
import com.zaurh.notefireremote.presentation.util.ObserveEffects
import com.zaurh.notefireremote.presentation.util.formatNoteDateTime

@Composable
fun NoteEditorRoute(
    onClose: () -> Unit,
    onCloseAndDelete: (noteId: String) -> Unit,
    onShowMessage: (String) -> Unit,
    viewModel: NoteEditorViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    ObserveEffects(viewModel.effects) { effect ->
        when (effect) {
            NoteEditorEffect.Close -> onClose()
            is NoteEditorEffect.CloseAndDelete -> onCloseAndDelete(effect.noteId)
            is NoteEditorEffect.ShowError -> onShowMessage(context.getString(effect.messageRes))
        }
    }

    BackHandler { viewModel.onIntent(NoteEditorIntent.Close) }

    NoteEditorScreen(state = state, onIntent = viewModel::onIntent)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditorScreen(
    state: NoteEditorState,
    onIntent: (NoteEditorIntent) -> Unit,
) {
    val background by animateColorAsState(state.color.containerColor(), label = "noteColor")
    val titleFocus = remember { FocusRequester() }
    val context = LocalContext.current

    if (!state.isExisting) {
        LaunchedEffect(Unit) { titleFocus.requestFocus() }
    }

    Scaffold(
        containerColor = background,
        topBar = {
            TopAppBar(
                title = {},
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                navigationIcon = {
                    IconButton(onClick = { onIntent(NoteEditorIntent.Close) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                actions = {
                    IconButton(onClick = { onIntent(NoteEditorIntent.TogglePin) }) {
                        Icon(
                            imageVector = if (state.isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                            contentDescription = stringResource(if (state.isPinned) R.string.unpin else R.string.pin),
                            tint = if (state.isPinned) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    IconButton(onClick = { onIntent(NoteEditorIntent.ToggleColorPicker) }) {
                        Icon(Icons.Outlined.Palette, contentDescription = stringResource(R.string.note_color))
                    }
                    if (state.isExisting) {
                        IconButton(onClick = { onIntent(NoteEditorIntent.Delete) }) {
                            Icon(Icons.Outlined.Delete, contentDescription = stringResource(R.string.delete))
                        }
                    }
                },
            )
        },
    ) { padding ->
        if (state.isLoading) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding), contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
            return@Scaffold
        }
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .consumeWindowInsets(padding)
                .imePadding()
        ) {
            AnimatedVisibility(
                visible = state.isColorPickerVisible,
                enter = expandVertically(),
                exit = shrinkVertically(),
            ) {
                ColorPicker(selected = state.color, onSelect = { onIntent(NoteEditorIntent.ColorSelected(it)) })
            }
            Column(
                Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
            ) {
                EditorField(
                    value = state.title,
                    onValueChange = { onIntent(NoteEditorIntent.TitleChanged(it)) },
                    placeholder = stringResource(R.string.title_hint),
                    textStyle = MaterialTheme.typography.headlineSmall,
                    singleLine = false,
                    imeAction = ImeAction.Next,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(titleFocus),
                )
                Spacer(Modifier.height(12.dp))
                EditorField(
                    value = state.content,
                    onValueChange = { onIntent(NoteEditorIntent.ContentChanged(it)) },
                    placeholder = stringResource(R.string.content_hint),
                    textStyle = MaterialTheme.typography.bodyLarge,
                    singleLine = false,
                    imeAction = ImeAction.Default,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 320.dp),
                )
            }
            state.updatedAt?.let { millis ->
                Text(
                    text = stringResource(R.string.edited_at, formatNoteDateTime(context, millis)),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(vertical = 12.dp),
                )
            }
        }
    }
}

@Composable
private fun EditorField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    textStyle: TextStyle,
    singleLine: Boolean,
    imeAction: ImeAction,
    modifier: Modifier = Modifier,
) {
    val color = MaterialTheme.colorScheme.onSurface
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = textStyle.copy(color = color),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        singleLine = singleLine,
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            imeAction = imeAction,
        ),
        modifier = modifier,
        decorationBox = { inner ->
            Box {
                if (value.isEmpty()) {
                    Text(placeholder, style = textStyle, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                }
                inner()
            }
        },
    )
}

@Composable
private fun ColorPicker(selected: NoteColor, onSelect: (NoteColor) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        NoteColor.entries.forEach { color ->
            val isSelected = color == selected
            Surface(
                color = color.containerColor(),
                shape = CircleShape,
                border = BorderStroke(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                ),
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable(onClickLabel = color.name) { onSelect(color) },
            ) {
                if (isSelected) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Outlined.Check,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NoteEditorScreenPreview() {
    NotefireTheme {
        NoteEditorScreen(
            state = NoteEditorState(
                noteId = "1", isExisting = true, title = "Groceries",
                content = "Milk\nEggs\nBread", color = NoteColor.Yellow,
                isColorPickerVisible = true, updatedAt = 0,
            ),
            onIntent = {},
        )
    }
}
