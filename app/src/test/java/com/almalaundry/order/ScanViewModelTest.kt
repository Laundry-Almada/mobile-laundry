package com.almalaundry.order

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.almalaundry.featured.order.data.repositories.OrderRepository
import com.almalaundry.featured.order.domain.models.Order
import com.almalaundry.featured.order.presentation.state.ScanScreenState
import com.almalaundry.featured.order.presentation.viewmodels.ScanViewModel
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class ScanViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: ScanViewModel
    private val repository: OrderRepository = mockk()
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ScanViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `setNavigating updates isNavigating in state`() = runTest {
        // Act
        viewModel.setNavigating(true)

        // Assert
        val state = viewModel.state.value
        assertEquals(true, state.isNavigating)
    }

    @Test
    fun `processBarcodeResult returns success when not navigating`() = runTest {
        // Arrange
        val order = Order(id = "1", status = "pending", barcode = "ORD-NzwFtbLs")
        coEvery { repository.getOrderByBarcode("ORD-NzwFtbLs") } returns Result.success(order)
        viewModel.setNavigating(false)

        // Act
        val result = viewModel.processBarcodeResult("ORD-NzwFtbLs")

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(order, result.getOrNull())
        assertEquals(true, viewModel.state.value.isNavigating)
    }

    @Test
    fun `processBarcodeResult returns failure when navigating`() = runTest {
        // Arrange
        viewModel.setNavigating(true)

        // Act
        val result = viewModel.processBarcodeResult("ORD-NzwFtbLs")

        // Assert
        assertTrue(result.isFailure)
        assertEquals("Navigation in progress", result.exceptionOrNull()?.message)
        assertEquals(true, viewModel.state.value.isNavigating) // isNavigating tetap true
    }

    @Test
    fun `processBarcodeResult returns failure when repository fails`() = runTest {
        // Arrange
        coEvery { repository.getOrderByBarcode("ORD-NzwFtbLs") } returns Result.failure(Exception("Failed to fetch order"))
        viewModel.setNavigating(false)

        // Act
        val result = viewModel.processBarcodeResult("ORD-NzwFtbLs")

        // Assert
        assertTrue(result.isFailure)
        assertEquals("Failed to fetch order", result.exceptionOrNull()?.message)
        assertEquals(true, viewModel.state.value.isNavigating)
    }

    @Test
    fun `onBarcodeDetected updates barcodeValue and stops scanning`() = runTest {
        // Act
        viewModel.onBarcodeDetected("ORD-NzwFtbLs")

        // Assert
        val state = viewModel.state.value
        assertEquals("ORD-NzwFtbLs", state.barcodeValue)
        assertEquals(false, state.isScanning)
        assertEquals(null, state.error)
    }

    @Test
    fun `startScanning sets isScanning to true and clears error`() = runTest {
        // Arrange
        viewModel.setError("Some error")
        viewModel.onBarcodeDetected("old_value")

        // Act
        viewModel.startScanning()

        // Assert
        val state = viewModel.state.value
        assertEquals(true, state.isScanning)
        assertEquals("", state.barcodeValue)
        assertEquals(null, state.error)
    }

    @Test
    fun `updatePermissionStatus updates hasPermission in state`() = runTest {
        // Act
        viewModel.updatePermissionStatus(true)

        // Assert
        val state = viewModel.state.value
        assertEquals(true, state.hasPermission)
    }

    @Test
    fun `setError updates error and stops scanning`() = runTest {
        // Arrange
        viewModel.startScanning()

        // Act
        viewModel.setError("Scan failed")

        // Assert
        val state = viewModel.state.value
        assertEquals("Scan failed", state.error)
        assertEquals(false, state.isScanning)
    }

    @Test
    fun `resetState resets state to initial values`() = runTest {
        // Arrange
        viewModel.onBarcodeDetected("ORD-NzwFtbLs")
        viewModel.setError("Some error")
        viewModel.setNavigating(true)
        viewModel.updatePermissionStatus(true)
        viewModel.startScanning()

        // Act
        viewModel.resetState()

        // Assert
        val state = viewModel.state.value
        assertEquals(ScanScreenState(), state)
        assertEquals("", state.barcodeValue)
        assertEquals(false, state.isScanning)
        assertEquals(false, state.isNavigating)
        assertEquals(false, state.hasPermission)
        assertEquals(null, state.error)
    }
}