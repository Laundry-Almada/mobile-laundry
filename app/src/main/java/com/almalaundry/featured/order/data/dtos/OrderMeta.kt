package com.almalaundry.featured.order.data.dtos

data class OrderMeta(
    val total_orders: Int,
    val total_pages: Int,
    val current_page: Int,
    val per_page: Int
)