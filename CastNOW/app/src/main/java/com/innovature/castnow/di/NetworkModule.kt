package com.innovature.castnow.di

import com.innovature.castnow.api.ImageApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object

NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofitProvider(
        okHttpClient: OkHttpClient
    ): RetrofitProvider {
        return RetrofitProvider(okHttpClient)
    }

    @Provides
    @Singleton
    fun provideWebSocketManager(
        okHttpClient: OkHttpClient
    ): WebSocketManager {
        return WebSocketManager(okHttpClient)
    }
}
