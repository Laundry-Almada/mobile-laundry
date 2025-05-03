package com.almalaundry.featured.auth.data.dtos

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    @SerializedName("c_password") val confirmPassword: String,
    val role: String,
    @SerializedName("laundry_id") val laundryId: String
)