package com.almalaundry.featured.auth.data.dtos

data class AuthResponse(
    val success: Boolean,
    val data: AuthData,
    val message: String
)