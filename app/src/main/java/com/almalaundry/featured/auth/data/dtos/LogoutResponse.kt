package com.almalaundry.featured.auth.data.dtos

data class LogoutResponse(
    val success: Boolean, val data: List<Any>?, // Data adalah array kosong, bisa nullable
    val message: String
)