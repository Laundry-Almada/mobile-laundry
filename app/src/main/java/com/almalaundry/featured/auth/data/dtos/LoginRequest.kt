package com.almalaundry.featured.auth.data.dtos

data class LoginRequest(
    val email: String,
    val password: String
)