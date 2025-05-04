package com.almalaundry.featured.auth.data.repositories

import android.util.Log
import com.almalaundry.featured.auth.data.dtos.AuthData
import com.almalaundry.featured.auth.data.dtos.LaundryRequest
import com.almalaundry.featured.auth.data.dtos.LoginRequest
import com.almalaundry.featured.auth.data.dtos.RegisterRequest
import com.almalaundry.featured.auth.data.source.AuthApi
import com.almalaundry.featured.order.domain.models.Laundry
import com.almalaundry.shared.commons.config.BuildConfig
import com.almalaundry.shared.commons.session.SessionManager
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    @Named("Authenticated") private val authenticatedApi: AuthApi,
    @Named("Public") private val publicApi: AuthApi,
    private val sessionManager: SessionManager
) {
    suspend fun login(request: LoginRequest): Result<AuthData> {
        return try {
            Log.d("AuthRepository", "BASE_URL: ${BuildConfig.BASE_URL}")
            Log.d("AuthRepository", "Login request: $request")

            val response = publicApi.login(request)

            if (response.isSuccessful) {
                val responseBody = response.body()
                Log.d("AuthRepository", "Login response successful: $responseBody")

                if (responseBody?.success == true) {
                    val authData = responseBody.data
                    sessionManager.saveSession(authData.toSession())
                    Result.success(authData)
                } else {
                    val errorMessage = responseBody?.message ?: "Unknown error"
                    Log.e("AuthRepository", "Login failed: $errorMessage")
                    Result.failure(Exception(errorMessage))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(
                    "AuthRepository",
                    "Login failed with code: ${response.code()}, Error: $errorBody"
                )

                // Try to parse error message if it's in JSON format
                val errorMessage = try {
                    val jsonObject = JSONObject(errorBody ?: "{}")
                    jsonObject.optString("message", "Unknown error")
                } catch (e: Exception) {
                    errorBody ?: "Unknown network error"
                }

                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Login exception: ${e.javaClass.simpleName}: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun register(request: RegisterRequest): Result<AuthData> {
        return try {
            val response = publicApi.register(request)
            Log.d("AuthRepository", "Register response: ${response.body()}")
            if (response.isSuccessful && response.body()?.success == true) {
                val authData = response.body()!!.data
                sessionManager.saveSession(authData.toSession())
                Result.success(authData)
            } else {
                val errorMessage = response.body()?.message ?: response.errorBody()?.string()
                ?: "Registration failed"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Register exception: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun getLaundries(): Result<List<Laundry>> {
        return try {
            val response = publicApi.getLaundries()
            if (response.isSuccessful && response.body()?.success == true) {
                val laundries = response.body()?.data ?: emptyList()
                Result.success(laundries)
            } else {
                val errorMessage = response.body()?.message ?: "Failed to fetch laundries"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Get laundries exception: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun createLaundry(request: LaundryRequest): Result<Laundry> {
        return try {
            val response = publicApi.createLaundry(request)
            if (response.isSuccessful && response.body()?.success == true) {
                val laundry = response.body()?.data
                    ?: return Result.failure(Exception("No laundry data returned"))
                Result.success(laundry)
            } else {
                val errorMessage =
                    response.body()?.message ?: response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Create laundry exception: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun logout(): Result<Unit> {
        return try {
            val response = authenticatedApi.logout()
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
