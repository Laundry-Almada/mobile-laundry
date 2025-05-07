package com.almalaundry.featured.auth.presentation.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.almalaundry.featured.auth.data.dtos.AuthData
import com.almalaundry.featured.auth.data.dtos.LoginRequest
import com.almalaundry.featured.auth.data.repositories.AuthRepository
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
class LoginViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val authRepository: AuthRepository = mockk()
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onEmailChange updates email state`() = runTest {
        // Arrange
        viewModel = LoginViewModel(authRepository)
        val testEmail = "test@example.com"

        // Act
        viewModel.onEmailChange(testEmail)

        // Assert
        assertEquals(testEmail, viewModel.state.first().email)
    }

    @Test
    fun `onPasswordChange updates password state`() = runTest {
        // Arrange
        viewModel = LoginViewModel(authRepository)
        val testPassword = "password123"

        // Act
        viewModel.onPasswordChange(testPassword)

        // Assert
        assertEquals(testPassword, viewModel.state.first().password)
    }

    @Test
    fun `login sets loading state and success on successful login`() = runTest {
        // Arrange
        val testEmail = "test@example.com"
        val testPassword = "password123"
        val authData = AuthData(
            token = "token123",
            name = "Test User",
            role = "admin",
            laundryId = "laundry1",
            laundryName = "Test Laundry",
            dashboardRoute = "/admin/dashboard"
        )

        coEvery { authRepository.login(LoginRequest(testEmail, testPassword)) } returns Result.success(authData)
        viewModel = LoginViewModel(authRepository)
        viewModel.onEmailChange(testEmail)
        viewModel.onPasswordChange(testPassword)

        // Act
        viewModel.login()

        // Assert
        val state = viewModel.state.first()
        assertEquals(false, state.isLoading)
        assertEquals(true, state.isSuccess)
        assertEquals("token123", state.token)
        assertEquals("Test User", state.name)
        assertEquals(null, state.error)
    }

    @Test
    fun `login sets loading state and error on failed login`() = runTest {
        // Arrange
        val testEmail = "test@example.com"
        val testPassword = "password123"
        val errorMessage = "Invalid credentials"

        coEvery { authRepository.login(LoginRequest(testEmail, testPassword)) } returns Result.failure(Exception(errorMessage))
        viewModel = LoginViewModel(authRepository)
        viewModel.onEmailChange(testEmail)
        viewModel.onPasswordChange(testPassword)

        // Act
        viewModel.login()

        // Assert
        val state = viewModel.state.first()
        assertEquals(false, state.isLoading)
        assertEquals(false, state.isSuccess)
        assertEquals(errorMessage, state.error)
    }
}