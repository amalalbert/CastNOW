package com.innovature.castnow.ui.screens.viewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.innovature.castnow.di.RetrofitManager
import com.innovature.castnow.ui.screens.fetchLocalImages
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SlideShowViewModel: ViewModel() {
    val fileList = mutableStateListOf<String>()
    var currentImageIndex by mutableIntStateOf(0)

    suspend fun loadImages(retrofitManager: RetrofitManager) {
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

}