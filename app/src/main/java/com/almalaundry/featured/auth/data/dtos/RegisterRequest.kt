package com.almalaundry.featured.auth.data.dtos

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("c_password") val confirmPassword: String,
    @SerializedName("role") val role: String,
    @SerializedName("laundry_id") val laundryId: String
//    @SerializedName("laundry_name") val laundryName: String,
//    @SerializedName("dashboard_route") val dashboardRoute: String

)