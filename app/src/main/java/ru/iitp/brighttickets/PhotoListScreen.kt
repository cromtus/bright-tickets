package ru.iitp.brighttickets

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PhotoListScreen(
    photos: List<Photo>, // List of photos with names
    onAddClick: () -> Unit,
    onPhotoClick: (Photo) -> Unit,
    onEditPhoto: (Photo, String) -> Unit,
    onDeletePhoto: (Photo) -> Unit,
    isEditMode: Boolean,
    onLongPress: () -> Unit,
    onDragReorder: (Int, Int) -> Unit,
    onDoneClick: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp // Get screen width
    val tileWidth = screenWidth / 3 // Calculate width of each tile (3 per row)
    val tileHeight = tileWidth * (configuration.screenHeightDp.toFloat() / configuration.screenWidthDp.toFloat())

    Scaffold(
        floatingActionButton = {
            if (isEditMode) {
                FloatingActionButton(onClick = onDoneClick) {
                    Icon(Icons.Default.Check, contentDescription = "Done")
                }
            } else {
                FloatingActionButton(onClick = onAddClick) {
                    Icon(Icons.Default.Add, contentDescription = "Add Photo")
                }
            }
        }
    ) {
        if (photos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Your tickets will be here",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3), // 3 columns
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(photos, key = { it.id }) { photo ->
                    PhotoListItem(
                        photo = photo,
                        isEditMode = isEditMode,
                        onClick = { onPhotoClick(photo) },
                        onEditClick = { newName -> onEditPhoto(photo, newName) },
                        onDeleteClick = { onDeletePhoto(photo) },
                        onLongPress = { onLongPress() },
                        modifier = Modifier
                            .width(tileWidth)
                            .height(tileHeight)
                    )
                }
            }
        }
    }
}
