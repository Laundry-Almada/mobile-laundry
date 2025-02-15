package com.almalaundry.featured.auth.domain.models

import com.almalaundry.featured.auth.data.dtos.LoginDto
import com.almalaundry.featured.auth.data.dtos.RegisterDto
import com.almalaundry.featured.auth.data.dtos.UserDto

interface AuthRepository {
    suspend fun login(loginDto: LoginDto): Result<UserDto>
    suspend fun register(registerDto: RegisterDto): Result<UserDto>
    fun isLoggedIn(): Boolean
}