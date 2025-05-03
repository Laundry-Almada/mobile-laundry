package com.almalaundry.featured.profile.data.repository

import com.almalaundry.featured.profile.data.model.UpdateProfileRequest
import com.almalaundry.featured.profile.data.model.UserResponse
import com.almalaundry.featured.profile.data.remote.ApiService
import com.almalaundry.featured.profile.data.remote.ProfileApi
import javax.inject.Inject
import javax.inject.Named

class ProfileRepository @Inject constructor(
    private val apiService: ApiService,
    @Named("Authenticated") private val authenticatedApi: ProfileApi,
    @Named("Public") private val publicApi: ProfileApi,
) {
    suspend fun getUser(): Result<UserResponse> {
        return try {
            val response = authenticatedApi.getUser()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProfile(request: UpdateProfileRequest): Result<UserResponse> {
        return try {
            val response = authenticatedApi.updateProfile(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

//package com.almalaundry.featured.profile.data.repository
//
//import com.almalaundry.featured.profile.data.model.UserResponse
//import com.almalaundry.featured.profile.data.remote.ApiService
//import javax.inject.Inject
//
//class ProfileRepository @Inject constructor(
//    private val apiService: ApiService
//) {
//    suspend fun getUser(): Result<UserResponse> {
//        return try {
//            val response = apiService.getUser()
//            Result.success(response)
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }
//}
