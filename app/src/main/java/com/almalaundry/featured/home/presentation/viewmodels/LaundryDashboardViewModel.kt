package com.almalaundry.featured.home.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.almalaundry.featured.home.data.models.DailyStatistic
import com.almalaundry.featured.home.data.models.MonthlyStatistic
import com.almalaundry.featured.home.data.sources.HomeApi
import com.almalaundry.featured.home.presentation.state.HomeState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class LaundryDashboardViewModel @Inject constructor(
    @Named("Authenticated") private val homeApi: HomeApi
) : ViewModel() {

    data class LaundryDashboardState(
        val dailyStats: List<DailyStatistic> = emptyList(),
        val monthlyStats: List<MonthlyStatistic> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null
    )

    private val _state = MutableStateFlow(LaundryDashboardState())
    val state = _state.asStateFlow()

    init { loadStatistics() }

    fun loadStatistics() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            try {
                val dailyResponse = homeApi.getDailyStatistics()
                val monthlyResponse = homeApi.getMonthlyStatistics()

                // Filter data bulanan
                val cleanedMonthly = monthlyResponse.data?.filter {
                    it.month.matches(Regex("\\d{4}-\\d{2}"))
                } ?: emptyList()

                _state.update {
                    it.copy(
                        dailyStats = dailyResponse.data ?: emptyList(),
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
}
