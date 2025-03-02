package com.almalaundry.featured.auth.data.repositories

import android.util.Log
import com.almalaundry.featured.auth.data.dtos.AuthData
import com.almalaundry.featured.auth.data.dtos.LoginRequest
import com.almalaundry.featured.auth.data.dtos.RegisterRequest
import com.almalaundry.featured.auth.data.source.AuthApi
import com.almalaundry.shared.commons.session.SessionManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val api: AuthApi, private val sessionManager: SessionManager
) {
    suspend fun login(request: LoginRequest): Result<AuthData> {
        return try {
            val response = api.login(request)
            if (response.isSuccessful && response.body()?.success == true) {
                val authData = response.body()!!.data
                sessionManager.saveSession(authData.toSession())
                Log.d("AuthRepository", "Login successful: $authData")
                Result.success(authData)
            } else {
                Log.e("AuthRepository", "Login failed: ${response.body()?.message}")
                Result.failure(Exception(response.body()?.message ?: "Login failed"))
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Login exception: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun register(request: RegisterRequest): Result<AuthData> {
        return try {
            val response = api.register(request)
            if (response.isSuccessful && response.body()?.success == true) {
                val authData = response.body()!!.data
                sessionManager.saveSession(authData.toSession())
                Log.d("AuthRepository", "Register successful: $authData")
                Result.success(authData)
            } else {
                Log.e("AuthRepository", "Register failed: ${response.body()?.message}")
                Result.failure(Exception(response.body()?.message ?: "Registration failed"))
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Register exception: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun logout(): Result<Unit> {
        return try {
            val response = api.logout()
            if (response.isSuccessful && response.body()?.success == true) {
                sessionManager.clearSession()
                Log.d("AuthRepository", "Logout successful")
                Result.success(Unit)
            } else {
                Log.e("AuthRepository", "Logout failed: ${response.body()?.message}")
                Result.failure(Exception(response.body()?.message ?: "Logout failed"))
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Logout exception: ${e.message}")
            Result.failure(e)
        }
    }
}