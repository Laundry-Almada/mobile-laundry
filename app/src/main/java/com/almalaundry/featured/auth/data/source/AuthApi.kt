package com.almalaundry.featured.auth.data.source

import com.almalaundry.featured.auth.data.dtos.AuthResponse
import com.almalaundry.featured.auth.data.dtos.AuthData
import com.almalaundry.featured.auth.data.dtos.LaundryRequest
import com.almalaundry.featured.auth.data.dtos.LaundryResponse
import com.almalaundry.featured.auth.data.dtos.LoginRequest
import com.almalaundry.featured.auth.data.dtos.LogoutResponse
import com.almalaundry.featured.auth.data.dtos.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface AuthApi {
    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("logout")
    @Headers("Accept: application/json")
    suspend fun logout(): Response<LogoutResponse>

    @POST("laundries") // Pastikan path ini sesuai dengan route di Laravel
    suspend fun createLaundry(@Body request: LaundryRequest): Response<LaundryResponse>

    @GET("laundries")
    suspend fun getLaundries(): Response<LaundryResponse>

//    @GET("laundries")
//    suspend fun getLaundries(): Response<AuthResponse<List<LaundryResponse>>>

}