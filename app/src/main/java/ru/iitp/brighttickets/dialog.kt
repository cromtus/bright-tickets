package ru.iitp.brighttickets

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester

@Composable
fun NameDialog(
    title: String,
    initialValue: String = "",
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val text = remember { mutableStateOf(initialValue) }
    val focusRequester = remember { FocusRequester() }

    AlertDialog(
        onDismissRequest = onDismiss,
        text = {
            TextField(
                value = text.value,
                onValueChange = { text.value = it },
                placeholder = { Text(title) },
                singleLine = true,
                modifier = Modifier.focusRequester(focusRequester)
            )
        },
        confirmButton = {
            Button(enabled = text.value.isNotBlank(), onClick = { onConfirm(text.value) }) {
                Text("OK")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.secondary
                ),
            ) {
                Text("Cancel")
            }
        }
    )

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@Composable
fun DeleteDialog(
    photoName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Delete Ticket") },
        text = { Text("Are you sure you want to \"$photoName\"? This action cannot be undone.") },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Delete")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.secondary
                ),
            ) {
                Text("Cancel")
            }
        }
    )
}
