package com.almalaundry.featured.order.presentation.state

import com.almalaundry.featured.order.domain.models.Order

data class DetailOrderState(
    val order: Order? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)