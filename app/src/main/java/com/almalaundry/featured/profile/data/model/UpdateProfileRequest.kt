package com.almalaundry.featured.profile.data.model

data class UpdateProfileRequest(
    val name: String,
    val email: String,
    val password: String? // optional
)
