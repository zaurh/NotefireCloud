package com.example.notefire_cloud.presentation.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.notefire_cloud.data.NoteData
import com.example.notefire_cloud.presentation.screens.viewmodel.AuthViewModel
import com.example.notefire_cloud.presentation.screens.viewmodel.NoteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AddEditNoteScreen(
    navController: NavController,
    noteData: NoteData? = null,
    noteViewModel: NoteViewModel,
    authViewModel: AuthViewModel
) {
    val currentUserId = authViewModel.currentUserId
    val focus = LocalFocusManager.current
    var titleTf by remember { mutableStateOf("") }
    var descriptionTf by remember { mutableStateOf("") }
    var dropdownExpanded by remember { mutableStateOf(false) }

    if (noteData != null) {
        LaunchedEffect(key1 = Unit) {
            titleTf = noteData.noteTitle.toString()
            descriptionTf = noteData.noteDescription.toString()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    if (noteData != null) {
                        IconButton(onClick = {
                            dropdownExpanded = true
                        }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "",
                                tint = Color.White
                            )
                        }
                        DropdownMenu(
                            expanded = dropdownExpanded,
                            onDismissRequest = { dropdownExpanded = false },
                            modifier = Modifier
                                .padding(start = 15.dp, end = 15.dp, top = 10.dp, bottom = 10.dp)
                                .width(160.dp)
                        )
                        {
                            Row(
                                Modifier
                                    .fillMaxSize()
                                    .clickable {
                                        noteViewModel.deleteNote(noteData.noteId!!)
                                        navController.navigate("main") {
                                            popUpTo(0)
                                        }
                                        dropdownExpanded = false
                                    }) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = "")
                                Spacer(modifier = Modifier.size(10.dp))
                                Text(
                                    fontSize = 18.sp,
                                    text = "Delete",
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                },
                title = {
                    Text(
                        text = if (noteData != null) "Edit note" else "Add note",
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = colorResource(id = com.example.notefire_cloud.R.color.blue)
                ),
            )
        },
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    TextField(
                        singleLine = true,
                        placeholder = { Text(text = "Title") },
                        value = titleTf,
                        onValueChange = { titleTf = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        textStyle = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    )
                    Divider()
                    TextField(
                        placeholder = { Text(text = "Note") },
                        value = descriptionTf,
                        onValueChange = { descriptionTf = it },
                        modifier = Modifier
                            .background(Color.White)
                            .fillMaxSize()
                            .weight(1f),
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                        )
                    )


                }
                FloatingActionButton(
                    onClick = {
                        focus.clearFocus()
                        if (noteData != null) {
                            if (titleTf.isEmpty() && descriptionTf.isEmpty()) {
                                noteViewModel.deleteNote(noteData.noteId!!)
                            } else {
                                noteViewModel.deleteNote(noteData.noteId!!)
                                noteViewModel.addNote(
                                    noteData = NoteData(
                                        userId = currentUserId,
                                        noteTitle = titleTf,
                                        noteDescription = descriptionTf
                                    )
                                )
                            }
                        } else {
                            if (titleTf.isNotEmpty() || descriptionTf.isNotEmpty()) {
                                noteViewModel.addNote(
                                    noteData = NoteData(
                                        userId = currentUserId,
                                        noteTitle = titleTf,
                                        noteDescription = descriptionTf
                                    )
                                )
                            }
                        }

                        navController.popBackStack()
                    },
                    modifier = Modifier
                        .align(alignment = Alignment.BottomEnd)
                        .padding(25.dp)
                        .size(70.dp),
                    containerColor = colorResource(id = com.example.notefire_cloud.R.color.blue)
                ) {
                    Icon(
                        painter = painterResource(id = com.example.notefire_cloud.R.drawable.save_ic),
                        contentDescription = "",
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }
    )

}