package com.almalaundry.featured.order.data.dtos

import com.almalaundry.featured.order.domain.models.Order

data class OrderResponse(
    val success: Boolean, val data: List<Order>, val meta: OrderMeta
)