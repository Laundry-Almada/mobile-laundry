package com.almalaundry.featured.order.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Customer(
    val id: String = "",
    val name: String = "",
    val phone: String? = null,
    val username: String? = null
)