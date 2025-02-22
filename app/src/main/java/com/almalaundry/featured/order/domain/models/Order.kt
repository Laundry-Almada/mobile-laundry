package com.almalaundry.featured.order.domain.models

import com.google.gson.annotations.SerializedName

data class Order(
    val id: String = "",
    val customer: Customer = Customer(),
    val laundry: Laundry = Laundry(),
    val status: String = "",
    val type: String = "",
    val barcode: String = "",
    val weight: String = "",
    @SerializedName("total_price") val totalPrice: String = "",
    val note: String = "",
    @SerializedName("created_at") val createdAt: String = "",
    @SerializedName("updated_at") val updatedAt: String = ""
)