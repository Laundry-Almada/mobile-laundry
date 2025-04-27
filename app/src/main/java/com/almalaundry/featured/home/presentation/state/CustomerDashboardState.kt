package com.almalaundry.featured.home.presentation.state

import com.almalaundry.featured.order.domain.models.Order
import kotlinx.serialization.Serializable

@Serializable
data class CustomerDashboardState(
    val phone: String = "",
    val isLoading: Boolean = false,
    val orders: List<Order> = emptyList(),
    val error: String? = null,
    val totalOrders: Int = 0,
    val hasMoreData: Boolean = false,
    val currentPage: Int = 1
)