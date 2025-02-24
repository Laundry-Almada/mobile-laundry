package com.almalaundry.featured.order.data.dtos

data class DeleteOrderResponse(
    val success: Boolean, val message: String, val error: String? = null
)