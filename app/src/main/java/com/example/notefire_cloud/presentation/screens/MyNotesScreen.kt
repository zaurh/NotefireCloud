package com.example.notefire_cloud.presentation.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.notefire_cloud.R
import com.example.notefire_cloud.presentation.components.MySearchBar
import com.example.notefire_cloud.presentation.components.NoteListItem
import com.example.notefire_cloud.presentation.screens.viewmodel.AuthViewModel
import com.example.notefire_cloud.presentation.screens.viewmodel.NoteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(
    navController: NavController,
    noteViewModel: NoteViewModel,
    authViewModel: AuthViewModel
) {
    var dropdownExpanded by remember { mutableStateOf(false) }
    val loginSuccess = authViewModel.loginSuccess.value
    val noteData = noteViewModel.noteData
    val focus = LocalFocusManager.current


    LaunchedEffect(key1 = true){
        if (loginSuccess){
            noteViewModel.getNotes()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "My Notes", color = Color.White) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = colorResource(id = R.color.blue)
                ),
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
                                    authViewModel.signOut()
                                    navController.navigate("sign_in") {
                                        popUpTo(0)
                                    }
                                    dropdownExpanded = false
                                }) {
                            Icon(imageVector = Icons.Default.Logout, contentDescription = "")
                            Spacer(modifier = Modifier.size(10.dp))
                            Text(
                                fontSize = 18.sp,
                                text = "Log out",
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                    }
                })
        },
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = colorResource(id = R.color.gray))
                    .padding(it)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(5.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    MySearchBar(
                        modifier = Modifier.padding(5.dp),
                        hint = "Search...",
                        onSearch = {
                            noteViewModel.searchNotes(it)
                        },
                        noteViewModel = noteViewModel
                    )
                    LazyVerticalGrid(
                        modifier = Modifier.weight(1f),
                        columns = GridCells.Fixed(2),
                        content = {
                            items(noteData.value) { note ->
                                NoteListItem(
                                    noteData = note,
                                    noteViewModel = noteViewModel,
                                    navController = navController
                                )
                            }
                        })


                }
                FloatingActionButton(
                    onClick = {
                        navController.navigate("add_note")
                        focus.clearFocus()
                    },
                    modifier = Modifier
                        .align(alignment = Alignment.BottomEnd)
                        .padding(20.dp)
                        .size(70.dp), containerColor = colorResource(id = R.color.blue)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "",
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }
    )


}