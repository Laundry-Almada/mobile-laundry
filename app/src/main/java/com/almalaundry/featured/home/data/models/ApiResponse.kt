package com.almalaundry.featured.home.data.models

data class ApiResponse<T>(
    val success: Boolean,
    val data: T
)

