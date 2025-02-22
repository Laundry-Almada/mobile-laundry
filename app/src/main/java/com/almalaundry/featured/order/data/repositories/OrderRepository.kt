package com.almalaundry.featured.order.data.repositories

import com.almalaundry.featured.order.data.dtos.OrderResponse
import com.almalaundry.featured.order.data.source.OrderApi
import com.almalaundry.featured.order.domain.models.Order
import javax.inject.Inject

class OrderRepository @Inject constructor(
    private val api: OrderApi
) {
    suspend fun getOrders(
        status: String? = null,
        type: String? = null,
        startDate: String? = null,
        endDate: String? = null,
        search: String? = null,
        sortBy: String = "created_at",
        sortDirection: String = "desc",
        perPage: Int = 10,
        page: Int = 1
    ): Result<OrderResponse> {
        return try {
            val response = api.getOrders(
                status = status,
                type = type,
                startDate = startDate,
                endDate = endDate,
                search = search,
                sortBy = sortBy,
                sortDirection = sortDirection,
                perPage = perPage,
                page = page
            )

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch orders: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getOrderDetail(orderId: String): Result<Order> {
        return try {
            val response = api.getOrderDetail(orderId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception("Failed to fetch order detail"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
