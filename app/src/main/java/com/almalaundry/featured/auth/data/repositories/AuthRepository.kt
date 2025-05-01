// package com.almalaundry.featured.auth.data.repositories
//
// import android.util.Log
// import com.almalaundry.featured.auth.data.dtos.AuthData
// import com.almalaundry.featured.auth.data.dtos.AuthResponse
// import com.almalaundry.featured.auth.data.dtos.LaundryRequest
// import com.almalaundry.featured.auth.data.dtos.LoginRequest
// import com.almalaundry.featured.auth.data.dtos.RegisterDto
// import com.almalaundry.featured.auth.data.dtos.RegisterRequest
// import com.almalaundry.featured.auth.data.source.AuthApi
// import com.almalaundry.shared.commons.session.SessionManager
// import javax.inject.Inject
// import javax.inject.Singleton
//
// @Singleton
// class AuthRepository<AuthData> @Inject constructor(
//    private val api: AuthApi,
//    private val sessionManager: com.almalaundry.shared.commons.session.SessionManager
// ) {
//    suspend fun login(request: LoginRequest): Result<AuthData> {
//        return try {
//            val response = api.login(request)
//            if (response.isSuccessful && response.body()?.success == true) {
//                val authData = response.body()!!.data
//                sessionManager.saveSession(authData.toSession())
//                Log.d("AuthRepository", "Login successful: $authData")
//                Result.success(authData)
//            } else {
//                Log.e("AuthRepository", "Login failed: ${response.body()?.message}")
//                Result.failure(Exception(response.body()?.message ?: "Login failed"))
//            }
//        } catch (e: Exception) {
//            Log.e("AuthRepository", "Login exception: ${e.message}")
//            Result.failure(e)
//        }
//        }
//
//    suspend fun register(dto: RegisterDto): Result<AuthData> {
//        return try {
//            // Jika user adalah owner dan ingin membuat laundry baru
//            if (dto.role == "Owner" && !dto.laundryName.isNullOrBlank()) {
//                // Pertama buat laundry
//                val laundryRequest = LaundryRequest(
//                    name = dto.laundryName,
//                    address = "", // Tambahkan field ini di RegisterDto jika perlu
//                    phone = ""    // Tambahkan field ini di RegisterDto jika perlu
//                )
//
//                val laundryResponse = AuthApi.createLaundry(laundryRequest)
//                if (!laundryResponse.isSuccessful) {
//                    return Result.failure(Exception("Gagal membuat laundry:
// ${laundryResponse.errorBody()?.string()}"))
//                }
//
//                val laundryId = laundryResponse.body()?.data?.id ?:
//                return Result.failure(Exception("Laundry ID tidak ditemukan"))
//
//                // Kemudian register user dengan laundry ID
//                val registerRequest = RegisterRequest(
//                    name = dto.username,
//                    email = dto.email,
//                    password = dto.password,
//                    cPassword = dto.confirmPassword,
//                    role = dto.role,
//                    laundryId = laundryId
//                )
//
//                val registerResponse = AuthApi.register(registerRequest)
//                handleRegisterResponse(registerResponse)
//            }
//            // Jika user adalah staff dan memilih laundry yang sudah ada
//            else if (dto.role == "Staff" && !dto.selectedLaundry.isNullOrBlank()) {
//                val registerRequest = RegisterRequest(
//                    name = dto.username,
//                    email = dto.email,
//                    password = dto.password,
//                    cPassword = dto.confirmPassword,
//                    role = dto.role,
//                    laundryId = dto.selectedLaundry
//                )
//
//                val response = AuthApi.register(registerRequest)
//                handleRegisterResponse(response)
//            }
//            // Jika kondisi tidak terpenuhi
//            else {
//                Result.failure(Exception("Data registrasi tidak valid"))
//            }
//        } catch (e: Exception) {
//            Log.e("AuthRepository", "Register exception: ${e.message}")
//            Result.failure(e)
//        }
//    }
////    suspend fun register(request: RegisterRequest): Result<AuthData> {
////        return try {
////            val response = api.register(request)
////            if (response.isSuccessful && response.body()?.success == true) {
////                val authData = response.body()!!.data
////                sessionManager.saveSession(authData.toSession())
////                Log.d("AuthRepository", "Register successful: $authData")
////                Result.success(authData)
////            } else {
////                Log.e("AuthRepository", "Register failed: ${response.body()?.message}")
////                Result.failure(Exception(response.body()?.message ?: "Registration failed"))
////            }
////        } catch (e: Exception) {
////            Log.e("AuthRepository", "Register exception: ${e.message}")
////            Result.failure(e)
////        }
////    }
//
//    private suspend fun handleRegisterResponse(response: Response<AuthResponse<AuthData>>):
// Result<AuthData> {
//        return if (response.isSuccessful && response.body()?.success == true) {
//            val authData = response.body()!!.data
//            sessionManager.saveSession(authData.toSession())
//            Log.d("AuthRepository", "Register successful: $authData")
//            Result.success(authData)
//        } else {
//            Log.e("AuthRepository", "Register failed: ${response.body()?.message}")
//            Result.failure(Exception(response.body()?.message ?: "Registration failed"))
//        }
//    }
//
////    fun isLoggedIn(): Boolean {
////        return sessionManager.getSession() != null
////    }
//
//
//    suspend fun logout(): Result<Unit> {
//        return try {
//            val response = api.logout()
//            if (response.isSuccessful && response.body()?.success == true) {
//                sessionManager.clearSession()
//                Log.d("AuthRepository", "Logout successful")
//                Result.success(Unit)
//            } else {
//                Log.e("AuthRepository", "Logout failed: ${response.body()?.message}")
//                Result.failure(Exception(response.body()?.message ?: "Logout failed"))
//            }
//        } catch (e: Exception) {
//            Log.e("AuthRepository", "Logout exception: ${e.message}")
//            Result.failure(e)
//        }
//    }
// }

package com.almalaundry.featured.auth.data.repositories

import android.util.Log
import com.almalaundry.featured.auth.data.dtos.AuthData
import com.almalaundry.featured.auth.data.dtos.AuthResponse
import com.almalaundry.featured.auth.data.dtos.LoginRequest
import com.almalaundry.featured.auth.data.dtos.RegisterDto
import com.almalaundry.featured.auth.data.dtos.RegisterRequest
import com.almalaundry.featured.auth.data.source.AuthApi
import com.almalaundry.featured.order.domain.models.Laundry
import com.almalaundry.shared.commons.session.SessionManager
import javax.inject.Inject
import javax.inject.Singleton
import retrofit2.Response

@Singleton
class AuthRepository
@Inject
constructor(private val api: AuthApi, private val sessionManager: SessionManager) {
    suspend fun login(request: LoginRequest): Result<AuthData> {
        return try {
            val response = authenticatedApi.login(request)
            Log.d("AuthRepository", "Response: ${response.body()}")
            if (response.isSuccessful && response.body()?.success == true) {
                val authData =
                        response.body()!!.data
                                ?: return Result.failure(Exception("No auth data returned"))
                sessionManager.saveSession(authData.toSession())
                Log.d("AuthRepository", "Login successful: $authData")
                Result.success(authData)
            } else {
                val errorMessage =
                        response.body()?.message
                                ?: response.errorBody()?.string() ?: "Unknown error"
                val errorDetail =
                        response.body()?.message
                                ?: response.errorBody()?.string() ?: "No error detail"
                Log.e("AuthRepository", "Login failed: $errorMessage, Detail: $errorDetail")
                Result.failure(Exception("$errorMessage: $errorDetail"))
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Login exception: ${e.message}")
            Result.failure(e)
        }
    }

    //    suspend fun register(dto: RegisterDto): Result<AuthData> {
    //        return try {
    //            when {
    //                // Jika role adalah Owner dan laundryName tidak kosong → createLaundry
    //                dto.role == "Owner" && !dto.laundryName.isNullOrBlank() -> {
    //                    val laundryRequest = LaundryRequest(
    //                        name = dto.laundryName
    //                        // Tambahkan address & phone jika diperlukan
    //                    )
    //
    //                    val laundryResponse = api.createLaundry(laundryRequest)
    //                    if (!laundryResponse.isSuccessful || laundryResponse.body()?.data == null)
    // {
    //                        return Result.failure(Exception("Gagal membuat laundry:
    // ${laundryResponse.errorBody()?.string()}"))
    //                    }
    //
    //                    val laundryId = laundryResponse.body()!!.data.id
    //
    //                    val registerRequest = RegisterRequest(
    //                        name = dto.username,
    //                        email = dto.email,
    //                        password = dto.password,
    //                        confirmPassword = dto.confirmPassword,
    //                        role = dto.role,
    //                        laundryId = laundryId
    //                    )
    //
    //                    val registerResponse = api.register(registerRequest)
    //                    handleRegisterResponse(registerResponse)
    //                }
    //
    //                // Jika role adalah Staff dan memilih laundry yang sudah ada → getLaundries
    //                dto.role == "Staff" && !dto.selectedLaundry.isNullOrBlank() -> {
    //                    val laundriesResponse = api.getLaundries()
    //                    if (!laundriesResponse.isSuccessful ||
    // laundriesResponse.body()?.data.isNullOrEmpty()) {
    //                        return Result.failure(Exception("Gagal mengambil daftar laundry"))
    //                    }
    //
    //                    // Cari ID laundry dari nama yang dipilih
    //                    val selectedLaundry = laundriesResponse.body()!!.data.firstOrNull {
    //                        it.name == dto.selectedLaundry
    //                    } ?: return Result.failure(Exception("Laundry tidak ditemukan"))
    //
    //                    val registerRequest = RegisterRequest(
    //                        name = dto.username,
    //                        email = dto.email,
    //                        password = dto.password,
    //                        confirmPassword = dto.confirmPassword,
    //                        role = dto.role,
    //                        laundryId = selectedLaundry.id
    //                    )
    //
    //                    val response = api.register(registerRequest)
    //                    handleRegisterResponse(response)
    //                }
    //
    //                else -> {
    //                    Result.failure(Exception("Data registrasi tidak valid"))
    //                }
    //            }
    //        } catch (e: Exception) {
    //            Log.e("AuthRepository", "Register exception: ${e.message}")
    //            Result.failure(e)
    //        }
    //    }

    //    suspend fun register(request: RegisterRequest): Result<AuthData> {
    //        return try {
    //            val response = api.register(request)
    //            if (response.isSuccessful && response.body()?.success == true) {
    //                val authData = response.body()!!.data
    //                sessionManager.saveSession(authData.toSession())
    //                Log.d("AuthRepository", "Register successful: $authData")
    //                Result.success(authData)
    //            } else {
    //                Log.e("AuthRepository", "Register failed: ${response.body()?.message}")
    //                Result.failure(Exception(response.body()?.message ?: "Registration failed"))
    //            }
    //        } catch (e: Exception) {
    //            Log.e("AuthRepository", "Register exception: ${e.message}")
    //            Result.failure(e)
    //        }
    //    }

    suspend fun register(dto: RegisterDto): Result<AuthData> {
        return try {
            // Pastikan laundryId tersedia
            val laundryId =
                    when (dto.role) {
                        "Owner" -> dto.laundryId // LaundryId sudah dibuat sebelumnya
                        "Staff" ->
                                dto.selectedLaundry // Laundry dipilih dari list yang sudah diambil
                        else -> null
                    }

            if (laundryId.isNullOrBlank()) {
                return Result.failure(Exception("Laundry ID tidak tersedia"))
            }

            val registerRequest =
                    RegisterRequest(
                            name = dto.username,
                            email = dto.email,
                            password = dto.password,
                            confirmPassword = dto.confirmPassword,
                            role = dto.role,
                            laundryId = laundryId
                    )

            val response = api.register(registerRequest)
            handleRegisterResponse(response)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Register exception: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun getLaundries(): Result<List<Laundry>> {
        return try {
            val response = api.getLaundries()
            if (response.isSuccessful && response.body()?.success == true) {
                val laundries = response.body()?.data as? List<Laundry> ?: emptyList()
                Result.success(laundries)
            } else {
                Log.e("AuthRepository", "Get laundries failed: ${response.body()?.message}")
                Result.failure(Exception(response.body()?.message ?: "Failed to fetch laundries"))
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Get laundries exception: ${e.message}")
            Result.failure(e)
        }
    }

    private suspend fun handleRegisterResponse(response: Response<AuthResponse>): Result<AuthData> {
        return if (response.isSuccessful && response.body()?.success == true) {
            val authData = response.body()!!.data
            sessionManager.saveSession(authData.toSession())
            Log.d("AuthRepository", "Register successful: $authData")
            Result.success(authData)
        } else {
            Log.e("AuthRepository", "Register failed: ${response.body()?.message}")
            Result.failure(Exception(response.body()?.message ?: "Registration failed"))
        }
    }

    suspend fun isLoggedIn(): Boolean {
        return sessionManager.getSession() != null
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
