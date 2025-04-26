package com.almalaundry.featured.order.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.almalaundry.featured.order.data.repositories.OrderRepository
import com.almalaundry.featured.order.domain.models.OrderFilter
import com.almalaundry.featured.order.presentation.state.OrderScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val repository: OrderRepository
) : ViewModel() {
    private val _state = MutableStateFlow(OrderScreenState())
    val state = _state.asStateFlow()

    init {
        loadOrders()
    }

    fun applyFilter(filter: OrderFilter) {
        _state.value = _state.value.copy(filter = filter)
        loadOrders()
    }

    fun loadOrders(isLoadMore: Boolean = false) {
        viewModelScope.launch {
            if (isLoadMore) {
                if (_state.value.currentPage >= _state.value.totalPages) return@launch
                _state.value = _state.value.copy(isLoadingMore = true)
            } else {
                _state.value = _state.value.copy(isLoading = true)
            }

            try {
                val filter = _state.value.filter
                val result = repository.getOrders(
                    status = if (filter.status.isEmpty()) null else filter.status.joinToString(","),
                    serviceId = filter.serviceId,
                    startDate = filter.startDate,
                    endDate = filter.endDate,
                    search = filter.search,
                    sortBy = filter.sortBy,
                    sortDirection = filter.sortDirection,
                    perPage = state.value.perPage,
                    page = if (isLoadMore) _state.value.currentPage + 1 else 196
                )

                result.onSuccess { response ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isLoadingMore = false,
                        orders = if (isLoadMore) _state.value.orders + response.data else response.data,
                        totalOrders = response.meta.totalOrders,
                        currentPage = response.meta.currentPage,
                        totalPages = response.meta.totalPages,
                        error = null
                    )
                }.onFailure { exception ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isLoadingMore = false,
                        error = exception.message ?: "Unknown error occurred"
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    isLoadingMore = false,
                    error = e.message ?: "Unknown error occurred"
                )
            }
        }
    }
}

