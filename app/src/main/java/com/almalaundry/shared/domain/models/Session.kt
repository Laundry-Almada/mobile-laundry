package com.almalaundry.shared.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Session(
    val token: String? = null,
    val name: String? = null,
    val role: String? = null, // "owner" atau "staff"
    val laundryId: String? = null,
    val printerAddress: String? = null
)