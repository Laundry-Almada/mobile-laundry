package com.almalaundry.featured.order.presentation.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.almalaundry.featured.order.data.dtos.OrderMeta
import com.almalaundry.featured.order.data.dtos.OrderResponse
import com.almalaundry.featured.order.data.repositories.OrderRepository
import com.almalaundry.featured.order.domain.models.Customer
import com.almalaundry.featured.order.domain.models.Laundry
import com.almalaundry.featured.order.domain.models.Order
import com.almalaundry.featured.order.domain.models.OrderFilter
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
    val instantExecutorRule = InstantTaskExecutorRule() // Untuk sinkronisasi Flow

    private lateinit var viewModel: OrderViewModel
    private lateinit var repository: OrderRepository

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher) // Set dispatcher untuk Coroutines
        repository = mockk() // Mock repository
        viewModel = OrderViewModel(repository) // Inisialisasi ViewModel
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // Reset dispatcher setelah test
    }

    @Test
    fun should_load_orders_successfully_on_init() = runTest {
        // Arrange
        val orders = listOf(Order(id = "1", customer = Customer(), laundry = Laundry()))
        val meta = OrderMeta(totalOrders = 1, totalPages = 1, currentPage = 1, perPage = 10)
        val response = OrderResponse(success = true, data = orders, meta = meta)
        coEvery {
            repository.getOrders(
                status = null,
                type = null,
                startDate = null,
                endDate = null,
                search = null,
                sortBy = "created_at",
                sortDirection = "desc",
                perPage = 10,
                page = 1
            )
        } returns Result.success(response)

        // Act - Inisialisasi sudah memanggil loadOrders di init
        val state = viewModel.state.value

        // Assert
        assertEquals(false, state.isLoading)
        assertEquals(orders, state.orders)
        assertEquals(1, state.totalOrders)
        assertEquals(1, state.currentPage)
        assertEquals(1, state.totalPages)
        assertEquals(null, state.error)
    }

    @Test
    fun should_show_error_when_load_orders_fails() = runTest {
        // Arrange
        val errorMessage = "Network error"
        coEvery {
            repository.getOrders(any(), any(), any(), any(), any(), any(), any(), any(), any())
        } returns Result.failure(Exception(errorMessage))

        // Act - Inisialisasi sudah memanggil loadOrders di init
        val state = viewModel.state.value

        // Assert
        assertEquals(false, state.isLoading)
        assertEquals(emptyList<Order>(), state.orders)
        assertEquals(errorMessage, state.error)
    }

    @Test
    fun should_load_more_orders_successfully() = runTest {
        // Arrange
        val initialOrders = listOf(Order(id = "1"))
        val moreOrders = listOf(Order(id = "2"))
        val initialMeta = OrderMeta(totalOrders = 2, totalPages = 2, currentPage = 1, perPage = 1)
        val moreMeta = OrderMeta(totalOrders = 2, totalPages = 2, currentPage = 2, perPage = 1)

        coEvery {
            repository.getOrders(
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                perPage = 1,
                page = 1
            )
        } returns Result.success(OrderResponse(true, initialOrders, initialMeta))
        coEvery {
            repository.getOrders(
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                perPage = 1,
                page = 2
            )
        } returns Result.success(OrderResponse(true, moreOrders, moreMeta))

        // Act
        viewModel.loadOrders() // Load halaman pertama
        viewModel.loadOrders(isLoadMore = true) // Load halaman kedua

        // Assert
        val state = viewModel.state.value
        assertEquals(false, state.isLoadingMore)
        assertEquals(initialOrders + moreOrders, state.orders) // Gabungan order
        assertEquals(2, state.totalOrders)
        assertEquals(2, state.currentPage)
        assertEquals(2, state.totalPages)
        assertEquals(null, state.error)
    }

    @Test
    fun should_not_load_more_when_no_more_pages() = runTest {
        // Arrange
        val orders = listOf(Order(id = "1"))
        val meta = OrderMeta(totalOrders = 1, totalPages = 1, currentPage = 1, perPage = 1)
        coEvery {
            repository.getOrders(
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                perPage = 1,
                page = 1
            )
        } returns Result.success(OrderResponse(true, orders, meta))

        // Act
        viewModel.loadOrders() // Load halaman pertama
        viewModel.loadOrders(isLoadMore = true) // Coba load lebih, tapi tidak ada halaman lagi

        // Assert
        val state = viewModel.state.value
        assertEquals(false, state.isLoadingMore)
        assertEquals(orders, state.orders) // Tidak bertambah
        assertEquals(1, state.currentPage) // Tidak berubah
    }

    @Test
    fun should_apply_filter_and_load_orders() = runTest {
        // Arrange
        val filter = OrderFilter(status = listOf("pending"), type = "express")
        val filteredOrders = listOf(Order(id = "1", status = "pending", type = "express"))
        val meta = OrderMeta(totalOrders = 1, totalPages = 1, currentPage = 1, perPage = 10)
        coEvery {
            repository.getOrders(
                status = "pending",
                type = "express",
                startDate = null,
                endDate = null,
                search = null,
                sortBy = "created_at",
                sortDirection = "desc",
                perPage = 10,
                page = 1
            )
        } returns Result.success(OrderResponse(true, filteredOrders, meta))

        // Act
        viewModel.applyFilter(filter)

        // Assert
        val state = viewModel.state.value
        assertEquals(false, state.isLoading)
        assertEquals(filteredOrders, state.orders)
        assertEquals(filter, state.filter)
        assertEquals(null, state.error)
    }
}