package com.innovature.castnow.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object

NetworkModule {


    @Provides
    @Singleton
    fun provideRetrofitProvider(
        okHttpProvider: OkHttpProvider
    ): RetrofitProvider {
        return RetrofitProvider()
    }

    @Provides
    @Singleton
    fun provideWebSocketManager(
        okHttpProvider: OkHttpProvider
    ): WebSocketManager {
        return WebSocketManager(okHttpProvider.provideOkHttpClient())
    }
}
