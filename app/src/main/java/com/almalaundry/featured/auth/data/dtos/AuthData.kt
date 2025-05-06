package com.almalaundry.featured.auth.data.dtos

import com.almalaundry.shared.domain.models.Session
import com.google.gson.annotations.SerializedName

data class AuthData(
    @SerializedName("token") val token: String,
    @SerializedName("name") val name: String,
    @SerializedName("role") val role: String,
    @SerializedName("laundry_id") val laundryId: String,
    @SerializedName("laundry_name") val laundryName: String,
    @SerializedName("dashboard_route") val dashboardRoute: String,
) {
    fun toSession() = Session(
        token = token,
        name = name,
        role = role,
        laundryId = laundryId,
        laundryName = laundryName
    )
}
