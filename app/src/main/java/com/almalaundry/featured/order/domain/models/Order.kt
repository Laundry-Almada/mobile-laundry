package com.almalaundry.featured.order.domain.models

data class Order(
    val id: Int,
    val customerName: String,
    val phoneNumber: String,
    val type: String,
    val status: String,
    val barcode: String,
    val weight: Double,
    val totalPrice: Double,
    val note: String,
    val createdAt: String
)