package com.almalaundry.order

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.almalaundry.featured.home.presentation.state.CustomerDashboardState
import com.almalaundry.featured.home.presentation.viewmodels.CustomerDashboardViewModel
import com.almalaundry.featured.order.data.dtos.OrderMeta
import com.almalaundry.featured.order.data.dtos.OrderResponse
import com.almalaundry.featured.order.data.repositories.OrderRepository
import com.almalaundry.featured.order.domain.models.Order
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class CustomerDashboardViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: CustomerDashboardViewModel
    private val repository: OrderRepository = mockk()
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `updateIdentifier updates state and triggers loadOrders for valid identifier`() = runTest {
        // Arrange
        val orderResponse = OrderResponse(
            success = true,
            data = listOf(Order(id = "1", status = "pending")),
            meta = OrderMeta(totalOrders = 1, totalPages = 1, currentPage = 1, perPage = 10)
        )
        coEvery {
            repository.getCustomerOrders(identifier = "customer123", perPage = 10, page = 1)
        } returns Result.success(orderResponse)
        viewModel = CustomerDashboardViewModel(repository)

        // Act
        viewModel.updateIdentifier("customer123")
        advanceUntilIdle()

        // Assert
        val state = viewModel.state.value
        assertEquals("customer123", state.identifier)
        assertEquals(false, state.isLoading)
        assertEquals(orderResponse.data, state.orders)
        assertEquals(orderResponse.meta.totalOrders, state.totalOrders)
        assertEquals(orderResponse.meta.currentPage, state.currentPage)
        assertEquals(false, state.hasMoreData)
        assertEquals(null, state.error)
    }

    @Test
    fun `updateIdentifier does not trigger loadOrders for invalid identifier`() = runTest {
        // Arrange
        viewModel = CustomerDashboardViewModel(repository)

        // Act
        viewModel.updateIdentifier("cu") // Kurang dari 3 karakter
        advanceTimeBy(500) // Simulasi delay

        // Assert
        val state = viewModel.state.value
        assertEquals("cu", state.identifier)
        assertEquals(false, state.isLoading)
        assertEquals(emptyList<Order>(), state.orders)
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
            repository.getCustomerOrders(identifier = "customer123", perPage = 10, page = 1)
        } returns Result.success(orderResponse)
        viewModel = CustomerDashboardViewModel(repository)
        viewModel.updateIdentifier("customer123") // Set identifier

        // Act
        viewModel.loadOrders("customer123")
        advanceTimeBy(500) // Simulasi delay

        // Assert
        val state = viewModel.state.value
        assertEquals(false, state.isLoading)
        assertEquals(orderResponse.data, state.orders)
        assertEquals(orderResponse.meta.totalOrders, state.totalOrders)
        assertEquals(orderResponse.meta.currentPage, state.currentPage)
        assertEquals(false, state.hasMoreData)
        assertEquals(null, state.error)
    }

    @Test
    fun `loadOrders updates state with error when repository fails`() = runTest {
        // Arrange
        coEvery {
            repository.getCustomerOrders(identifier = "customer123", perPage = 10, page = 1)
        } returns Result.failure(Exception("Failed to fetch customer orders"))
        viewModel = CustomerDashboardViewModel(repository)
        viewModel.updateIdentifier("customer123") // Set identifier

        // Act
        viewModel.loadOrders("customer123")
        advanceTimeBy(500) // Simulasi delay

        // Assert
        val state = viewModel.state.value
        assertEquals(false, state.isLoading)
        assertEquals(emptyList<Order>(), state.orders)
        assertEquals("Failed to fetch customer orders", state.error)
    }

    @Test
    fun `loadOrders with isLoadMore appends data to existing orders`() = runTest {
        // Arrange
        val initialOrders = listOf(Order(id = "1", status = "pending"))
        val initialState = CustomerDashboardState(
            identifier = "customer123",
            orders = initialOrders,
            currentPage = 1,
            totalOrders = 1,
            hasMoreData = true
        )
        val newOrderResponse = OrderResponse(
            success = true,
            data = listOf(Order(id = "2", status = "completed")),
            meta = OrderMeta(totalOrders = 2, totalPages = 2, currentPage = 2, perPage = 10)
        )
        coEvery {
            repository.getCustomerOrders(identifier = "customer123", perPage = 10, page = 2)
        } returns Result.success(newOrderResponse)
        viewModel = CustomerDashboardViewModel(repository)
        viewModel.apply { mutableState.value = initialState } // Set initial state

        // Act
        viewModel.loadOrders("customer123", isLoadMore = true)
        advanceTimeBy(500) // Simulasi delay

        // Assert
        val state = viewModel.state.value
        assertEquals(false, state.isLoading)
        assertEquals(
            listOf(
                Order(id = "1", status = "pending"),
                Order(id = "2", status = "completed")
            ),
            state.orders
        )
        assertEquals(newOrderResponse.meta.totalOrders, state.totalOrders)
        assertEquals(newOrderResponse.meta.currentPage, state.currentPage)
        assertEquals(false, state.hasMoreData)
        assertEquals(null, state.error)
    }

    @Test
    fun `clearOrders resets orders and related fields`() = runTest {
        // Arrange
        val initialState = CustomerDashboardState(
            identifier = "customer123",
            orders = listOf(Order(id = "1", status = "pending")),
            totalOrders = 1,
            hasMoreData = true,
            currentPage = 2,
            error = "Some error"
        )
        viewModel = CustomerDashboardViewModel(repository)
        viewModel.apply { mutableState.value = initialState } // Set initial state

        // Act
        viewModel.clearOrders()

        // Assert
        val state = viewModel.state.value
        assertEquals("customer123", state.identifier)
        assertEquals(emptyList<Order>(), state.orders)
        assertEquals(0, state.totalOrders)
        assertEquals(false, state.hasMoreData)
        assertEquals(1, state.currentPage)
        assertEquals(null, state.error)
    }
}