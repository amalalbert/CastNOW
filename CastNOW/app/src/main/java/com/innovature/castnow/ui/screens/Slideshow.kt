package com.innovature.castnow.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.tv.material3.Button
import androidx.tv.material3.Text
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import com.innovature.castnow.R
import com.innovature.castnow.api.ImageApi
import com.innovature.castnow.utils.TvNumberInterceptor
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun Slideshow(imageApi: ImageApi) {
    val fileList = remember { mutableStateListOf<String>() }
    var currentImageIndex by remember { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    suspend fun loadImages() {
        withContext(Dispatchers.IO + CoroutineExceptionHandler { _, throwable ->
            Log.e("error", "Slideshow: ${throwable.message}")
        }) {
            val images = fetchLocalImages(imageApi)
            withContext(Dispatchers.Main) {
                fileList.clear()
                fileList.addAll(images)
                currentImageIndex = 0
            }
        }
    }

    var refreshTrigger by remember {
        mutableIntStateOf(0)
    }


    val interceptor = remember {
        TvNumberInterceptor {
            refreshTrigger++
            Toast.makeText(context, "Fetching new images", Toast.LENGTH_SHORT).show()
        }
    }
    val focusRequester = remember {
        FocusRequester()
    }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    LaunchedEffect(refreshTrigger) {
        loadImages()
    }

    // Slideshow cycling logic
    LaunchedEffect(fileList.size) {
        Log.d("amal", "Slideshow:${fileList.size} ")
        if (fileList.size > 1) {
            while (true) {
                delay(9000) // 9 second delay
                currentImageIndex = (currentImageIndex + 1) % fileList.size
            }
        }
    }

    if (fileList.isNotEmpty()) {

        Crossfade(
            modifier = Modifier
                .focusRequester(focusRequester)
                .focusable()
                .onPreviewKeyEvent { event ->
                    if (event.type != KeyEventType.KeyDown) {
                        return@onPreviewKeyEvent false
                    }

                    val keyCode =
                        event.nativeKeyEvent.keyCode

                    if (
                        keyCode in
                        android.view.KeyEvent.KEYCODE_0..
                        android.view.KeyEvent.KEYCODE_9
                    ) {

                        val number =
                            keyCode -
                                    android.view.KeyEvent.KEYCODE_0

                        interceptor.onNumberPressed(number)

                        true
                    } else {
                        false
                    }
                },
            targetState = fileList[currentImageIndex],
            animationSpec = tween(durationMillis = 800),
            label = "Slideshow"
        ) { imageUrl ->
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(imageUrl)
                    .diskCachePolicy(CachePolicy.DISABLED)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .build(),
                error = painterResource(R.drawable.stone_henge),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds,
                onError = {
                    Log.d("amal", "Slideshow: error $it")
                }
            )
        }
    } else {
        Button(
            modifier = Modifier.fillMaxWidth(0.1f),
            onClick = {
                scope.launch {
                    loadImages()
                }
            }
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Reload", textAlign = TextAlign.Center)
            }
        }
    }
}

suspend fun fetchLocalImages(imageApi: ImageApi): List<String> {
    return try {
        imageApi.getImages()
    } catch (e: Exception) {
        Log.e("Server", "Error fetching images: ${e.message}")
        emptyList()
    }
}
