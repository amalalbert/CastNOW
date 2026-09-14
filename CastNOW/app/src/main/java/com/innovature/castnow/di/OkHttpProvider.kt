package com.innovature.castnow.di

import okhttp3.OkHttpClient
import javax.inject.Singleton

@Singleton
class OkHttpProvider() {
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .build()
    }
}