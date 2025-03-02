package com.almalaundry.featured.auth.data.repositories

import com.almalaundry.featured.auth.data.dtos.LoginDto
import com.almalaundry.featured.auth.data.dtos.RegisterDto
import com.almalaundry.featured.auth.data.dtos.UserDto
import com.almalaundry.featured.auth.domain.models.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor() : AuthRepository {
    // Dummy data
    private val dummyUser = UserDto(
        id = "1", username = "admin", token = "dummy_token"
    )

    override suspend fun login(loginDto: LoginDto): Result<UserDto> {
        return if (loginDto.username == "admin" && loginDto.password == "admin") {
            Result.success(dummyUser)
        } else {
            Result.failure(Exception("Invalid credentials"))
        }
    }

    override suspend fun register(registerDto: RegisterDto): Result<UserDto> {
        return if (registerDto.username.isNotBlank() && registerDto.password.isNotBlank()) {
            Result.success(dummyUser.copy(username = registerDto.username))
        } else {
            Result.failure(Exception("Invalid registration data"))
        }
    }

    override fun isLoggedIn(): Boolean {
        return false
    }
}