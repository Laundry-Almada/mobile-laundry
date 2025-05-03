package com.almalaundry.featured.auth.data.dtos

import com.almalaundry.shared.domain.models.Session
import com.google.gson.annotations.SerializedName

data class AuthData(
    val token: String,
    val name: String,
    val role: String,
    @SerializedName("laundry_id") val laundryId: String,
    @SerializedName("laundry_name") val laundryName: String,
) {
    fun toSession() = Session(
        token = token,
        name = name,
        role = role,
        laundryId = laundryId,
        laundryName = laundryName
    )
}
