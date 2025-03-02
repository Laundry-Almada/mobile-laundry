package com.almalaundry.featured.auth.data.source

import com.almalaundry.featured.auth.data.dtos.AuthResponse
import com.almalaundry.featured.auth.data.dtos.LoginRequest
import com.almalaundry.featured.auth.data.dtos.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface AuthApi {
    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("logout")
    @Headers("Accept: application/json")
    suspend fun logout(): Response<Unit>
}