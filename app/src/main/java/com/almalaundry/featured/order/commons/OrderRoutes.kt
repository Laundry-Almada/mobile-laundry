package com.almalaundry.featured.order.commons

import kotlinx.serialization.Serializable

@Serializable
sealed class OrderRoutes(val route: String) {
    @Serializable
    data object Index : OrderRoutes("orders")
}