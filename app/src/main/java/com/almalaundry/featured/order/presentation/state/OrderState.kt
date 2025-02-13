package com.almalaundry.featured.order.presentation.state

data class OrderState(
    val isLoading: Boolean = false,
    val error: String? = null,
    //  other state properties
)
