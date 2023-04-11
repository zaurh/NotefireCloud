package com.example.random.presentation.components

import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.input.TextFieldValue

@Composable
fun MyAlert(
    onButtonClick: () -> Unit,
    onDismiss: () -> Unit,
    textFieldValue: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        buttons = {
            TextField(value = textFieldValue, onValueChange = onValueChange, placeholder = {
                Text(
                    text = "Title"
                )
            })
            Button(onClick = { onButtonClick() }) {
                Text(text = "Add")
            }
        }
    )
}