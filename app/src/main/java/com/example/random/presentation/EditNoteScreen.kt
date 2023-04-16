package com.example.random.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MoreVert
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.random.data.NoteData

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun EditNoteScreen(
    navController: NavController,
    note: NoteData,
    sharedViewModel: SharedViewModel = hiltViewModel()
) {
    val focus = LocalFocusManager.current
    var titleTf by remember { mutableStateOf("") }
    var descriptionTf by remember { mutableStateOf("") }
    var dropdownExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = Unit){
        titleTf = note.noteTitle.toString()
        descriptionTf = note.noteDescription.toString()
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
                                    sharedViewModel.deleteNote(note.noteId!!)
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
                },
                title = { Text(text = "Edit Note", color = Color.White) },
                backgroundColor = colorResource(id = com.example.random.R.color.blue)
            )
        },
        content = {
            Box(modifier = Modifier.fillMaxSize()) {
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
                            backgroundColor = Color.White,
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
                            backgroundColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                        )
                    )


                }
                FloatingActionButton(
                    onClick = {
                        focus.clearFocus()
                        if (titleTf.isEmpty() && descriptionTf.isEmpty()){
                            sharedViewModel.deleteNote(note.noteId!!)
                        }
                        sharedViewModel.addNote(titleTf, descriptionTf)
                        sharedViewModel.deleteNote(note.noteId!!)

                        navController.navigate("main"){
                            popUpTo(0)
                        }
                    },
                    modifier = Modifier
                        .align(alignment = Alignment.BottomEnd)
                        .padding(25.dp)
                        .size(70.dp), backgroundColor = colorResource(id = com.example.random.R.color.blue)
                ) {
                    Icon(
                        painter = painterResource(id = com.example.random.R.drawable.save),
                        contentDescription = "",
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }
    )

}