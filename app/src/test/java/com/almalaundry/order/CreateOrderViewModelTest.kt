package com.almalaundry.order

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.almalaundry.featured.order.data.dtos.CreateOrderRequest
import com.almalaundry.featured.order.data.dtos.SearchCustomersResponse
import com.almalaundry.featured.order.data.dtos.ServicesResponse
import com.almalaundry.featured.order.data.repositories.OrderRepository
import com.almalaundry.featured.order.domain.models.Customer
import com.almalaundry.featured.order.domain.models.Order
import com.almalaundry.featured.order.domain.models.Service
import com.almalaundry.featured.order.presentation.viewmodels.CreateOrderViewModel
import com.almalaundry.shared.commons.session.SessionManager
import com.almalaundry.shared.domain.models.Session
import io.mockk.coEvery
import io.mockk.every
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
class CreateOrderViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val repository: OrderRepository = mockk()
    private val sessionManager: SessionManager = mockk()
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: CreateOrderViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun setupDefaultMocks(laundryId: String = "laundry1") {
        coEvery { sessionManager.getSession() } returns mockk {
            every { this@mockk.laundryId } returns laundryId
        }
        // Mock getServices untuk mencegah MockKException
        val services = listOf(Service(id = "service1", name = "Wash"))
        val servicesResponse = ServicesResponse(success = true, data = services)
        coEvery { repository.getServices(laundryId) } returns Result.success(servicesResponse)
    }

    @Test
    fun `init loads laundryId from session and fetches services when session exists`() = runTest {
        // Arrange
        val laundryId = "laundry1"
        val session = mockk<Session> {
            every { this@mockk.laundryId } returns laundryId
        }
        coEvery { sessionManager.getSession() } returns session
        val services = listOf(Service(id = "service1", name = "Wash"))
        val servicesResponse = ServicesResponse(success = true, data = services)
        coEvery { repository.getServices(laundryId) } returns Result.success(servicesResponse)

        // Act
        viewModel = CreateOrderViewModel(repository, sessionManager)

        // Assert
        val state = viewModel.state.first()
        assertEquals(laundryId, state.laundryId)
        assertEquals(services, state.services)
        assertEquals("service1", state.serviceId)
        assertEquals(false, state.isLoadingServices)
        assertEquals(null, state.servicesError)
    }

    @Test
    fun `init sets error when session is null`() = runTest {
        // Arrange
        coEvery { sessionManager.getSession() } returns null

        // Act
        viewModel = CreateOrderViewModel(repository, sessionManager)

        // Assert
        val state = viewModel.state.first()
        assertEquals("", state.laundryId)
        assertEquals("Sesi tidak ditemukan, silakan login kembali", state.servicesError)
        assertEquals(emptyList<Service>(), state.services)
        assertEquals(false, state.isLoadingServices)
    }

    @Test
    fun `fetchServices updates state with services on success`() = runTest {
        // Arrange
        val laundryId = "laundry1"
        coEvery { sessionManager.getSession() } returns mockk { every { this@mockk.laundryId } returns laundryId }
        val services = listOf(Service(id = "service1", name = "Wash"))
        val servicesResponse = ServicesResponse(success = true, data = services)
        coEvery { repository.getServices(laundryId) } returns Result.success(servicesResponse)
        viewModel = CreateOrderViewModel(repository, sessionManager)

        // Act
        viewModel.fetchServices(laundryId)

        // Assert
        val state = viewModel.state.first()
        assertEquals(services, state.services)
        assertEquals("service1", state.serviceId)
        assertEquals(false, state.isLoadingServices)
        assertEquals(null, state.servicesError)
    }

    @Test
    fun `fetchServices sets error on failure`() = runTest {
        // Arrange
        val laundryId = "laundry1"
        coEvery { sessionManager.getSession() } returns mockk { every { this@mockk.laundryId } returns laundryId }
        coEvery { repository.getServices(laundryId) } returns Result.failure(Exception("Network error"))
        viewModel = CreateOrderViewModel(repository, sessionManager)

        // Act
        viewModel.fetchServices(laundryId)

        // Assert
        val state = viewModel.state.first()
        assertEquals(emptyList<Service>(), state.services)
        assertEquals("Network error", state.servicesError)
        assertEquals(false, state.isLoadingServices)
    }

    @Test
    fun `searchCustomers updates state with customers on success`() = runTest {
        // Arrange
        setupDefaultMocks()
        viewModel = CreateOrderViewModel(repository, sessionManager)
        viewModel.updateName("John")
        val customers = listOf(Customer(id = "1", name = "John Doe"))
        val searchResponse =
            SearchCustomersResponse(success = true, data = customers, message = "Success")
        coEvery { repository.searchCustomers("John") } returns Result.success(searchResponse)

        // Act
        viewModel.searchCustomers()

        // Assert
        val state = viewModel.state.first()
        assertEquals(customers, state.customers)
        assertEquals(false, state.isLoadingCustomers)
        assertEquals(null, state.customerSearchError)
    }

    @Test
    fun `searchCustomers sets empty list when query is too short`() = runTest {
        // Arrange
        setupDefaultMocks()
        viewModel = CreateOrderViewModel(repository, sessionManager)
        viewModel.updateName("J") // Query < 2 chars

        // Act
        viewModel.searchCustomers()

        // Assert
        val state = viewModel.state.first()
        assertEquals(emptyList<Customer>(), state.customers)
        assertEquals(false, state.isLoadingCustomers)
        assertEquals(null, state.customerSearchError)
    }

    @Test
    fun `searchCustomers sets error on failure`() = runTest {
        // Arrange
        setupDefaultMocks()
        viewModel = CreateOrderViewModel(repository, sessionManager)
        viewModel.updateName("John")
        coEvery { repository.searchCustomers("John") } returns Result.failure(Exception("Network error"))

        // Act
        viewModel.searchCustomers()

        // Assert
        val state = viewModel.state.first()
        assertEquals(emptyList<Customer>(), state.customers)
        assertEquals("Network error", state.customerSearchError)
        assertEquals(false, state.isLoadingCustomers)
    }

    @Test
    fun `selectCustomer updates state with customer details`() = runTest {
        // Arrange
        setupDefaultMocks()
        viewModel = CreateOrderViewModel(repository, sessionManager)
        val customer =
            Customer(id = "1", name = "John Doe", phone = "123456789", username = "johndoe")

        // Act
        viewModel.selectCustomer(customer)

        // Assert
        val state = viewModel.state.first()
        assertEquals("John Doe", state.name)
        assertEquals("123456789", state.phone)
        assertEquals("", state.username)
        assertEquals(emptyList<Customer>(), state.customers)
    }

    @Test
    fun `createOrder fails when name is blank`() = runTest {
        // Arrange
        setupDefaultMocks()
        viewModel = CreateOrderViewModel(repository, sessionManager)
        viewModel.updateName("")

        // Act
        viewModel.createOrder()

        // Assert
        val state = viewModel.state.first()
        assertEquals("Nama pelanggan harus diisi", state.error)
        assertEquals(false, state.isLoading)
        assertEquals(false, state.success)
    }

    @Test
    fun `createOrder fails when phone is blank with hasWhatsApp true`() = runTest {
        // Arrange
        setupDefaultMocks()
        viewModel = CreateOrderViewModel(repository, sessionManager)
        viewModel.updateName("John Doe")
        viewModel.updateHasWhatsApp(true)
        viewModel.updatePhone("")
        viewModel.updateServiceId("service1")
        viewModel.updateWeight("1.0")
        viewModel.updateTotalPrice("100")

        // Act
        viewModel.createOrder()

        // Assert
        val state = viewModel.state.first()
        assertEquals("Nomor telepon harus diisi jika menggunakan WhatsApp", state.error)
        assertEquals(false, state.isLoading)
        assertEquals(false, state.success)
    }

    @Test
    fun `createOrder succeeds and resets state on success`() = runTest {
        // Arrange
        setupDefaultMocks()
        viewModel = CreateOrderViewModel(repository, sessionManager)
        viewModel.updateName("John Doe")
        viewModel.updatePhone("123456789")
        viewModel.updateHasWhatsApp(false)
        viewModel.updateServiceId("service1")
        viewModel.updateWeight("1.0")
        viewModel.updateTotalPrice("100")
        val order = Order(id = "1", status = "pending")
        coEvery { repository.createOrder(any<CreateOrderRequest>()) } returns Result.success(order)

        // Act
        viewModel.createOrder()

        // Assert
        val state = viewModel.state.first()
        assertEquals(true, state.success)
        assertEquals(null, state.error)
        assertEquals(false, state.isLoading)
        assertEquals("", state.name)
        assertEquals("", state.phone)
        assertEquals("", state.username)
        assertEquals(emptyList<Customer>(), state.customers)
    }

    @Test
    fun `createOrder sets specific error when username is duplicate`() = runTest {
        // Arrange
        coEvery { sessionManager.getSession() } returns mockk { every { this@mockk.laundryId } returns "laundry1" }
        val services = listOf(Service(id = "service1", name = "Wash"))
        val servicesResponse = ServicesResponse(success = true, data = services)
        coEvery { repository.getServices("laundry1") } returns Result.success(servicesResponse) // Mock getServices
        viewModel = CreateOrderViewModel(repository, sessionManager)
        viewModel.updateName("John Doe")
        viewModel.updateUsername("johndoe")
        viewModel.updateHasWhatsApp(false) // Ensure WhatsApp is not used
        viewModel.updatePhone("123456789") // Ensure phone is valid
        viewModel.updateServiceId("service1")
        viewModel.updateWeight("1.0")
        viewModel.updateTotalPrice("100")
        coEvery { repository.createOrder(any<CreateOrderRequest>()) } returns Result.failure(
            Exception("Duplicate username")
        )

        // Act
        viewModel.createOrder()

        // Assert
        val state = viewModel.state.first()
        assertEquals(
            "Username sudah digunakan, silakan masukkan username lain atau kosongkan untuk otomatis",
            state.error
        )
        assertEquals(false, state.isLoading)
        assertEquals(false, state.success)
    }
}