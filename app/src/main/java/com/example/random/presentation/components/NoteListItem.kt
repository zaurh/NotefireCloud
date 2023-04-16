package com.example.random.presentation.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key.Companion.Delete
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.random.R
import com.example.random.common.myFormatDate
import com.example.random.data.NoteData
import com.example.random.presentation.SharedViewModel
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteListItem(
    noteData: NoteData,
    navController: NavController,
    sharedViewModel: SharedViewModel = hiltViewModel(),
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    var longClick by remember { mutableStateOf(false) }
    Card(modifier = Modifier
        .fillMaxSize()
        .padding(5.dp)
        .combinedClickable(
            onClick = {
                onClick()
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
                    modifier = Modifier.clickable { sharedViewModel.deleteNote(noteData.noteId!!) },
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

