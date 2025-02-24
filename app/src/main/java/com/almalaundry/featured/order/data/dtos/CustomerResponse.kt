package com.almalaundry.featured.order.data.dtos

import com.almalaundry.featured.order.domain.models.Customer

data class CustomerResponse(
    val success: Boolean, val data: Customer
)