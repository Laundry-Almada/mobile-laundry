package com.almalaundry.featured.profile.data.repository

import com.almalaundry.featured.profile.data.model.UserResponse
import com.almalaundry.featured.profile.data.remote.ApiService
import javax.inject.Inject

class ProfileRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getUser(): Result<UserResponse> {
        return try {
            val response = apiService.getUser()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
