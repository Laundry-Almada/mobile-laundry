package com.almalaundry.featured.order.data.dtos

import com.almalaundry.featured.order.domain.models.Service
import kotlinx.serialization.Serializable

@Serializable
data class ServiceResponse(
    val success: Boolean,
    val data: List<Service>
)