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
    internal val mutableState = MutableStateFlow(OrderScreenState())
    val state = mutableState.asStateFlow()

    init {
        loadOrders()
    }

    fun applyFilter(filter: OrderFilter) {
        mutableState.value =
            mutableState.value.copy(filter = filter, currentPage = 1, orders = emptyList())
        loadOrders()
    }

    fun searchOrders(searchQuery: String) {
        val trimmedQuery = searchQuery.trim()
        val filter = if (trimmedQuery.length >= 3) {
            mutableState.value.filter.copy(search = trimmedQuery)
        } else {
            mutableState.value.filter.copy(search = null) // Reset search jika kurang dari 3 huruf
        }
        mutableState.value =
            mutableState.value.copy(filter = filter, currentPage = 1, orders = emptyList())
        loadOrders()
    }

    fun loadOrders(isLoadMore: Boolean = false) {
        viewModelScope.launch {
            if (isLoadMore) {
                if (mutableState.value.currentPage >= mutableState.value.totalPages || !mutableState.value.hasMoreData) {
                    return@launch
                }
                mutableState.value = mutableState.value.copy(isLoadingMore = true)
            } else {
                mutableState.value =
                    mutableState.value.copy(isLoading = true, error = null, orders = emptyList())
            }

            try {
                val filter = mutableState.value.filter
                val result = repository.getOrders(
                    status = if (filter.status.isEmpty()) null else filter.status.joinToString(","),
                    serviceId = filter.serviceId,
                    startDate = filter.startDate,
                    endDate = filter.endDate,
                    search = filter.search,
                    sortBy = filter.sortBy,
                    sortDirection = filter.sortDirection,
                    perPage = mutableState.value.perPage,
                    page = if (isLoadMore) mutableState.value.currentPage + 1 else 1
                )

                result.onSuccess { response ->
                    mutableState.value = mutableState.value.copy(
                        isLoading = false,
                        isLoadingMore = false,
                        orders = if (isLoadMore) mutableState.value.orders + response.data else response.data,
                        totalOrders = response.meta.totalOrders,
                        currentPage = response.meta.currentPage,
                        totalPages = response.meta.totalPages,
                        hasMoreData = response.meta.currentPage < response.meta.totalPages,
                        error = null
                    )
                }.onFailure { exception ->
                    mutableState.value = mutableState.value.copy(
                        isLoading = false,
                        isLoadingMore = false,
                        error = exception.message ?: "Gagal memuat order"
                    )
                }
            } catch (e: Exception) {
                mutableState.value = mutableState.value.copy(
                    isLoading = false,
                    isLoadingMore = false,
                    error = e.message ?: "Gagal memuat order"
                )
            }
        }
    }
}
