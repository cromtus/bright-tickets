import android.view.View
import android.view.Window
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import coil.compose.rememberImagePainter
import android.view.WindowManager
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import ru.iitp.brighttickets.Photo

@Composable
fun FullScreenImage(
    photo: Photo,
    onBackPress: () -> Unit,
    window: Window
) {
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    BackHandler {
        onBackPress()
    }

    DisposableEffect(Unit) {
        // Increase the brightness when photo is opened
        val layoutParams = window.attributes
        layoutParams.screenBrightness = 1f // Max brightness
        window.attributes = layoutParams

        // Restore brightness on back press
        onDispose {
            layoutParams.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
            window.attributes = layoutParams
        }
    }

    // Enter fullscreen by hiding status and navigation bars
    DisposableEffect(Unit) {
        val originalFlags = window.decorView.systemUiVisibility
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                )
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        onDispose {
            window.decorView.systemUiVisibility = originalFlags // Restore system UI on exit
            window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale *= zoom
                    offset = Offset(offset.x + pan.x, offset.y + pan.y)
                }
            }
    ) {
        // Add dim overlay with a translucent black color
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(shadowElevation = 16.dp.value)
                .background(Color.Black)
        )
        Image(
            painter = rememberImagePainter(photo.uri),
            contentDescription = photo.name,
            modifier = Modifier
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y
                )
                .fillMaxSize()
        )
    }
}
