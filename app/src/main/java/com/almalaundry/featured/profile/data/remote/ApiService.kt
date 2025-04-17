package com.almalaundry.featured.profile.data.remote

import com.almalaundry.featured.profile.data.model.UserResponse
import retrofit2.http.GET

interface ApiService {
    @GET("user")
    suspend fun getUser(): UserResponse
}
