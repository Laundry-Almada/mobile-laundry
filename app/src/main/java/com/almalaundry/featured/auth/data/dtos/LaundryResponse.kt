package com.almalaundry.featured.auth.data.dtos

import com.almalaundry.featured.order.domain.models.Laundry

data class LaundryResponse (
    val success: Boolean,
    val data: List<Laundry>,
    val message: String
    )