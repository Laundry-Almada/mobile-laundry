package com.almalaundry.order

import android.util.Log
import com.almalaundry.featured.order.data.dtos.CreateOrderRequest
import com.almalaundry.featured.order.data.dtos.DeleteOrderResponse
import com.almalaundry.featured.order.data.dtos.OrderDetailResponse
import com.almalaundry.featured.order.data.dtos.OrderMeta
import com.almalaundry.featured.order.data.dtos.OrderResponse
import com.almalaundry.featured.order.data.dtos.SearchCustomersResponse
import com.almalaundry.featured.order.data.dtos.ServicesResponse
import com.almalaundry.featured.order.data.dtos.UpdateStatusRequest
import com.almalaundry.featured.order.data.repositories.OrderRepository
import com.almalaundry.featured.order.data.source.OrderApi
import com.almalaundry.featured.order.domain.models.Customer
import com.almalaundry.featured.order.domain.models.Order
import com.almalaundry.featured.order.domain.models.Service
import com.almalaundry.shared.commons.session.SessionManager
import io.mockk.EqMatcher
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class OrderRepositoryTest {
    private lateinit var repository: OrderRepository
    private val authenticatedApi: OrderApi = mockk()
    private val publicApi: OrderApi = mockk()
    private val sessionManager: SessionManager = mockk()

    @Before
    fun setUp() {
        // Mock android.util.Log
        mockkStatic(Log::class)
        every { Log.v(any(), any()) } returns 0
        every { Log.d(any(), any()) } returns 0
        every { Log.i(any(), any()) } returns 0
        every { Log.w(any<String>(), any<String>()) } returns 0
        every { Log.e(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0

        repository = OrderRepository(authenticatedApi, publicApi, sessionManager)
    }

    @Test
    fun `getOrders returns success when response is successful`() = runTest {
        // Arrange
        coEvery { sessionManager.isLoggedIn() } returns true
        val orderResponse = OrderResponse(
            success = true,
            data = listOf(Order(id = "1", status = "pending")),
            meta = OrderMeta(totalOrders = 1, totalPages = 1, currentPage = 1, perPage = 10)
        )
        coEvery {
            authenticatedApi.getOrders(
                status = null, serviceId = null, startDate = null, endDate = null,
                search = null, sortBy = "created_at", sortDirection = "desc", perPage = 10, page = 1
            )
        } returns Response.success(orderResponse)

        // Act
        val result = repository.getOrders()

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(orderResponse, result.getOrNull())
    }

    @Test
    fun `getOrders returns failure when user is not logged in`() = runTest {
        // Arrange
        coEvery { sessionManager.isLoggedIn() } returns false

        // Act
        val result = repository.getOrders()

        // Assert
        assertTrue(result.isFailure)
        assertEquals("User not logged in", result.exceptionOrNull()?.message)
    }

    @Test
    fun `getOrders returns failure when response is not successful`() = runTest {
        // Arrange
        coEvery { sessionManager.isLoggedIn() } returns true
        coEvery {
            authenticatedApi.getOrders(
                status = null, serviceId = null, startDate = null, endDate = null,
                search = null, sortBy = "created_at", sortDirection = "desc", perPage = 10, page = 1
            )
        } returns Response.error(500, "Internal Server Error".toResponseBody())

        // Act
        val result = repository.getOrders()

        // Assert
        assertTrue(result.isFailure)
        assertEquals(
            "Failed to fetch orders: Internal Server Error",
            result.exceptionOrNull()?.message
        )
    }

    @Test
    fun `getOrders returns failure when network exception occurs`() = runTest {
        // Arrange
        coEvery { sessionManager.isLoggedIn() } returns true
        coEvery {
            authenticatedApi.getOrders(
                status = null, serviceId = null, startDate = null, endDate = null,
                search = null, sortBy = "created_at", sortDirection = "desc", perPage = 10, page = 1
            )
        } throws Exception("Network error")

        // Act
        val result = repository.getOrders()

        // Assert
        assertTrue(result.isFailure)
        assertEquals("Network error", result.exceptionOrNull()?.message)
    }

    @Test
    fun `getCustomerOrders returns success when response is successful`() = runTest {
        // Arrange
        val orderResponse = OrderResponse(
            success = true,
            data = listOf(Order(id = "1", status = "pending")),
            meta = OrderMeta(totalOrders = 1, totalPages = 1, currentPage = 1, perPage = 10)
        )
        coEvery {
            publicApi.getCustomerOrders(identifier = "customer123", perPage = 10, page = 1)
        } returns Response.success(orderResponse)

        // Act
        val result = repository.getCustomerOrders(identifier = "customer123")

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(orderResponse, result.getOrNull())
    }

    @Test
    fun `getCustomerOrders returns success with empty data when response is 404`() = runTest {
        // Arrange
        val emptyOrderResponse = OrderResponse(
            success = false,
            data = emptyList(),
            meta = OrderMeta(totalOrders = 0, totalPages = 0, currentPage = 1, perPage = 10)
        )
        coEvery {
            publicApi.getCustomerOrders(identifier = "customer123", perPage = 10, page = 1)
        } returns Response.error(404, "Not Found".toResponseBody())

        // Act
        val result = repository.getCustomerOrders(identifier = "customer123")

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(emptyOrderResponse, result.getOrNull())
    }

    @Test
    fun `getCustomerOrders returns failure when response is not successful`() = runTest {
        // Arrange
        coEvery {
            publicApi.getCustomerOrders(identifier = "customer123", perPage = 10, page = 1)
        } returns Response.error(500, "Internal Server Error".toResponseBody())

        // Act
        val result = repository.getCustomerOrders(identifier = "customer123")

        // Assert
        assertTrue(result.isFailure)
        assertEquals(
            "Failed to fetch customer orders: Response.error()",
            result.exceptionOrNull()?.message
        )
    }

    @Test
    fun `getCustomerOrders returns failure when network exception occurs`() = runTest {
        // Arrange
        coEvery {
            publicApi.getCustomerOrders(identifier = "customer123", perPage = 10, page = 1)
        } throws Exception("Network error")

        // Act
        val result = repository.getCustomerOrders(identifier = "customer123")

        // Assert
        assertTrue(result.isFailure)
        assertEquals("Network error", result.exceptionOrNull()?.message)
    }

    @Test
    fun `getOrderDetail returns success when response is successful`() = runTest {
        // Arrange
        val order = Order(id = "1", status = "pending")
        val orderDetailResponse = OrderDetailResponse(success = true, data = order)
        coEvery { authenticatedApi.getOrderDetail("1") } returns Response.success(
            orderDetailResponse
        )

        // Act
        val result = repository.getOrderDetail("1")

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(order, result.getOrNull())
    }

    @Test
    fun `getOrderDetail returns failure when response is not successful`() = runTest {
        // Arrange
        coEvery { authenticatedApi.getOrderDetail("1") } returns Response.error(
            404,
            "Not Found".toResponseBody()
        )

        // Act
        val result = repository.getOrderDetail("1")

        // Assert
        assertTrue(result.isFailure)
        assertEquals("Failed to fetch order detail: 404", result.exceptionOrNull()?.message)
    }

    @Test
    fun `getOrderDetail returns failure when network exception occurs`() = runTest {
        // Arrange
        coEvery { authenticatedApi.getOrderDetail("1") } throws Exception("Network error")

        // Act
        val result = repository.getOrderDetail("1")

        // Assert
        assertTrue(result.isFailure)
        assertEquals("Network error", result.exceptionOrNull()?.message)
    }

    @Test
    fun `updateOrderStatus returns success when response is successful`() = runTest {
        // Arrange
        val updatedOrder = Order(id = "1", status = "completed")
        val updateResponse = OrderDetailResponse(success = true, data = updatedOrder)
        coEvery {
            authenticatedApi.updateOrderStatus("1", UpdateStatusRequest("completed"))
        } returns Response.success(updateResponse)

        // Act
        val result = repository.updateOrderStatus("1", "completed")

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(updatedOrder, result.getOrNull())
    }

    @Test
    fun `updateOrderStatus returns failure when response is not successful`() = runTest {
        // Arrange
        coEvery {
            authenticatedApi.updateOrderStatus("1", UpdateStatusRequest("completed"))
        } returns Response.error(400, "Bad Request".toResponseBody())

        // Act
        val result = repository.updateOrderStatus("1", "completed")

        // Assert
        assertTrue(result.isFailure)
        assertEquals("Failed to update order status", result.exceptionOrNull()?.message)
    }

    @Test
    fun `updateOrderStatus returns failure when network exception occurs`() = runTest {
        // Arrange
        coEvery {
            authenticatedApi.updateOrderStatus("1", UpdateStatusRequest("completed"))
        } throws Exception("Network error")

        // Act
        val result = repository.updateOrderStatus("1", "completed")

        // Assert
        assertTrue(result.isFailure)
        assertEquals("Network error", result.exceptionOrNull()?.message)
    }

    @Test
    fun `deleteOrder returns success when response is successful`() = runTest {
        // Arrange
        val deleteResponse = DeleteOrderResponse(success = true, message = "Order deleted")
        coEvery { authenticatedApi.deleteOrder("1") } returns Response.success(deleteResponse)

        // Act
        val result = repository.deleteOrder("1")

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(true, result.getOrNull())
    }

    @Test
    fun `deleteOrder returns failure when response indicates failure`() = runTest {
        // Arrange
        val deleteResponse =
            DeleteOrderResponse(success = false, message = "Order not found", error = "Not found")
        coEvery { authenticatedApi.deleteOrder("1") } returns Response.success(deleteResponse)

        // Act
        val result = repository.deleteOrder("1")

        // Assert
        assertTrue(result.isFailure)
        assertEquals("Not found", result.exceptionOrNull()?.message)
    }

    @Test
    fun `deleteOrder returns failure when response is not successful`() = runTest {
        // Arrange
        coEvery { authenticatedApi.deleteOrder("1") } returns Response.error(
            500,
            "Internal Server Error".toResponseBody()
        )

        // Act
        val result = repository.deleteOrder("1")

        // Assert
        assertTrue(result.isFailure)
        assertEquals("Failed to delete order", result.exceptionOrNull()?.message)
    }

    @Test
    fun `deleteOrder returns failure when network exception occurs`() = runTest {
        // Arrange
        coEvery { authenticatedApi.deleteOrder("1") } throws Exception("Network error")

        // Act
        val result = repository.deleteOrder("1")

        // Assert
        assertTrue(result.isFailure)
        assertEquals("Network error", result.exceptionOrNull()?.message)
    }

    @Test
    fun `getOrderByBarcode returns success when response is successful`() = runTest {
        // Arrange
        val order = Order(id = "1", status = "pending", barcode = "123456")
        val orderDetailResponse = OrderDetailResponse(success = true, data = order)
        coEvery { authenticatedApi.getOrderByBarcode("123456") } returns Response.success(
            orderDetailResponse
        )

        // Act
        val result = repository.getOrderByBarcode("123456")

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(order, result.getOrNull())
    }

    @Test
    fun `getOrderByBarcode returns failure when response is not successful`() = runTest {
        // Arrange
        coEvery { authenticatedApi.getOrderByBarcode("123456") } returns Response.error(
            404,
            "Not Found".toResponseBody()
        )

        // Act
        val result = repository.getOrderByBarcode("123456")

        // Assert
        assertTrue(result.isFailure)
        assertEquals("Failed to fetch order by barcode", result.exceptionOrNull()?.message)
    }

    @Test
    fun `getOrderByBarcode returns failure when network exception occurs`() = runTest {
        // Arrange
        coEvery { authenticatedApi.getOrderByBarcode("123456") } throws Exception("Network error")

        // Act
        val result = repository.getOrderByBarcode("123456")

        // Assert
        assertTrue(result.isFailure)
        assertEquals("Network error", result.exceptionOrNull()?.message)
    }

    @Test
    fun `createOrder returns success when response is successful`() = runTest {
        // Arrange
        val order = Order(id = "1", status = "pending")
        val createOrderResponse = OrderDetailResponse(success = true, data = order)
        coEvery { authenticatedApi.createOrder(any<CreateOrderRequest>()) } returns Response.success(
            createOrderResponse
        )

        // Act
        val result = repository.createOrder(
            CreateOrderRequest(
                name = "John Doe",
                laundryId = "laundry1",
                serviceId = "service1",
                weight = 1.0,
                totalPrice = 100
            )
        )

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(order, result.getOrNull())
    }

    @Test
    fun `createOrder returns failure when response is not successful`() = runTest {
        // Arrange
        val errorBody = """{"message":"Invalid request","error":"Duplicate username"}"""
        coEvery { authenticatedApi.createOrder(any<CreateOrderRequest>()) } returns Response.error(
            400,
            errorBody.toResponseBody()
        )

        // Mock JSONObject untuk parsing di kode produksi
        mockkConstructor(JSONObject::class)
        every { constructedWith<JSONObject>(EqMatcher(errorBody)).getString("message") } returns "Invalid request"
        every {
            constructedWith<JSONObject>(EqMatcher(errorBody)).optString(
                "error",
                ""
            )
        } returns "Duplicate username"

        // Act
        val result = repository.createOrder(
            CreateOrderRequest(
                name = "John Doe",
                laundryId = "laundry1",
                serviceId = "service1",
                weight = 1.0,
                totalPrice = 100
            )
        )

        // Assert
        assertTrue(result.isFailure)
        assertEquals("Invalid request: Duplicate username", result.exceptionOrNull()?.message)
    }

    @Test
    fun `createOrder returns failure when network exception occurs`() = runTest {
        // Arrange
        coEvery { authenticatedApi.createOrder(any<CreateOrderRequest>()) } throws Exception("Network error")

        // Act
        val result = repository.createOrder(
            CreateOrderRequest(
                name = "John Doe",
                laundryId = "laundry1",
                serviceId = "service1",
                weight = 1.0,
                totalPrice = 100
            )
        )

        // Assert
        assertTrue(result.isFailure)
        assertEquals("Network error", result.exceptionOrNull()?.message)
    }

    @Test
    fun `searchCustomers returns success when response is successful`() = runTest {
        // Arrange
        val customers = listOf(Customer(id = "1", name = "John Doe"))
        val searchResponse =
            SearchCustomersResponse(success = true, data = customers, message = "Success")
        coEvery { authenticatedApi.searchCustomers("John") } returns Response.success(searchResponse)

        // Act
        val result = repository.searchCustomers("John")

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(searchResponse, result.getOrNull())
    }

    @Test
    fun `searchCustomers returns success with empty data when response is not successful`() =
        runTest {
            // Arrange
            val errorBody = "No customers found"
            coEvery { authenticatedApi.searchCustomers("John") } returns Response.error(
                404,
                errorBody.toResponseBody()
            )

            // Act
            val result = repository.searchCustomers("John")

            // Assert
            assertTrue(result.isSuccess)
            assertEquals(
                SearchCustomersResponse(
                    success = false,
                    data = emptyList(),
                    message = errorBody
                ), result.getOrNull()
            )
        }

    @Test
    fun `searchCustomers returns failure when network exception occurs`() = runTest {
        // Arrange
        coEvery { authenticatedApi.searchCustomers("John") } throws Exception("Network error")

        // Act
        val result = repository.searchCustomers("John")

        // Assert
        assertTrue(result.isFailure)
        assertEquals("Network error", result.exceptionOrNull()?.message)
    }

    @Test
    fun `getServices returns success when response is successful`() = runTest {
        // Arrange
        coEvery { sessionManager.isLoggedIn() } returns true
        val services = listOf(Service(id = "1", name = "Wash"))
        val servicesResponse = ServicesResponse(success = true, data = services)
        coEvery { authenticatedApi.getServices("laundry1") } returns Response.success(
            servicesResponse
        )

        // Act
        val result = repository.getServices("laundry1")

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(servicesResponse, result.getOrNull())
    }

    @Test
    fun `getServices returns failure when user is not logged in`() = runTest {
        // Arrange
        coEvery { sessionManager.isLoggedIn() } returns false

        // Act
        val result = repository.getServices("laundry1")

        // Assert
        assertTrue(result.isFailure)
        assertEquals("User not logged in", result.exceptionOrNull()?.message)
    }

    @Test
    fun `getServices returns failure when response is not successful`() = runTest {
        // Arrange
        coEvery { sessionManager.isLoggedIn() } returns true
        val errorBody = """{"message":"Invalid laundry ID"}"""
        coEvery { authenticatedApi.getServices("laundry1") } returns Response.error(
            400,
            errorBody.toResponseBody()
        )

        // Mock JSONObject untuk parsing di kode produksi
        mockkConstructor(JSONObject::class)
        every { constructedWith<JSONObject>(EqMatcher(errorBody)).getString("message") } returns "Invalid laundry ID"

        // Act
        val result = repository.getServices("laundry1")

        // Assert
        assertTrue(result.isFailure)
        assertEquals("Invalid laundry ID", result.exceptionOrNull()?.message)
    }

    @Test
    fun `getServices returns failure when network exception occurs`() = runTest {
        // Arrange
        coEvery { sessionManager.isLoggedIn() } returns true
        coEvery { authenticatedApi.getServices("laundry1") } throws Exception("Network error")

        // Act
        val result = repository.getServices("laundry1")

        // Assert
        assertTrue(result.isFailure)
        assertEquals("Network error", result.exceptionOrNull()?.message)
    }
}