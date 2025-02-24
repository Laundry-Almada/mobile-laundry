package com.almalaundry.featured.order.presentation.state

import com.almalaundry.featured.order.domain.models.Order

data class HistoryOrderScreenState(
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val histories: List<Order> = emptyList(),
    val totalHistories: Int = 0,
    val currentPage: Int = 1,
    val totalPages: Int = 1,
    val perPage: Int = 10,
    val hasMoreData: Boolean = true
)