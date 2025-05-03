package com.almalaundry.featured.order.data.dtos

import com.almalaundry.featured.order.domain.models.Customer
import kotlinx.serialization.Serializable

@Serializable
data class SearchCustomersResponse(
    val success: Boolean,
    val data: List<Customer> = emptyList(),
    val message: String? = null
)