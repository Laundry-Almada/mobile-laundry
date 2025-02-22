package com.almalaundry.featured.order.domain.models

data class Order(
    val id: String = "",
    val customer: Customer = Customer(),
    val laundry: Laundry = Laundry(),
    val status: String = "",
    val type: String = "",
    val barcode: String = "",
    val weight: String = "",
    val total_price: String = "",
    val note: String = "",
    val created_at: String = "",
    val updated_at: String = ""
)