package com.almalaundry.featured.auth.presentation.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.almalaundry.featured.auth.data.dtos.AuthData
import com.almalaundry.featured.auth.data.dtos.LaundryRequest
import com.almalaundry.featured.auth.data.dtos.RegisterRequest
import com.almalaundry.featured.auth.data.repositories.AuthRepository
import com.almalaundry.featured.order.domain.models.Laundry
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
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class RegisterViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val authRepository: AuthRepository = mockk()
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: RegisterViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        // Mock default getLaundries response
        coEvery { authRepository.getLaundries() } returns Result.success(emptyList())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init should fetch laundries`() = runTest {
        // Arrange
        val testLaundries = listOf(
            Laundry("1", "Laundry A"),
            Laundry("2", "Laundry B")
        )
        coEvery { authRepository.getLaundries() } returns Result.success(testLaundries)

        // Act
        viewModel = RegisterViewModel(authRepository)

        // Assert
        val state = viewModel.state.first()
        assertEquals(listOf("Laundry A", "Laundry B"), state.availableLaundries)
    }

    @Test
    fun `onRoleChange should reset laundry fields`() = runTest {
        // Arrange
        viewModel = RegisterViewModel(authRepository)
        viewModel.onLaundryNameChange("Test Laundry")
        viewModel.onLaundryAddressChange("Test Address")
        viewModel.onLaundryPhoneChange("81234567")
        viewModel.onSelectedLaundryChange("Laundry A")

        // Act
        viewModel.onRoleChange("owner")

        // Assert
        val state = viewModel.state.first()
        assertEquals("owner", state.role)
        assertEquals("", state.laundryName)
        assertEquals("", state.laundryAddress)
        assertEquals("", state.laundryPhone)
        assertEquals("", state.selectedLaundry)
    }

    @Test
    fun `onPasswordChange should validate password match`() = runTest {
        // Arrange
        viewModel = RegisterViewModel(authRepository)
        viewModel.onPasswordChange("password123")
        viewModel.onConfirmPasswordChange("password123")

        // Act - Change password to mismatch
        viewModel.onPasswordChange("newpassword456")

        // Assert
        val state = viewModel.state.first()
        assertEquals("Konfirmasi password tidak cocok", state.confirmPasswordError)
    }

    @Test
    fun `onConfirmPasswordChange should validate password match`() = runTest {
        // Arrange
        viewModel = RegisterViewModel(authRepository)
        viewModel.onPasswordChange("password123")

        // Act
        viewModel.onConfirmPasswordChange("different456")

        // Assert
        val state = viewModel.state.first()
        assertEquals("Konfirmasi password tidak cocok", state.confirmPasswordError)
    }

    @Test
    fun `createLaundry should validate empty fields`() = runTest {
        // Arrange
        viewModel = RegisterViewModel(authRepository)

        // Act - Try to create with empty fields
        viewModel.createLaundry()

        // Assert
        val state = viewModel.state.first()
        assertEquals("Nama laundry tidak boleh kosong", state.error)
    }

    @Test
    fun `createLaundry should validate phone number format`() = runTest {
        // Arrange
        viewModel = RegisterViewModel(authRepository)
        viewModel.onLaundryNameChange("Test Laundry")
        viewModel.onLaundryAddressChange("Test Address")
        viewModel.onLaundryPhoneChange("91234567") // Invalid - doesn't start with 8

        // Act
        viewModel.createLaundry()

        // Assert
        val state = viewModel.state.first()
        assertEquals("Nomor telepon harus diawali dengan 8", state.error)
    }

    @Test
    fun `createLaundry success should update state`() = runTest {
        // Arrange
        val newLaundry = Laundry("3", "New Laundry")
        coEvery { authRepository.createLaundry(any()) } returns Result.success(newLaundry)
        viewModel = RegisterViewModel(authRepository)
        viewModel.onLaundryNameChange("New Laundry")
        viewModel.onLaundryAddressChange("456 Avenue")
        viewModel.onLaundryPhoneChange("81234567")

        // Act
        viewModel.createLaundry()

        // Assert
        val state = viewModel.state.first()
        assertEquals(false, state.isLoading)
        assertEquals(listOf("New Laundry"), state.availableLaundries)
        assertEquals("New Laundry", state.selectedLaundry)
        assertEquals("", state.laundryName)
        assertEquals("", state.laundryAddress)
        assertEquals("", state.laundryPhone)
    }

    @Test
    fun `register should validate empty name`() = runTest {
        // Arrange
        viewModel = RegisterViewModel(authRepository)
        viewModel.onEmailChange("test@example.com")
        viewModel.onPasswordChange("password123")
        viewModel.onConfirmPasswordChange("password123")
        viewModel.onRoleChange("staff")
        viewModel.onSelectedLaundryChange("Laundry A")

        // Act
        viewModel.register()

        // Assert
        val state = viewModel.state.first()
        assertEquals("Nama tidak boleh kosong", state.error)
    }

    @Test
    fun `register should validate password requirements`() = runTest {
        // Arrange
        viewModel = RegisterViewModel(authRepository)
        viewModel.onNameChange("Test User")
        viewModel.onEmailChange("test@example.com")
        viewModel.onPasswordChange("short") // Too short
        viewModel.onConfirmPasswordChange("short")
        viewModel.onRoleChange("staff")
        viewModel.onSelectedLaundryChange("Laundry A")

        // Act
        viewModel.register()

        // Assert
        val state = viewModel.state.first()
        assertEquals("Password minimal 8 karakter", state.passwordError)
    }

    @Test
    fun `register should validate password contains letters and numbers`() = runTest {
        // Arrange
        viewModel = RegisterViewModel(authRepository)
        viewModel.onNameChange("Test User")
        viewModel.onEmailChange("test@example.com")
        viewModel.onPasswordChange("lettersonly") // No numbers
        viewModel.onConfirmPasswordChange("lettersonly")
        viewModel.onRoleChange("staff")
        viewModel.onSelectedLaundryChange("Laundry A")

        // Act
        viewModel.register()

        // Assert
        val state = viewModel.state.first()
        assertEquals("Password harus mengandung huruf dan angka", state.passwordError)
    }

    @Test
    fun `register should validate password confirmation`() = runTest {
        // Arrange
        viewModel = RegisterViewModel(authRepository)
        viewModel.onNameChange("Test User")
        viewModel.onEmailChange("test@example.com")
        viewModel.onPasswordChange("password123")
        viewModel.onConfirmPasswordChange("different456") // Mismatch
        viewModel.onRoleChange("staff")
        viewModel.onSelectedLaundryChange("Laundry A")

        // Act
        viewModel.register()

        // Assert
        val state = viewModel.state.first()
        assertEquals("Konfirmasi password tidak cocok", state.confirmPasswordError)
    }

    @Test
    fun `register should validate laundry selection`() = runTest {
        // Arrange
        viewModel = RegisterViewModel(authRepository)
        viewModel.onNameChange("Test User")
        viewModel.onEmailChange("test@example.com")
        viewModel.onPasswordChange("password123")
        viewModel.onConfirmPasswordChange("password123")
        viewModel.onRoleChange("staff")
        // Don't select laundry

        // Act
        viewModel.register()

        // Assert
        val state = viewModel.state.first()
        assertEquals("Pilih laundry terlebih dahulu", state.error)
    }

    @Test
    fun `register success should update state`() = runTest {
        // Arrange
        val testLaundries = listOf(Laundry("1", "Laundry A"))
        coEvery { authRepository.getLaundries() } returns Result.success(testLaundries)

        val authData = AuthData(
            token = "token123",
            name = "Test User",
            role = "staff",
            laundryId = "1",
            laundryName = "Laundry A",
            dashboardRoute = "/staff/dashboard"
        )
        coEvery { authRepository.register(any()) } returns Result.success(authData)

        viewModel = RegisterViewModel(authRepository)
        viewModel.onNameChange("Test User")
        viewModel.onEmailChange("test@example.com")
        viewModel.onPasswordChange("password123")
        viewModel.onConfirmPasswordChange("password123")
        viewModel.onRoleChange("staff")
        viewModel.onSelectedLaundryChange("Laundry A")

        // Act
        viewModel.register()

        // Assert
        val state = viewModel.state.first()
        assertEquals(false, state.isLoading)
        assertEquals(true, state.isSuccess)
    }

    @Test
    fun `register failure should show error`() = runTest {
        // Arrange
        val testLaundries = listOf(Laundry("1", "Laundry A"))
        coEvery { authRepository.getLaundries() } returns Result.success(testLaundries)
        coEvery { authRepository.register(any()) } returns Result.failure(Exception("Registration failed"))

        viewModel = RegisterViewModel(authRepository)
        viewModel.onNameChange("Test User")
        viewModel.onEmailChange("test@example.com")
        viewModel.onPasswordChange("password123")
        viewModel.onConfirmPasswordChange("password123")
        viewModel.onRoleChange("staff")
        viewModel.onSelectedLaundryChange("Laundry A")

        // Act
        viewModel.register()

        // Assert
        val state = viewModel.state.first()
        assertEquals(false, state.isLoading)
        assertEquals("Registration failed", state.error)
    }
}