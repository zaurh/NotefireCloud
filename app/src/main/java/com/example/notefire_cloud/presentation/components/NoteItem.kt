package com.example.notefire_cloud.presentation.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.notefire_cloud.R
import com.example.notefire_cloud.common.NavParam
import com.example.notefire_cloud.common.myFormatDate
import com.example.notefire_cloud.common.navigateTo
import com.example.notefire_cloud.data.NoteData
import com.example.notefire_cloud.presentation.screens.viewmodel.NoteViewModel
import java.util.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteListItem(
    navController: NavController,
    noteData: NoteData,
    noteViewModel: NoteViewModel
) {
    val focus = LocalFocusManager.current
    var longClick by remember { mutableStateOf(false) }
    Card(modifier = Modifier
        .fillMaxSize()
        .padding(5.dp)
        .combinedClickable(
            onClick = {
                navigateTo(navController, "edit_note", NavParam("noteData", noteData))
                focus.clearFocus()
            },
            onLongClick = {
                longClick = !longClick
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .height(150.dp)
                .padding(5.dp)
        ) {

            Text(
                text = myFormatDate(noteData.time),
                fontSize = 12.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.size(10.dp))
            Text(
                text = noteData.noteTitle.toString(),
                fontWeight = FontWeight.Bold,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                fontSize = 18.sp
            )
            Text(
                text = noteData.noteDescription.toString(),
                overflow = TextOverflow.Ellipsis,
                color = Color.DarkGray
            )

        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            if (longClick) {
                Icon(
                    modifier = Modifier.clickable { noteViewModel.deleteNote(noteData.noteId!!) },
                    imageVector = Icons.Default.Delete,
                    contentDescription = "",
                    tint = colorResource(
                        id = R.color.blue,
                    )
                )
            }
        }
    }


}

