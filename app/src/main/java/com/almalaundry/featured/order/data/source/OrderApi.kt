package com.almalaundry.featured.order.data.source

import com.almalaundry.featured.order.data.dtos.CreateOrderRequest
import com.almalaundry.featured.order.data.dtos.CustomerResponse
import com.almalaundry.featured.order.data.dtos.DeleteOrderResponse
import com.almalaundry.featured.order.data.dtos.OrderDetailResponse
import com.almalaundry.featured.order.data.dtos.OrderResponse
import com.almalaundry.featured.order.data.dtos.SearchCustomersResponse
import com.almalaundry.featured.order.data.dtos.ServicesResponse
import com.almalaundry.featured.order.data.dtos.UpdateStatusRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface OrderApi {
    @GET("orders")
    suspend fun getOrders(
        @Query("status") status: String? = null,
        @Query("service_id") serviceId: String? = null,
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null,
        @Query("search") search: String? = null,
        @Query("sort_by") sortBy: String = "created_at",
        @Query("sort_direction") sortDirection: String = "desc",
        @Query("per_page") perPage: Int = 10,
        @Query("page") page: Int = 1
    ): Response<OrderResponse>

    @GET("orders/{orderId}")
    suspend fun getOrderDetail(@Path("orderId") orderId: String): Response<OrderDetailResponse>

    @GET("orders/barcode/{barcode}")
    suspend fun getOrderByBarcode(@Path("barcode") barcode: String): Response<OrderDetailResponse>

    @POST("orders")
    suspend fun createOrder(@Body request: CreateOrderRequest): Response<OrderDetailResponse>

    @PATCH("orders/{orderId}/status")
    suspend fun updateOrderStatus(
        @Path("orderId") orderId: String, @Body statusRequest: UpdateStatusRequest
    ): Response<OrderDetailResponse>

    @DELETE("orders/{orderId}")
    suspend fun deleteOrder(@Path("orderId") orderId: String): Response<DeleteOrderResponse>

    @GET("laundries/{laundryId}/services")
    suspend fun getServices(@Path("laundryId") laundryId: String): Response<ServicesResponse>


    @GET("customers/check/{identifier}")
    suspend fun checkCustomer(@Path("identifier") identifier: String): Response<CustomerResponse>

    @GET("customers/{phone}/orders")
    suspend fun getCustomerOrders(
        @Path("phone") phone: String,
        @Query("per_page") perPage: Int = 10,
        @Query("page") page: Int = 1
    ): Response<OrderResponse>

    @GET("customers/search")
    suspend fun searchCustomers(@Query("name") query: String): Response<SearchCustomersResponse>
}