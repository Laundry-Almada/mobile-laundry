package com.almalaundry.featured.home.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.almalaundry.featured.home.presentation.state.DashboardUserState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardLaundryViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow(DashboardUserState())
    val state = _state.asStateFlow()

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        viewModelScope.launch {
            // Load data implementation
        }
    }
}