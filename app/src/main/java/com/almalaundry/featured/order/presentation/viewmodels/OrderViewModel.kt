package com.almalaundry.featured.order.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.almalaundry.featured.order.data.repositories.OrderRepository
import com.almalaundry.featured.order.presentation.state.OrderState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val repository: OrderRepository
) : ViewModel() {
    private val _state = MutableStateFlow(OrderState())
    val state = _state.asStateFlow()

    init {
        loadOrders()
    }

    fun loadOrders() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            try {
                val result = repository.getOrders()
                result.onSuccess { response ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        orders = response.data,
                        error = null
                    )
                }.onFailure { exception ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Unknown error occurred"
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error occurred"
                )
            }
        }
    }

//
    fun searchOrders(query: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val result = repository.getOrders(search = query)
                result.onSuccess { response ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        orders = response.data
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun filterOrders(
        status: String? = null,
        type: String? = null,
        startDate: String? = null,
        endDate: String? = null
    ) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val result = repository.getOrders(
                    status = status,
                    type = type,
                    startDate = startDate,
                    endDate = endDate
                )
                result.onSuccess { response ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        orders = response.data
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
}

