package com.almalaundry.featured.order.data.dtos

import com.google.gson.annotations.SerializedName

data class CreateOrderRequest(
    val phone: String,
    val name: String? = null,
    @SerializedName("laundry_id") val laundryId: String,
    @SerializedName("service_id") val serviceId: String,
    val weight: Double,
    @SerializedName("total_price") val totalPrice: Int,
    val note: String
)