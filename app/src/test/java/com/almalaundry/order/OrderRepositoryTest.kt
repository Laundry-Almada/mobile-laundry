package com.almalaundry.order

import android.util.Log
import com.almalaundry.featured.order.data.dtos.OrderMeta
import com.almalaundry.featured.order.data.dtos.OrderResponse
import com.almalaundry.featured.order.data.repositories.OrderRepository
import com.almalaundry.featured.order.data.source.OrderApi
import com.almalaundry.featured.order.domain.models.Order
import com.almalaundry.shared.commons.session.SessionManager
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
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
}