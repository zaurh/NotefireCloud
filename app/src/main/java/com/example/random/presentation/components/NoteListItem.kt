package com.example.random.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.random.data.NoteData

@Composable
fun NoteListItem(
    noteData: NoteData
) {
    Box(modifier = Modifier.fillMaxSize()){
        Text(text = noteData.noteTitle.toString())
    }
}