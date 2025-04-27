package com.almalaundry.featured.home.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.almalaundry.featured.home.presentation.state.CustomerDashboardState
import com.almalaundry.featured.order.data.repositories.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CustomerDashboardViewModel @Inject constructor(
    private val repository: OrderRepository
) : ViewModel() {
    private val _state = MutableStateFlow(CustomerDashboardState())
    val state = _state.asStateFlow()

    private var fetchJob: Job? = null

    fun updatePhone(phone: String) {
        _state.value = _state.value.copy(phone = phone)

        if (phone.length >= 10) {
            fetchJob?.cancel() // Cancel fetch sebelumnya

            fetchJob = viewModelScope.launch {
                delay(500) // Tunggu 500ms (half second) sebelum load
                loadOrders(phone)
            }
        }
    }

    fun clearOrders() {
        _state.value = _state.value.copy(
            orders = emptyList(),
            error = null,
            totalOrders = 0,
            hasMoreData = false,
            currentPage = 1
        )
    }

    fun loadOrders(phone: String, isLoadMore: Boolean = false) {
        if (_state.value.isLoading || phone.length < 10) return

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            val page = if (isLoadMore) _state.value.currentPage + 1 else 1
            val result = repository.getCustomerOrders(
                phone = phone,
                perPage = 10,
                page = page
            )

            result.onSuccess { response ->
                val newOrders = if (isLoadMore) {
                    _state.value.orders + response.data
                } else {
                    response.data
                }

                _state.value = _state.value.copy(
                    isLoading = false,
                    orders = newOrders,
                    error = null,
                    totalOrders = response.meta.totalOrders,
                    hasMoreData = response.meta.currentPage < response.meta.totalPages,
                    currentPage = response.meta.currentPage
                )
            }.onFailure { exception ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = exception.message
                )
            }
        }
    }
}