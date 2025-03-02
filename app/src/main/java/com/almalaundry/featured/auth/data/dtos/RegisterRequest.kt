package com.almalaundry.featured.auth.data.dtos

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val c_password: String,
    val role: String,
    val laundry_id: String
)
