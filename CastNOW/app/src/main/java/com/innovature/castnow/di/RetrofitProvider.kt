package com.innovature.castnow.di

import com.innovature.castnow.api.ImageApi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RetrofitProvider @Inject constructor(
    private val okHttpProvider: OkHttpProvider
) {

    private var retrofit: Retrofit? = null
    private var currentBaseUrl: String? = null

    fun getRetrofit(baseUrl: String): Retrofit {

        val normalizedUrl = if (baseUrl.endsWith("/")) {
            baseUrl
        } else {
            "$baseUrl/"
        }

        if (retrofit == null || currentBaseUrl != normalizedUrl) {

            retrofit = Retrofit.Builder()
                .baseUrl(normalizedUrl)
                .client(okHttpProvider.provideOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            currentBaseUrl = normalizedUrl
        }

        return retrofit!!
    }

    fun getImageApi(baseUrl: String): ImageApi {
        return getRetrofit(baseUrl)
            .create(ImageApi::class.java)
    }
}