package com.almalaundry.featured.order.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.almalaundry.featured.order.presentation.state.OrderState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow(OrderState())
    val state = _state.asStateFlow()

    init {
        loadOrderModel()
    }

    private fun loadOrderModel() {
        viewModelScope.launch {
            // Load data implementation
        }
    }
}