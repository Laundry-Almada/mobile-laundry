package com.almalaundry.featured.order.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Laundry(
    val id: String = "",
    val name: String = "",
    val phone: String = "",
)