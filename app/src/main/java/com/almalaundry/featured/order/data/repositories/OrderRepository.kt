package com.almalaundry.featured.order.data.repositories

import android.util.Log
import com.almalaundry.featured.order.data.dtos.CreateOrderRequest
import com.almalaundry.featured.order.data.dtos.CustomerResponse
import com.almalaundry.featured.order.data.dtos.OrderResponse
import com.almalaundry.featured.order.data.dtos.ServicesResponse
import com.almalaundry.featured.order.data.dtos.UpdateStatusRequest
import com.almalaundry.featured.order.data.source.OrderApi
import com.almalaundry.featured.order.domain.models.Order
import com.almalaundry.shared.commons.session.SessionManager
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderRepository @Inject constructor(
    private val api: OrderApi, private val sessionManager: SessionManager
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
            val response = api.getOrders(
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
                Log.e("OrderRepository", "Error: ${response.errorBody()?.string()}")
                Result.failure(Exception("Failed to fetch orders: ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e("OrderRepository", "Exception: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun getOrderDetail(orderId: String): Result<Order> {
        return try {
            Log.d("OrderRepository", "Fetching order detail: $orderId")
            val response = api.getOrderDetail(orderId)
            Log.d("OrderRepository", "Response code: ${response.code()}")
            Log.d("OrderRepository", "Response body: ${response.body()}")
            Log.d("OrderRepository", "Error body: ${response.errorBody()?.string()}")

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data)
            } else {
                Log.e("OrderRepository", "Error: ${response.errorBody()?.string()}")
                Result.failure(Exception("Failed to fetch order detail: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e("OrderRepository", "Exception: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun getOrderByBarcode(barcode: String): Result<Order> {
        return try {
            Log.d("OrderRepository", "Fetching order with barcode: $barcode")
            val response = api.getOrderByBarcode(barcode)
            Log.d("OrderRepository", "Response: ${response.code()}")

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data)
            } else {
                Log.e("OrderRepository", "Error: ${response.errorBody()?.string()}")
                Result.failure(Exception("Failed to fetch order by barcode"))
            }
        } catch (e: Exception) {
            Log.e("OrderRepository", "Exception: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun checkCustomer(phone: String): Result<CustomerResponse> {
        return try {
            val response = api.checkCustomer(phone)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Customer tidak ditemukan"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createOrder(request: CreateOrderRequest): Result<Order> {
        return try {
            val response = api.createOrder(request)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data)
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("OrderRepository", "Create order failed: $errorBody")
                val errorMessage = try {
                    val jsonObject = JSONObject(errorBody ?: "")
                    val message = jsonObject.getString("message")
                    val errorDetail = jsonObject.optString("error", "")
                    if (errorDetail.isNotEmpty()) "$message: $errorDetail" else message
                } catch (e: Exception) {
                    "Failed to create order"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Log.e("OrderRepository", "Create order exception: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun getServices(laundryId: String): Result<ServicesResponse> {
        return try {
            if (!sessionManager.isLoggedIn()) {
                throw Exception("User not logged in")
            }
            val response = api.getServices(laundryId)
            Log.d("OrderRepository", "Service Response: ${response.body()}")
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("OrderRepository", "Service Error: $errorBody")
                val errorMessage = try {
                    val jsonObject = JSONObject(errorBody ?: "")
                    jsonObject.getString("message")
                } catch (e: Exception) {
                    "Failed to fetch services"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Log.e("OrderRepository", "Service Exception: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun updateOrderStatus(orderId: String, status: String): Result<Order> {
        return try {
            val response = api.updateOrderStatus(orderId, UpdateStatusRequest(status))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception("Failed to update order status"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteOrder(orderId: String): Result<Boolean> {
        return try {
            val response = api.deleteOrder(orderId)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success) {
                    Result.success(true)
                } else {
                    Result.failure(Exception(body.error ?: body.message))
                }
            } else {
                Result.failure(Exception("Failed to delete order"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}