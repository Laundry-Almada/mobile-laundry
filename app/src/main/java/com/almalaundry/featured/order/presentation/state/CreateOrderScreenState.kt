package com.almalaundry.featured.order.presentation.state

data class CreateOrderScreenState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false,
    val phone: String = "",
    val laundryId: String = "",
    val type: String = "regular",
    val weight: String = "",
    val totalPrice: String = "",
    val note: String = ""
)