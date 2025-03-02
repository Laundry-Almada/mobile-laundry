package com.almalaundry.featured.auth.data.dtos

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    @SerializedName("c_password") val cPassword: String,
    val role: String,
    @SerializedName("laundry_id") val laundryId: String
)
