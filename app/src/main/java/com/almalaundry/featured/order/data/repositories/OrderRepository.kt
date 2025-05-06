package com.almalaundry.featured.order.data.repositories

import android.util.Log
import com.almalaundry.featured.order.data.dtos.CreateOrderRequest
import com.almalaundry.featured.order.data.dtos.OrderMeta
import com.almalaundry.featured.order.data.dtos.OrderResponse
import com.almalaundry.featured.order.data.dtos.SearchCustomersResponse
import com.almalaundry.featured.order.data.dtos.ServicesResponse
import com.almalaundry.featured.order.data.dtos.UpdateStatusRequest
import com.almalaundry.featured.order.data.source.OrderApi
import com.almalaundry.featured.order.domain.models.Order
import com.almalaundry.shared.commons.ErrorHandler
import com.almalaundry.shared.commons.session.SessionManager
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class OrderRepository @Inject constructor(
    @Named("Authenticated") private val authenticatedApi: OrderApi,
    @Named("Public") private val publicApi: OrderApi,
    private val sessionManager: SessionManager
) {
    suspend fun getOrders(
        status: String? = null,
        serviceId: String? = null,
        startDate: String? = null,
        endDate: String? = null,
        search: String? = null,
        sortBy: String = "created_at",
        sortDirection: String = "desc",
        perPage: Int = 10,
        page: Int = 1
    ): Result<OrderResponse> {
        return try {
            if (!sessionManager.isLoggedIn()) {
                throw Exception("User not logged in")
            }
            val response = authenticatedApi.getOrders(
                status = status,
                serviceId = serviceId,
                startDate = startDate,
                endDate = endDate,
                search = search,
                sortBy = sortBy,
                sortDirection = sortDirection,
                perPage = perPage,
                page = page
            )
            Log.d("OrderRepository", "Response: ${response.body()}")

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("OrderRepository", "Error: $errorBody")
                Result.failure(Exception(ErrorHandler.parseApiError(errorBody)))
            }
        } catch (e: Exception) {
            Log.e("OrderRepository", "Exception: ${e.message}", e)
            Result.failure(Exception(ErrorHandler.getErrorMessage(e)))
        }
    }

    suspend fun getCustomerOrders(
        identifier: String,
        perPage: Int = 10,
        page: Int = 1
    ): Result<OrderResponse> {
        return try {
            val response = publicApi.getCustomerOrders(
                identifier = identifier,
                perPage = perPage,
                page = page
            )
            Log.d("OrderRepository", "Customer Orders Response: ${response.body()}")

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else if (response.code() == 404) {
                // Kembalikan sukses dengan data kosong untuk 404
                Result.success(
                    OrderResponse(
                        success = false,
                        data = emptyList(),
                        meta = OrderMeta(
                            totalOrders = 0,
                            totalPages = 0,
                            currentPage = 1,
                            perPage = perPage
                        )
                    )
                )
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("OrderRepository", "Error: $errorBody")
                Result.failure(Exception(ErrorHandler.parseApiError(errorBody)))
            }
        } catch (e: Exception) {
            Log.e("OrderRepository", "Exception: ${e.message}", e)
            Result.failure(Exception(ErrorHandler.getErrorMessage(e)))
        }
    }

    suspend fun getOrderDetail(orderId: String): Result<Order> {
        return try {
            Log.d("OrderRepository", "Fetching order detail: $orderId")
            val response = authenticatedApi.getOrderDetail(orderId)
            Log.d("OrderRepository", "Response code: ${response.code()}, body: ${response.body()}")

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data)
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("OrderRepository", "Error: $errorBody")
                Result.failure(Exception(ErrorHandler.parseApiError(errorBody)))
            }
        } catch (e: Exception) {
            Log.e("OrderRepository", "Exception: ${e.message}", e)
            Result.failure(Exception(ErrorHandler.getErrorMessage(e)))
        }
    }

    suspend fun getOrderByBarcode(barcode: String): Result<Order> {
        return try {
            Log.d("OrderRepository", "Fetching order with barcode: $barcode")
            val response = authenticatedApi.getOrderByBarcode(barcode)
            Log.d("OrderRepository", "Response: ${response.code()}")

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data)
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("OrderRepository", "Error: $errorBody")
                Result.failure(Exception(ErrorHandler.parseApiError(errorBody)))
            }
        } catch (e: Exception) {
            Log.e("OrderRepository", "Exception: ${e.message}", e)
            Result.failure(Exception(ErrorHandler.getErrorMessage(e)))
        }
    }

    suspend fun createOrder(request: CreateOrderRequest): Result<Order> {
        return try {
            val response = authenticatedApi.createOrder(request)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data)
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("OrderRepository", "Create order failed: $errorBody")
                Result.failure(Exception(ErrorHandler.parseApiError(errorBody)))
            }
        } catch (e: Exception) {
            Log.e("OrderRepository", "Create order exception: ${e.message}", e)
            Result.failure(Exception(ErrorHandler.getErrorMessage(e)))
        }
    }

    suspend fun searchCustomers(query: String): Result<SearchCustomersResponse> {
        return try {
            val response = authenticatedApi.searchCustomers(query)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("OrderRepository", "Search customers failed: $errorBody")
                Result.success(
                    SearchCustomersResponse(
                        success = false,
                        data = emptyList(),
                        message = ErrorHandler.parseApiError(errorBody)
                    )
                )
            }
        } catch (e: Exception) {
            Log.e("OrderRepository", "Search customers exception: ${e.message}", e)
            Result.failure(Exception(ErrorHandler.getErrorMessage(e)))
        }
    }

    suspend fun getServices(laundryId: String): Result<ServicesResponse> {
        return try {
            if (!sessionManager.isLoggedIn()) {
                throw Exception("User not logged in")
            }
            val response = authenticatedApi.getServices(laundryId)
            Log.d("OrderRepository", "Service Response: ${response.body()}")

            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("OrderRepository", "Service Error: $errorBody")
                Result.failure(Exception(ErrorHandler.parseApiError(errorBody)))
            }
        } catch (e: Exception) {
            Log.e("OrderRepository", "Service Exception: ${e.message}", e)
            Result.failure(Exception(ErrorHandler.getErrorMessage(e)))
        }
    }

    suspend fun updateOrderStatus(orderId: String, status: String): Result<Order> {
        return try {
            val response = authenticatedApi.updateOrderStatus(orderId, UpdateStatusRequest(status))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data)
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("OrderRepository", "Update status failed: $errorBody")
                Result.failure(Exception(ErrorHandler.parseApiError(errorBody)))
            }
        } catch (e: Exception) {
            Log.e("OrderRepository", "Update status exception: ${e.message}", e)
            Result.failure(Exception(ErrorHandler.getErrorMessage(e)))
        }
    }

    suspend fun deleteOrder(orderId: String): Result<Boolean> {
        return try {
            val response = authenticatedApi.deleteOrder(orderId)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success) {
                    Result.success(true)
                } else {
                    Log.e("OrderRepository", "Delete order failed: ${body.error ?: body.message}")
                    Result.failure(
                        Exception(
                            ErrorHandler.parseApiError(
                                body.error ?: body.message
                            )
                        )
                    )
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("OrderRepository", "Delete order failed: $errorBody")
                Result.failure(Exception(ErrorHandler.parseApiError(errorBody)))
            }
        } catch (e: Exception) {
            Log.e("OrderRepository", "Delete order exception: ${e.message}", e)
            Result.failure(Exception(ErrorHandler.getErrorMessage(e)))
        }
    }
}