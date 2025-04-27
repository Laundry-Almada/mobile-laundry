package com.almalaundry.featured.order.presentation.state

import com.almalaundry.featured.order.domain.models.Service

data class ServiceState(
    val services: List<Service> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)