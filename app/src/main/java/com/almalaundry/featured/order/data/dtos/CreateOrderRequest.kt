package com.almalaundry.featured.order.data.dtos

import com.google.gson.annotations.SerializedName

data class CreateOrderRequest(
    @SerializedName("name") val name: String? = null,
    @SerializedName("phone") val phone: String? = null,
    @SerializedName("username") val username: String? = null,
    @SerializedName("laundry_id") val laundryId: String,
    @SerializedName("service_id") val serviceId: String,
    @SerializedName("weight") val weight: Double,
    @SerializedName("total_price") val totalPrice: Int,
    @SerializedName("note") val note: String? = null,
)