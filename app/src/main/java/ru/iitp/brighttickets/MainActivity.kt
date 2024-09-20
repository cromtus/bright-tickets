package ru.iitp.brighttickets

import FullScreenImage
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat
import ru.iitp.brighttickets.ui.theme.BrightTicketsTheme

class MainActivity : ComponentActivity() {

    private val viewModel: PhotoViewModel by viewModels {
        PhotoViewModelFactory(applicationContext)
    }
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                launchImagesSelector()
            } else {
                // Permission denied, do nothing
            }
        }

    private val selectImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            viewModel.setNewPhoto(viewModel.addPhoto(it))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BrightTicketsTheme {
                val photos = viewModel.photos.collectAsState()
                val newPhoto = viewModel.newPhoto.collectAsState()

                // Main photo list screen
                PhotoListScreen(
                    photos = photos.value,
                    onAddClick = ::onAddClick,
                    onPhotoClick = { photo -> viewModel.showFullScreenPhoto(photo) },
                    onEditPhoto = { photo, newName -> viewModel.editPhotoName(photo, newName) },
                    onDeletePhoto = { photo -> viewModel.deletePhoto(photo) },
                    isEditMode = viewModel.isEditMode.value,
                    onLongPress = { viewModel.enableEditMode() },
                    onDragReorder = { fromIndex, toIndex -> viewModel.reorderPhotos(fromIndex, toIndex) },
                    onDoneClick = { viewModel.disableEditMode() },
                )

                // Handle full-screen photo display
                if (viewModel.fullScreenPhoto.value != null) {
                    FullScreenImage(
                        photo = viewModel.fullScreenPhoto.value!!,
                        onBackPress = { viewModel.closeFullScreen() },
                        window = window
                    )
                }

                // Show the RenameDialog when a new photo is selected but not named yet
                newPhoto.value?.let { uri ->
                    NameDialog(
                        title = "Ticket Name",
                        onConfirm = { name ->
                            viewModel.editPhotoName(uri, name)
                            viewModel.clearNewPhoto() // Clear after adding
                        },
                        onDismiss = { viewModel.clearNewPhoto() }
                    )
                }
            }
        }
    }

    private fun onAddClick() {
        if (checkStoragePermission()) {
            launchImagesSelector()
        } else {
            requestStoragePermission()
        }
    }

    private fun launchImagesSelector() {
        selectImageLauncher.launch("image/*")
    }

    private fun checkStoragePermission() = ContextCompat.checkSelfPermission(
                this, android.Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

    private fun requestStoragePermission() {
        requestPermissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
    }
}
