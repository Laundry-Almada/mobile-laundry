package com.almalaundry.featured.order.data.dtos

import com.almalaundry.featured.order.domain.models.Order

data class OrderDetailResponse(
    val success: Boolean, val data: Order
)