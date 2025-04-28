package com.almalaundry.featured.order.data.dtos

import com.almalaundry.featured.order.domain.models.Customer
import kotlinx.serialization.Serializable

@Serializable
data class CustomerResponse(
    val success: Boolean,
    val data: Customer? = null,
    val message: String? = null
)