package ru.iitp.brighttickets

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File

data class Photo(val id: Long, val name: String, val uri: Uri)

class PhotoViewModel(private val context: Context) : ViewModel() {
    private val _photos = MutableStateFlow(loadPhotos(context)) // Load persisted photos
    val photos: StateFlow<List<Photo>> = _photos

    var isEditMode = mutableStateOf(false)
    var fullScreenPhoto = mutableStateOf<Photo?>(null)

    private val _newPhoto = MutableStateFlow<Photo?>(null)
    val newPhoto: StateFlow<Photo?> = _newPhoto

    fun setNewPhoto(photo: Photo) {
        _newPhoto.value = photo
    }

    fun clearNewPhoto() {
        _newPhoto.value = null
    }

    fun addPhoto(uri: Uri): Photo {
        val storedFile = copyPhotoToAppStorage(context, uri)
        val newPhoto = Photo(
            id = System.currentTimeMillis(),
            name = storedFile.nameWithoutExtension,
            uri = Uri.fromFile(storedFile)
        )
        _photos.value = _photos.value + newPhoto
        savePhotos(context, _photos.value) // Save the updated list
        return newPhoto
    }

    fun deletePhoto(photo: Photo) {
        _photos.value = _photos.value - photo

        // Delete the corresponding photo file from internal storage
        val photoFile = File(photo.uri.path ?: "not-exists")
        if (photoFile.exists()) {
            photoFile.delete()
        }

        savePhotos(context, _photos.value) // Save after deleting a photo
    }

    fun reorderPhotos(fromIndex: Int, toIndex: Int) {
        val newList = _photos.value.toMutableList()
        val item = newList.removeAt(fromIndex)
        newList.add(toIndex, item)
        _photos.value = newList
        savePhotos(context, _photos.value) // Save after reordering photos
    }

    fun showFullScreenPhoto(photo: Photo) {
        fullScreenPhoto.value = photo
    }

    fun closeFullScreen() {
        fullScreenPhoto.value = null
    }

    fun editPhotoName(photo: Photo, newName: String) {
        val updatedList = _photos.value.map {
            if (it.id == photo.id) it.copy(name = newName) else it
        }
        _photos.value = updatedList
        savePhotos(context, _photos.value) // Save after renaming
    }

    fun enableEditMode() {
        isEditMode.value = true
    }

    fun disableEditMode() {
        isEditMode.value = false
    }
}

class PhotoViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PhotoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PhotoViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

private fun copyPhotoToAppStorage(context: Context, uri: Uri): File {
    val inputStream = context.contentResolver.openInputStream(uri)
    val file = File(context.filesDir, "ticket_${System.currentTimeMillis()}.jpg")
    val outputStream = file.outputStream()
    inputStream?.copyTo(outputStream)
    inputStream?.close()
    outputStream.close()
    return file
}