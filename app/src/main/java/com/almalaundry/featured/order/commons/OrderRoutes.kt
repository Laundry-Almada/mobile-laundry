package com.almalaundry.featured.order.commons

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class OrderRoutes(val route: String) {
    @Serializable
    @SerialName("order")
    data object Index : OrderRoutes("order")

    @Serializable
    @SerialName("order/create")
    data object Create : OrderRoutes("order/create")

    @Serializable
    @SerialName("order/{orderId}")
    data class Detail(
        val orderId: String
    ) : OrderRoutes("order/{orderId}")

    @Serializable
    @SerialName("orderHistory")
    data object History : OrderRoutes("orderHistory")

    @Serializable
    @SerialName("scan")
    data object Scan : OrderRoutes("scan")

    @Serializable
    @SerialName("print")
    data class Print(
        val barcode: String,
        val customerName: String,
        val customerPhone: String,
        val customerUsername: String,
        val serviceName: String,
        val laundryName: String,
        val weight: String,
        val totalPrice: String,
        val createdAt: String
    ) : OrderRoutes("print/{barcode}")
}