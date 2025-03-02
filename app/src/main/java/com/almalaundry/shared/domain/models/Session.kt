package com.almalaundry.shared.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Session(
    val token: String,
    val name: String,
    val role: String, // "owner" atau "staff"
    val laundryId: String
)