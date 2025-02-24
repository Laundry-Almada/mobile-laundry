package com.almalaundry.featured.order.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.almalaundry.featured.order.data.repositories.OrderRepository
import com.almalaundry.featured.order.presentation.state.HistoryOrderScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryOrderViewModel @Inject constructor(
    private val repository: OrderRepository
) : ViewModel() {
    private val _state = MutableStateFlow(HistoryOrderScreenState())
    val state = _state.asStateFlow()

    init {
        loadHistories()
    }

    fun loadHistories(isLoadMore: Boolean = false) {
        viewModelScope.launch {
            if (isLoadMore) {
                if (_state.value.currentPage >= _state.value.totalPages) return@launch
                _state.value = _state.value.copy(isLoadingMore = true)
            } else {
                _state.value = _state.value.copy(isLoading = true)
            }

            try {
                val result = repository.getOrders(
                    page = if (isLoadMore) _state.value.currentPage + 1 else 1,
                    status = "completed,cancelled",
                    sortBy = "created_at",
                    sortDirection = "desc"
                )

                result.onSuccess { response ->
//                    Log.d("HistoryViewModel", "Received ${response.data.size} histories")
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isLoadingMore = false,
                        histories = if (isLoadMore) _state.value.histories + response.data
                        else response.data,
                        totalHistories = response.meta.totalOrders,
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