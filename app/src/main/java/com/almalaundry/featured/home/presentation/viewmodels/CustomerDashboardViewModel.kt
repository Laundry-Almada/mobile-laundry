package com.almalaundry.featured.home.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.almalaundry.featured.home.presentation.state.CustomerDashboardState
import com.almalaundry.featured.order.data.repositories.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CustomerDashboardViewModel @Inject constructor(
    private val repository: OrderRepository
) : ViewModel() {
    internal val mutableState = MutableStateFlow(CustomerDashboardState())
    val state = mutableState.asStateFlow()

    fun updateIdentifier(identifier: String) {
        mutableState.value = mutableState.value.copy(identifier = identifier)
    }

    fun clearOrders() {
        mutableState.value = mutableState.value.copy(
            orders = emptyList(),
            error = null,
            totalOrders = 0,
            hasMoreData = false,
            currentPage = 1
        )
    }

    fun loadOrders(identifier: String, isLoadMore: Boolean = false) {
        if (mutableState.value.isLoading || identifier.length < 3) return

        viewModelScope.launch {
            mutableState.value = mutableState.value.copy(isLoading = true)

            val page = if (isLoadMore) mutableState.value.currentPage + 1 else 1
            val result = repository.getCustomerOrders(
                identifier = identifier,
                perPage = 10,
                page = page
            )

            result.onSuccess { response ->
                // Filter order dengan data lengkap
                val filteredOrders = response.data.filter { order ->
                    order.customer.name.isNotBlank() &&
                            (order.customer.phone != null || order.customer.username != null) &&
                            order.laundry.phone != null
                }
                val newOrders = if (isLoadMore) {
                    mutableState.value.orders + filteredOrders
                } else {
                    filteredOrders
                }

                mutableState.value = mutableState.value.copy(
                    isLoading = false,
                    orders = newOrders,
                    error = null,
                    totalOrders = response.meta.totalOrders,
                    hasMoreData = response.meta.currentPage < response.meta.totalPages,
                    currentPage = response.meta.currentPage
                )
            }.onFailure { exception ->
                mutableState.value = mutableState.value.copy(
                    isLoading = false,
                    error = exception.message
                )
            }
        }
    }
}