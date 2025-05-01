package com.almalaundry.featured.home.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.almalaundry.featured.home.data.sources.HomeApi
import com.almalaundry.featured.home.presentation.state.DashboardLaundryState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardLaundryViewModel @Inject constructor(
    private val homeApi: HomeApi
) : ViewModel() {
    private val _state = MutableStateFlow(DashboardLaundryState())
    val state: StateFlow<DashboardLaundryState> = _state

    init {
        fetchData()
    }

    fun fetchData() {
        fetchMonthlyStatistics()
        fetchDailyStatistics()
    }

//    fun fetchMonthlyStatistics() {
//        viewModelScope.launch {
//            _state.update { it.copy(isLoading = true) }
//            try {
//                val response = homeApi.getMonthlyStatistics(months = 12)
//                if (response.success) {
//                    _state.update {
//                        it.copy(
//                            monthlyData = response.data,
//                            isLoading = false
//                        )
//                    }
//                }
//            } catch (e: Exception) {
//                _state.update {
//                    it.copy(
//                        errorMessage = e.message,
//                        isLoading = false
//                    )
//                }
//            }
//        }
//    }

    private fun fetchMonthlyStatistics() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val response = homeApi.getMonthlyStatistics(months = 12)
                _state.update {
                    it.copy(
                        monthlyData = if (response.success) response.data else emptyList(),
                        isLoading = false,
                        errorMessage = if (!response.success) "Failed to load monthly data" else null
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Network error: ${e.localizedMessage}"
                    )
                }
            }
        }
    }

//    fun fetchDailyStatistics() {
//        viewModelScope.launch {
//            _state.update { it.copy(isLoading = true) }
//            try {
//                val response = homeApi.getDailyStatistics(days = 365)
//                if (response.success) {
//                    _state.update {
//                        it.copy(
//                            dailyData = response.data,
//                            isLoading = false
//                        )
//                    }
//                }
//            } catch (e: Exception) {
//                _state.update {
//                    it.copy(
//                        errorMessage = e.message,
//                        isLoading = false
//                    )
//                }
//            }
//        }
//    }

    private fun fetchDailyStatistics() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val response = homeApi.getDailyStatistics(days = 365)
                _state.update {
                    it.copy(
                        dailyData = if (response.success) response.data else emptyList(),
                        isLoading = false,
                        errorMessage = if (!response.success) "Failed to load daily data" else null
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Network error: ${e.localizedMessage}"
                    )
                }
            }
        }
    }

    fun refreshData() {
        fetchData()
    }
}


//package com.almalaundry.featured.home.presentation.viewmodels
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.almalaundry.featured.home.presentation.state.DashboardUserState
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.launch
//import javax.inject.Inject
//
//@HiltViewModel
//class DashboardLaundryViewModel @Inject constructor() : ViewModel() {
//    private val _state = MutableStateFlow(DashboardUserState())
//    val state = _state.asStateFlow()
//
//    init {
//        loadDashboardData()
//    }
//
//    private fun loadDashboardData() {
//        viewModelScope.launch {
//            // Load data implementation
//        }
//    }
//}