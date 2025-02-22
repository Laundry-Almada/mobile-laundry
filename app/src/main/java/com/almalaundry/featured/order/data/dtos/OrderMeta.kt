package com.almalaundry.featured.order.data.dtos

import com.google.gson.annotations.SerializedName

data class OrderMeta(
    @SerializedName("total_orders")
    val totalOrders: Int,
    @SerializedName("total_pages")
    val totalPages: Int,
    @SerializedName("current_page")
    val currentPage: Int,
    @SerializedName("per_page")
    val perPage: Int
)