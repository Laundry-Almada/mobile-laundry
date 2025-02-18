package com.almalaundry.featured.order.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.almalaundry.featured.order.domain.models.Order
import com.almalaundry.featured.order.presentation.state.OrderState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
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
            _state.value = _state.value.copy(isLoading = true)
            // Simulate API call delay
            delay(1000)

            // Dummy data
            val dummyOrders = listOf(
                Order(
                    id = 1,
                    customerName = "Muhammad Idrus",
                    phoneNumber = "081234567890",
                    type = "Express",
                    status = "Dicuci",
                    barcode = "ORD001",
                    weight = 2.5,
                    totalPrice = 50000.0,
                    note = "2 Kemeja, 3 Celana",
                    createdAt = "2024-02-18"
                ),
                Order(
                    id = 2,
                    customerName = "Muhammad Iqbal",
                    phoneNumber = "087654321098",
                    type = "Regular",
                    status = "Disetrika",
                    barcode = "ORD002",
                    weight = 3.0,
                    totalPrice = 45000.0,
                    note = "4 Baju, 2 Celana",
                    createdAt = "2024-02-17"
                )
            )

            _state.value = _state.value.copy(
                isLoading = false,
                orders = dummyOrders
            )
        }
    }
}
