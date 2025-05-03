package com.almalaundry.featured.auth.data.dtos

import com.almalaundry.featured.order.domain.models.Laundry

data class DetailLaundryResponse(
    val success: Boolean,
    val data: Laundry,
    val message: String
)