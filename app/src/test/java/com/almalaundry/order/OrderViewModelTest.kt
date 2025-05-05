package com.almalaundry.order

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.almalaundry.featured.order.data.dtos.OrderMeta
import com.almalaundry.featured.order.data.dtos.OrderResponse
import com.almalaundry.featured.order.data.repositories.OrderRepository
import com.almalaundry.featured.order.domain.models.Order
import com.almalaundry.featured.order.presentation.state.OrderScreenState
import com.almalaundry.featured.order.presentation.viewmodels.OrderViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class OrderViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val repository: OrderRepository = mockk()
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: OrderViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init triggers loadOrders and updates state with success`() = runTest {
        // Arrange
        val orderResponse = OrderResponse(
            success = true,
            data = listOf(Order(id = "1", status = "pending")),
            meta = OrderMeta(totalOrders = 1, totalPages = 1, currentPage = 1, perPage = 10)
        )
        coEvery {
            repository.getOrders(
                status = null, serviceId = null, startDate = null, endDate = null,
                search = null, sortBy = "created_at", sortDirection = "desc", perPage = 10, page = 1
            )
        } returns Result.success(orderResponse)

        // Act
        viewModel = OrderViewModel(repository) // Inisialisasi setelah mock disiapkan

        // Assert
        val state = viewModel.state.first()
        assertEquals(false, state.isLoading)
        assertEquals(orderResponse.data, state.orders)
        assertEquals(orderResponse.meta.totalOrders, state.totalOrders)
        assertEquals(orderResponse.meta.currentPage, state.currentPage)
        assertEquals(orderResponse.meta.totalPages, state.totalPages)
        assertEquals(null, state.error)
    }

    @Test
    fun `loadOrders updates state with success`() = runTest {
        // Arrange
        val orderResponse = OrderResponse(
            success = true,
            data = listOf(Order(id = "1", status = "pending")),
            meta = OrderMeta(totalOrders = 1, totalPages = 1, currentPage = 1, perPage = 10)
        )
        coEvery {
            repository.getOrders(
                status = null, serviceId = null, startDate = null, endDate = null,
                search = null, sortBy = "created_at", sortDirection = "desc", perPage = 10, page = 1
            )
        } returns Result.success(orderResponse)

        // Act
        viewModel = OrderViewModel(repository)
        viewModel.loadOrders()

        // Assert
        val state = viewModel.state.first()
        assertEquals(false, state.isLoading)
        assertEquals(orderResponse.data, state.orders)
        assertEquals(orderResponse.meta.totalOrders, state.totalOrders)
        assertEquals(orderResponse.meta.currentPage, state.currentPage)
        assertEquals(orderResponse.meta.totalPages, state.totalPages)
        assertEquals(null, state.error)
    }

    @Test
    fun `loadOrders updates state with error when repository fails`() = runTest {
        // Arrange
        coEvery {
            repository.getOrders(
                status = null, serviceId = null, startDate = null, endDate = null,
                search = null, sortBy = "created_at", sortDirection = "desc", perPage = 10, page = 1
            )
        } returns Result.failure(Exception("Failed to fetch orders"))

        // Act
        viewModel = OrderViewModel(repository)
        viewModel.loadOrders()

        // Assert
        val state = viewModel.state.first()
        assertEquals(false, state.isLoading)
        assertEquals(emptyList<Order>(), state.orders)
        assertEquals("Failed to fetch orders", state.error)
    }

    @Test
    fun `loadOrders with isLoadMore appends data to existing orders`() = runTest {
        // Arrange
        val initialState = OrderScreenState(
            orders = listOf(Order(id = "1", status = "pending")),
            currentPage = 1,
            totalPages = 2,
            hasMoreData = true
        )
        coEvery {
            repository.getOrders(
                status = null, serviceId = null, startDate = null, endDate = null,
                search = null, sortBy = "created_at", sortDirection = "desc", perPage = 10, page = 1
            )
        } returns Result.success(
            OrderResponse(
                success = true,
                data = initialState.orders,
                meta = OrderMeta(totalOrders = 1, totalPages = 2, currentPage = 1, perPage = 10)
            )
        )

        viewModel = OrderViewModel(repository)
        val newOrderResponse = OrderResponse(
            success = true,
            data = listOf(Order(id = "2", status = "completed")),
            meta = OrderMeta(totalOrders = 2, totalPages = 2, currentPage = 2, perPage = 10)
        )
        coEvery {
            repository.getOrders(
                status = null, serviceId = null, startDate = null, endDate = null,
                search = null, sortBy = "created_at", sortDirection = "desc", perPage = 10, page = 2
            )
        } returns Result.success(newOrderResponse)

        // Act
        viewModel.loadOrders(isLoadMore = true)

        // Assert
        val state = viewModel.state.first()
        assertEquals(false, state.isLoadingMore)
        assertEquals(
            listOf(
                Order(id = "1", status = "pending"),
                Order(id = "2", status = "completed")
            ), state.orders
        )
        assertEquals(newOrderResponse.meta.totalOrders, state.totalOrders)
        assertEquals(newOrderResponse.meta.currentPage, state.currentPage)
        assertEquals(newOrderResponse.meta.totalPages, state.totalPages)
        assertEquals(false, state.hasMoreData) // No more data since currentPage == totalPages
    }
}