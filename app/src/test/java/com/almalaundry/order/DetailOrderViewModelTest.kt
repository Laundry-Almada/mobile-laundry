package com.almalaundry.order

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.almalaundry.featured.order.data.repositories.OrderRepository
import com.almalaundry.featured.order.domain.models.Order
import com.almalaundry.featured.order.presentation.viewmodels.DetailOrderViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
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
class DetailOrderViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: DetailOrderViewModel
    private val repository: OrderRepository = mockk()
    private val savedStateHandle: SavedStateHandle = mockk()
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { savedStateHandle.get<String>("orderId") } returns "1"
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init triggers loadOrderDetail and updates state with success`() = runTest {
        // Arrange
        val order = Order(id = "1", status = "pending")
        coEvery { repository.getOrderDetail("1") } returns Result.success(order)
        viewModel = DetailOrderViewModel(repository, savedStateHandle)

        // Act
        // Initialization triggers loadOrderDetail

        // Assert
        val state = viewModel.state.value
        assertEquals(false, state.isLoading)
        assertEquals(order, state.order)
        assertEquals(null, state.error)
    }

    @Test
    fun `init triggers loadOrderDetail and updates state with error`() = runTest {
        // Arrange
        coEvery { repository.getOrderDetail("1") } returns Result.failure(Exception("Failed to fetch order detail"))
        viewModel = DetailOrderViewModel(repository, savedStateHandle)

        // Act
        // Initialization triggers loadOrderDetail

        // Assert
        val state = viewModel.state.value
        assertEquals(false, state.isLoading)
        assertEquals(null, state.order)
        assertEquals("Failed to fetch order detail", state.error)
    }

    @Test
    fun `loadOrderDetail updates state with success`() = runTest {
        // Arrange
        val order = Order(id = "1", status = "pending")
        coEvery { repository.getOrderDetail("1") } returns Result.success(order)
        viewModel = DetailOrderViewModel(repository, savedStateHandle)

        // Act
        viewModel.loadOrderDetail()

        // Assert
        val state = viewModel.state.value
        assertEquals(false, state.isLoading)
        assertEquals(order, state.order)
        assertEquals(null, state.error)
    }

    @Test
    fun `loadOrderDetail updates state with error`() = runTest {
        // Arrange
        coEvery { repository.getOrderDetail("1") } returns Result.failure(Exception("Failed to fetch order detail"))
        viewModel = DetailOrderViewModel(repository, savedStateHandle)

        // Act
        viewModel.loadOrderDetail()

        // Assert
        val state = viewModel.state.value
        assertEquals(false, state.isLoading)
        assertEquals(null, state.order)
        assertEquals("Failed to fetch order detail", state.error)
    }

    @Test
    fun `updateStatus updates state with success`() = runTest {
        // Arrange
        val updatedOrder = Order(id = "1", status = "completed")
        coEvery { repository.updateOrderStatus("1", "completed") } returns Result.success(
            updatedOrder
        )
        viewModel = DetailOrderViewModel(repository, savedStateHandle)

        // Act
        viewModel.updateStatus("completed")

        // Assert
        val state = viewModel.state.value
        assertEquals(false, state.isLoading)
        assertEquals(updatedOrder, state.order)
        assertEquals(null, state.error)
    }

    @Test
    fun `updateStatus updates state with error`() = runTest {
        // Arrange
        coEvery { repository.updateOrderStatus("1", "completed") } returns Result.failure(
            Exception(
                "Failed to update status"
            )
        )
        viewModel = DetailOrderViewModel(repository, savedStateHandle)

        // Act
        viewModel.updateStatus("completed")

        // Assert
        val state = viewModel.state.value
        assertEquals(false, state.isLoading)
        assertEquals(null, state.order)
        assertEquals("Failed to update status", state.error)
    }

    @Test
    fun `deleteOrder updates state and calls onSuccess when successful`() = runTest {
        // Arrange
        coEvery { repository.deleteOrder("1") } returns Result.success(true)
        viewModel = DetailOrderViewModel(repository, savedStateHandle)
        val onSuccess: () -> Unit = mockk(relaxed = true)

        // Act
        viewModel.deleteOrder(onSuccess)

        // Assert
        val state = viewModel.state.value
        assertEquals(false, state.isLoading)
        assertEquals(null, state.error)
        verify { onSuccess() }
    }

    @Test
    fun `deleteOrder updates state with error when failed`() = runTest {
        // Arrange
        coEvery { repository.deleteOrder("1") } returns Result.failure(Exception("Failed to delete order"))
        viewModel = DetailOrderViewModel(repository, savedStateHandle)
        val onSuccess: () -> Unit = mockk(relaxed = true)

        // Act
        viewModel.deleteOrder(onSuccess)

        // Assert
        val state = viewModel.state.value
        assertEquals(false, state.isLoading)
        assertEquals("Failed to delete order", state.error)
        verify(exactly = 0) { onSuccess() }
    }
}