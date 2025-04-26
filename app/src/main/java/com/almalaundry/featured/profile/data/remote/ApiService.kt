package com.almalaundry.featured.profile.data.remote

import com.almalaundry.featured.profile.data.model.UpdateProfileRequest
import com.almalaundry.featured.profile.data.model.UserResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT

interface ApiService {
    @GET("user")
    suspend fun getUser(): UserResponse

    @PUT("updateProfile")
    suspend fun updateProfile(
        @Body request: UpdateProfileRequest
    ): UserResponse
}

//package com.almalaundry.featured.profile.data.remote
//
//import com.almalaundry.featured.profile.data.model.UserResponse
//import retrofit2.http.GET
//
//interface ApiService {
//    @GET("user")
//    suspend fun getUser(): UserResponse
//}
