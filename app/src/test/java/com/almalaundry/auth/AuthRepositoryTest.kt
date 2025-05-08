package com.almalaundry.featured.auth.data.repositories

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.almalaundry.featured.auth.data.dtos.AuthData
import com.almalaundry.featured.auth.data.dtos.AuthResponse
import com.almalaundry.featured.auth.data.dtos.LoginRequest
import com.almalaundry.featured.auth.data.dtos.LogoutResponse
import com.almalaundry.featured.auth.data.dtos.RegisterRequest
import com.almalaundry.featured.auth.data.source.AuthApi
import com.almalaundry.shared.commons.session.SessionManager
import io.mockk.coEvery
import io.mockk.coVerify
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
import retrofit2.Response


@ExperimentalCoroutinesApi
class AuthRepositoryTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var publicApi: AuthApi
    private lateinit var authenticatedApi: AuthApi
    private lateinit var sessionManager: SessionManager
    private lateinit var authRepository: AuthRepository

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        publicApi = mockk()
        authenticatedApi = mockk()
        sessionManager = mockk(relaxed = true)
        authRepository = AuthRepository(authenticatedApi, publicApi, sessionManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `login success returns AuthData and saves session`() = runTest {
        val request = LoginRequest("user@example.com", "password")
//        val authData = AuthData("token123", "user_id")
        val authData = AuthData(
            token = "token456",
            name = "New User",
            role = "admin",
            laundryId = "laundry123",
            laundryName = "Laundry Amanah",
            dashboardRoute = "admin/home"
        )

        val response = Response.success(AuthResponse(true, authData, "Login success"))
        coEvery { publicApi.login(request) } returns response

        val result = authRepository.login(request)

        assertTrue(result.isSuccess)
        assertEquals(authData, result.getOrNull())
        coVerify { sessionManager.saveSession(authData.toSession()) }

    }

    @Test
    fun `login failure returns error with message`() = runTest {
        val request = LoginRequest("wrong@example.com", "wrongpass")
        val response = Response.success(AuthResponse(false, AuthData(
            token = "token456",
            name = "New User",
            role = "admin",
            laundryId = "laundry123",
            laundryName = "Laundry Amanah",
            dashboardRoute = "admin/home"
        ), "Invalid credentials")) // ✅ perbaikan

        coEvery { publicApi.login(request) } returns response

        val result = authRepository.login(request)

        assertTrue(result.isFailure)
        assertEquals("Invalid credentials", result.exceptionOrNull()?.message)
    }

    @Test
    fun `register success returns AuthData and saves session`() = runTest {
        val request = RegisterRequest(
            email = "new@example.com",
            password = "newpass",
            confirmPassword = "newpass",
            name = "New User",
            laundryId = "laundry123",
            role = "admin"
        )


//        val authData = AuthData("token456", "new_id")
        val authData = AuthData(
            token = "token456",
            name = "New User",
            role = "admin",
            laundryId = "laundry123",
            laundryName = "Laundry Amanah",
            dashboardRoute = "admin/home"
        )

        val response = Response.success(AuthResponse(true, authData, "Register success"))
        coEvery { publicApi.register(request) } returns response

        val result = authRepository.register(request)

        assertTrue(result.isSuccess)
        assertEquals(authData, result.getOrNull())
        coVerify { sessionManager.saveSession(authData.toSession()) }

    }

    @Test
    fun `logout success clears session`() = runTest {
        val response = Response.success(LogoutResponse(true, emptyList(), "Logout success"))
        coEvery { authenticatedApi.logout() } returns response

        val result = authRepository.logout()

        assertTrue(result.isSuccess)
        coVerify { sessionManager.clearSession() }
    }
}