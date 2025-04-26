package com.almalaundry.featured.profile.data.model

data class UserResponse(
    val success: Boolean,
    val data: UserData,
    val message: String
)

data class UserData(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    val laundry: Laundry
)

data class Laundry(
    val id: String,
    val name: String
)

