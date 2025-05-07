package com.almalaundry.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.almalaundry.featured.home.data.models.ApiResponse
import com.almalaundry.featured.home.data.models.DailyStatistic
import com.almalaundry.featured.home.data.models.MonthlyStatistic
import com.almalaundry.featured.home.data.sources.HomeApi
import com.almalaundry.featured.home.presentation.viewmodels.LaundryDashboardViewModel
import com.almalaundry.shared.commons.session.SessionManager
import com.almalaundry.shared.domain.models.Session
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class LaundryDashboardViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: LaundryDashboardViewModel
    private val homeApi: HomeApi = mockk()
    private val sessionManager: SessionManager = mockk()
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadStatistics updates state with success`() = runTest {
        // Arrange
        val dailyStats = listOf(DailyStatistic(date = "2025-05-01", count = 1, revenue = 10.0))
        val monthlyStats = listOf(
            MonthlyStatistic(month = "2025-04", count = 2, revenue = 100.0),
            MonthlyStatistic(month = "invalid", count = 1, revenue = 50.0)
        )

        coEvery { homeApi.getDailyStatistics() } returns ApiResponse(success = true, data = dailyStats)
        coEvery { homeApi.getMonthlyStatistics() } returns ApiResponse(success = true, data = monthlyStats)

        viewModel = LaundryDashboardViewModel(homeApi)

        // Act
        viewModel.loadStatistics()

        // Assert
        val state = viewModel.state.value
        assertEquals(false, state.isLoading)
        assertEquals(dailyStats, state.dailyStats)
        assertEquals(1, state.monthlyStats.size) // hanya 1 data valid
        assertEquals(null, state.error)
    }

    @Test
    fun `loadStatistics updates state with error`() = runTest {
        // Arrange
        coEvery { homeApi.getDailyStatistics() } throws Exception("Network error")
        coEvery { homeApi.getMonthlyStatistics() } throws Exception("Network error")

        viewModel = LaundryDashboardViewModel(homeApi)

        // Act
        viewModel.loadStatistics()

        // Assert
        val state = viewModel.state.value
        assertEquals(false, state.isLoading)
        assertEquals("Failed to load data", state.error)
        assertTrue(state.dailyStats.isEmpty())
        assertTrue(state.monthlyStats.isEmpty())
    }

    @Test
    fun `refreshSession updates userName and laundryName from session`() = runTest {
        // Arrange
        val session = Session(name = "John Doe", laundryName = "Sparkle Wash")
        coEvery { sessionManager.getSession() } returns session

        viewModel = LaundryDashboardViewModel(homeApi)

        // Act
        viewModel.refreshSession(sessionManager)

        // Assert
        val state = viewModel.state.value
        assertEquals("John Doe", state.userName)
        assertEquals("Sparkle Wash", state.laundryName)
    }

    @Test
    fun `refreshSession uses default values when session is null`() = runTest {
        // Arrange
        coEvery { sessionManager.getSession() } returns null

        viewModel = LaundryDashboardViewModel(homeApi)

        // Act
        viewModel.refreshSession(sessionManager)

        // Assert
        val state = viewModel.state.value
        assertEquals("User", state.userName)
        assertEquals("Loading...", state.laundryName)
    }
}
