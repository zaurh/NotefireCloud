package com.example.random.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
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
import com.example.random.R

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun AddNoteScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel = hiltViewModel()
) {
    var titleTf by remember { mutableStateOf("") }
    var descriptionTf by remember { mutableStateOf("") }
    val focus = LocalFocusManager.current

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
                title = { Text(text = "Add Note", color = Color.White) },
                backgroundColor = colorResource(id = R.color.blue)
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
                            focusedIndicatorColor = Color.Gray,
                            unfocusedIndicatorColor = Color.Gray
                        ),
                        textStyle = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )

                    )
                    TextField(
                        placeholder = { Text(text = "Note") },
                        value = descriptionTf,
                        onValueChange = { descriptionTf = it },
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = Color.White,
                            focusedIndicatorColor = Color.Gray,
                            unfocusedIndicatorColor = Color.Gray
                        )
                    )


                }
                FloatingActionButton(
                    onClick = {
                        navController.navigate("main"){
                            popUpTo(0)
                        }
                        focus.clearFocus()
                        if (titleTf.isNotEmpty() || descriptionTf.isNotEmpty()){
                            sharedViewModel.addNote(titleTf, descriptionTf)
                        }

                    },
                    modifier = Modifier
                        .align(alignment = Alignment.BottomEnd)
                        .padding(25.dp)
                        .size(70.dp), backgroundColor = colorResource(id = R.color.blue)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.save),
                        contentDescription = "",
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }
    )

}