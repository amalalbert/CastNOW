package com.innovature.castnow.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.tv.material3.Button
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import com.innovature.castnow.R
import com.innovature.castnow.api.ImageApi
import com.innovature.castnow.di.RetrofitManager
import com.innovature.castnow.utils.TvNumberInterceptor
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun Slideshow(retrofitManager: RetrofitManager) {
    val fileList = remember { mutableStateListOf<String>() }
    var currentImageIndex by remember { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    suspend fun loadImages() {
        withContext(Dispatchers.IO + CoroutineExceptionHandler { _, throwable ->
            Log.e("error", "Slideshow: ${throwable.message}")
        }) {
            val images = fetchLocalImages(retrofitManager.getImageApi())
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

    var showConfigureDialog by remember {
        mutableStateOf(false)
    }

    fun toggleConfigureDialog() {
        showConfigureDialog = !showConfigureDialog
    }

    val interceptor = remember {
        TvNumberInterceptor(
            onSecretCodeMatchedSuccess = {
                refreshTrigger++
                Toast.makeText(context, "Fetching new images", Toast.LENGTH_SHORT).show()
            },
            onConfigureCodeMatchedSuccess = {
                Toast.makeText(context, "Configuration Mode", Toast.LENGTH_SHORT).show()
                toggleConfigureDialog()
            }
        )
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

    if (showConfigureDialog) {
        ConfigurationDialog(
            currentUrl = retrofitManager.getBaseUrl(),
            onDismiss = { showConfigureDialog = false },
            onSave = { newUrl ->
                retrofitManager.updateBaseUrl(newUrl)
                showConfigureDialog = false
                refreshTrigger++
            }
        )
    }

    if (fileList.isNotEmpty()) {

        Crossfade(
            modifier = Modifier
                .focusRequester(focusRequester)
                .focusable()
                .onPreviewKeyEvent {
                    onKeyEvent(it, interceptor)
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
            modifier = Modifier
                .focusRequester(focusRequester)
                .fillMaxWidth(0.1f)
                .onPreviewKeyEvent {
                    onKeyEvent(it, interceptor)
                },
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

fun onKeyEvent(event: KeyEvent, interceptor: TvNumberInterceptor): Boolean {
    if (event.type != KeyEventType.KeyDown) {
        return false
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

        return true
    } else {
        return false
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun ConfigurationDialog(
    currentUrl: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    // Basic parsing for http://10.10.13.82:8000/
    val regex = Regex("http://(\\d+)\\.(\\d+)\\.(\\d+)\\.(\\d+):(\\d+)/")
    val matchResult = regex.find(currentUrl)

    var ip1 by remember { mutableStateOf(matchResult?.groupValues?.get(1) ?: "") }
    var ip2 by remember { mutableStateOf(matchResult?.groupValues?.get(2) ?: "") }
    var ip3 by remember { mutableStateOf(matchResult?.groupValues?.get(3) ?: "") }
    var ip4 by remember { mutableStateOf(matchResult?.groupValues?.get(4) ?: "") }
    var port by remember { mutableStateOf(matchResult?.groupValues?.get(5) ?: "") }

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .width(550.dp)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Server Configuration",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    IpOctetField(ip1, { if (it.length <= 3) ip1 = it }, focusRequester)
                    Text(" . ", fontWeight = FontWeight.Bold, color = Color.White)
                    IpOctetField(ip2, { if (it.length <= 3) ip2 = it })
                    Text(" . ", fontWeight = FontWeight.Bold, color = Color.White)
                    IpOctetField(ip3, { if (it.length <= 3) ip3 = it })
                    Text(" . ", fontWeight = FontWeight.Bold, color = Color.White)
                    IpOctetField(ip4, { if (it.length <= 3) ip4 = it })
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = port,
                    onValueChange = {
                        if (it.length <= 5 && it.all { c -> c.isDigit() }) port = it
                    },
                    label = { Text("Port") },
                    modifier = Modifier.width(150.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(onClick = {
                        if (validateIp(ip1, ip2, ip3, ip4) && validatePort(port)) {
                            onSave("http://$ip1.$ip2.$ip3.$ip4:$port/")
                        }
                    }) {
                        Text("Save")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun IpOctetField(
    value: String,
    onValueChange: (String) -> Unit,
    focusRequester: FocusRequester? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = {
            if (it.isEmpty() || (it.all { char -> char.isDigit() } && it.toInt() <= 255)) {
                onValueChange(it)
            }
        },
        colors = OutlinedTextFieldDefaults.colors()
            .copy(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
        modifier = Modifier
            .width(85.dp)
            .run { if (focusRequester != null) focusRequester(focusRequester) else this },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        textStyle = androidx.compose.ui.text.TextStyle(textAlign = TextAlign.Center)
    )
}

fun validateIp(vararg octets: String): Boolean {
    return octets.all {
        val num = it.toIntOrNull()
        num != null && num in 0..255
    }
}

fun validatePort(port: String): Boolean {
    val p = port.toIntOrNull()
    return p != null && p in 1..65535
}

suspend fun fetchLocalImages(imageApi: ImageApi): List<String> {
    return try {
        imageApi.getImages()
    } catch (e: Exception) {
        Log.e("Server", "Error fetching images: ${e.message}")
        emptyList()
    }
}
