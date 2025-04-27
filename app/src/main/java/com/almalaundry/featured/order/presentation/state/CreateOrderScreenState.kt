package com.almalaundry.featured.order.presentation.state

import com.almalaundry.featured.order.domain.models.Service

data class CreateOrderScreenState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false,
    val phone: String = "",
    val laundryId: String = "",
    val serviceId: String = "",
    val services: List<Service> = emptyList(),
    val isLoadingServices: Boolean = false,
    val servicesError: String? = null,
    val weight: String = "",
    val totalPrice: String = "",
    val note: String = "",
    val showNameDialog: Boolean = false,
    val name: String = ""
)