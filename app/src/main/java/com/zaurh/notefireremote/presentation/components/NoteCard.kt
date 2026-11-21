package com.zaurh.notefireremote.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zaurh.notefireremote.R
import com.zaurh.notefireremote.domain.model.Note
import com.zaurh.notefireremote.domain.model.NoteColor
import com.zaurh.notefireremote.presentation.theme.containerColor
import com.zaurh.notefireremote.presentation.util.formatNoteDate

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteCard(
    note: Note,
    onClick: () -> Unit,
    onTogglePin: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val haptics = LocalHapticFeedback.current
    val context = LocalContext.current
    val shape = MaterialTheme.shapes.medium

    Box(modifier) {
        Surface(
            color = note.color.containerColor(),
            shape = shape,
            border = if (note.color == NoteColor.Default) {
                BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            } else null,
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        menuExpanded = true
                    },
                ),
        ) {
            Column(Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.Top) {
                    Text(
                        text = note.title.ifBlank { stringResource(R.string.untitled) },
                        style = MaterialTheme.typography.titleMedium,
                        color = if (note.title.isBlank()) {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        } else MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                    )
                    if (note.isPinned) {
                        Spacer(Modifier.width(8.dp))
                        Icon(
                            Icons.Filled.PushPin,
                            contentDescription = stringResource(R.string.pinned),
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
                if (note.content.isNotBlank()) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = note.content.trim(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 8,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                note.updatedAt?.let { millis ->
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = formatNoteDate(context, millis),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    )
                }
            }
        }
        DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
            DropdownMenuItem(
                text = { Text(stringResource(if (note.isPinned) R.string.unpin else R.string.pin)) },
                leadingIcon = { Icon(Icons.Outlined.PushPin, contentDescription = null) },
                onClick = {
                    menuExpanded = false
                    onTogglePin()
                },
            )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.delete)) },
                leadingIcon = { Icon(Icons.Outlined.Delete, contentDescription = null) },
                onClick = {
                    menuExpanded = false
                    onDelete()
                },
            )
        }
    }
}
