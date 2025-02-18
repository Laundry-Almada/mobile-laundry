package com.almalaundry.featured.order.presentation.state

import com.almalaundry.featured.order.domain.models.Order

data class OrderState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val orders: List<Order> = emptyList()
)