package com.almalaundry.featured.order.domain.models

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class Order(
    val id: String = "",
    val customer: Customer = Customer(),
    val laundry: Laundry = Laundry(),
    val service: Service = Service(),
    val status: String = "", // 'pending', 'washed', 'dried', 'ironed', 'ready_picked', 'completed', 'cancelled'
    val barcode: String = "",
    val weight: String = "",
    @SerializedName("total_price") val totalPrice: String = "",
    val note: String = "",
    @SerializedName("created_at") val createdAt: String = "",
    @SerializedName("updated_at") val updatedAt: String = "",
    @SerializedName("order_date") val orderDate: String? = null
)