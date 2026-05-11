package com.innovature.castnow.api

import retrofit2.http.GET

interface ImageApi {
    @GET("api/images/")
    suspend fun getImages(): List<String>
}