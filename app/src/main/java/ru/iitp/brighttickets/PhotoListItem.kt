package ru.iitp.brighttickets

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter


@Composable
fun PhotoListItem(
    photo: Photo,
    isEditMode: Boolean,
    onClick: () -> Unit,
    onEditClick: (newName: String) -> Unit,
    onDeleteClick: () -> Unit,
    onLongPress: () -> Unit,
    modifier: Modifier,
) {
    var showRenameDialog by remember { mutableStateOf(false) }
    if (showRenameDialog) {
        NameDialog(
            title = "New Ticket Name",
            initialValue = photo.name,
            onConfirm = { newName ->
                onEditClick(newName) // Handle renaming action
                showRenameDialog = false
            },
            onDismiss = { showRenameDialog = false }
        )
    }

    var showDeleteDialog by remember { mutableStateOf(false) }
    if (showDeleteDialog) {
        DeleteDialog(
            photoName = photo.name,
            onConfirm = {
                onDeleteClick() // Call the delete function when confirmed
                showDeleteDialog = false
            },
            onDismiss = { showDeleteDialog = false }
        )
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.LightGray)
            .pointerInput(Unit) {
                detectTapGestures(onLongPress = { onLongPress() }, onTap = { onClick() })
            }
    ) {
        Image(
            painter = rememberImagePainter(photo.uri),
            contentDescription = photo.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                        startY = 150f
                    )
                ),
            contentAlignment = Alignment.BottomStart
        ) {
            BasicText(
                text = photo.name,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.White,
                    fontSize = 16.sp
                ),
                modifier = Modifier
                    .padding(8.dp)
            )
        }
        if (isEditMode) {
            // Dim background to indicate edit mode
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
            )

            // Delete button (top-left corner)
            IconButton(
                onClick = { showDeleteDialog = true },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(4.dp)
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.White)
            }

            // Edit button (top-right corner)
            IconButton(
                onClick = { showRenameDialog = true },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.White)
            }
        }
    }
}
