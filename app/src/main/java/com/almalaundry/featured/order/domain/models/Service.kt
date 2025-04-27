package com.almalaundry.featured.order.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Service(
    val id: String = "",
    val name: String = "",
    val description: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)