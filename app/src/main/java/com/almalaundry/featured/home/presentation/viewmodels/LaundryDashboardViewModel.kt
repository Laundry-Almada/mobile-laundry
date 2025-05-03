package com.almalaundry.featured.home.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.almalaundry.featured.home.data.sources.HomeApi
import com.almalaundry.featured.home.presentation.state.LaundryDashboardState
import com.almalaundry.shared.commons.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class LaundryDashboardViewModel @Inject constructor(
    @Named("Authenticated") private val homeApi: HomeApi
) : ViewModel() {
    private val _state = MutableStateFlow(LaundryDashboardState())
    val state = _state.asStateFlow()

    init {
        loadStatistics()
    }

    fun loadStatistics() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val dailyResponse = homeApi.getDailyStatistics()
                val monthlyResponse = homeApi.getMonthlyStatistics()

                // Filter data bulanan
                val cleanedMonthly =
                    monthlyResponse.data.filter { it.month.matches(Regex("\\d{4}-\\d{2}")) }

                _state.update {
                    it.copy(
                        dailyStats = dailyResponse.data,
                        monthlyStats = cleanedMonthly
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Failed to load data") }
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    fun refreshSession(sessionManager: SessionManager) {
        viewModelScope.launch {
            val session = sessionManager.getSession()
            _state.update {
                it.copy(
                    userName = session?.name ?: "User",
                    laundryName = session?.laundryName ?: "Loading..."
                )
            }
        }
    }
}
