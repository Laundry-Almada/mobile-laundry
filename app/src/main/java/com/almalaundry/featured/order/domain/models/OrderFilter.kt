package com.almalaundry.featured.order.domain.models

data class OrderFilter(
    val status: List<String> = emptyList(),
    val serviceId: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val search: String? = null,
    val sortBy: String = "created_at",
    val sortDirection: String = "desc"
)
