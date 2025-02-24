package com.almalaundry.featured.order.presentation.state

import com.almalaundry.featured.order.domain.models.Order
import com.almalaundry.featured.order.domain.models.OrderFilter

data class OrderScreenState(
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val orders: List<Order> = emptyList(),
    val totalOrders: Int = 0,
    val currentPage: Int = 1,
    val totalPages: Int = 1,
    val perPage: Int = 10,
    val hasMoreData: Boolean = true,
    val filter: OrderFilter = OrderFilter()
)