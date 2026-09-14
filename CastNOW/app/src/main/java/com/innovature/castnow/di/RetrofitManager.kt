package com.innovature.castnow.di

import com.innovature.castnow.api.ImageApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RetrofitManager @Inject constructor(private val retrofitProvider: RetrofitProvider) {

    @Volatile
    private var currentBaseUrl = "https://imageserver-3hza.onrender.com/"

    fun updateBaseUrl(newUrl: String) {
        currentBaseUrl = if (newUrl.endsWith("/")) newUrl else "$newUrl/"
    }

    fun getBaseUrl(): String = currentBaseUrl

    fun getImageApi(): ImageApi {
        return retrofitProvider.getImageApi(currentBaseUrl)
    }
}