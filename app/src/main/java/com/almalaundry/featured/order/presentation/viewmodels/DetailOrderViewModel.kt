package com.almalaundry.featured.order.presentation.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.almalaundry.featured.order.data.repositories.OrderRepository
import com.almalaundry.featured.order.presentation.state.DetailOrderScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailOrderViewModel @Inject constructor(
    private val repository: OrderRepository, savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _state = MutableStateFlow(DetailOrderScreenState())
    val state = _state.asStateFlow()

    private val orderId: String = savedStateHandle.get<String>("orderId")
        ?: throw IllegalArgumentException("Order ID is required")

    init {
        loadOrderDetail()
    }

    fun loadOrderDetail() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            try {
                repository.getOrderDetail(orderId).onSuccess { order ->
                    _state.value = _state.value.copy(
                        isLoading = false, order = order, error = null
                    )
                }.onFailure { exception ->
                    _state.value = _state.value.copy(
                        isLoading = false, error = exception.message
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false, error = e.message
                )
            }
        }
    }
}
