package com.innovature.castnow.di

import com.innovature.castnow.api.ImageApi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RetrofitManager @Inject constructor() {

    private val client = OkHttpClient.Builder().build()

    @Volatile
    private var retrofit: Retrofit? = null

    @Volatile
    private var currentBaseUrl = "http://10.10.13.82:8000/"

    private fun createRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(currentBaseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun updateBaseUrl(newUrl: String) {
        currentBaseUrl = if (newUrl.endsWith("/")) newUrl else "$newUrl/"
        retrofit = createRetrofit()
    }

    fun getBaseUrl(): String = currentBaseUrl

    fun getImageApi(): ImageApi {
        if (retrofit == null) {
            retrofit = createRetrofit()
        }

        return retrofit!!.create(ImageApi::class.java)
    }
}