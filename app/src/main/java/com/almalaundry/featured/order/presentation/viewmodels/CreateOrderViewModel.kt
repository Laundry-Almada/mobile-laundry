package com.almalaundry.featured.order.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.almalaundry.featured.order.data.dtos.CreateOrderRequest
import com.almalaundry.featured.order.data.repositories.OrderRepository
import com.almalaundry.featured.order.presentation.state.CreateOrderScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateOrderViewModel @Inject constructor(
    private val repository: OrderRepository
) : ViewModel() {
    private val _state = MutableStateFlow(CreateOrderScreenState())
    val state = _state.asStateFlow()

    fun updatePhone(phone: String) {
        _state.value = _state.value.copy(phone = phone)
    }

    fun updateLaundryId(laundryId: String) {
        _state.value = _state.value.copy(laundryId = laundryId)
    }

    fun updateWeight(weight: String) {
        _state.value = _state.value.copy(weight = weight)
    }

    fun updateTotalPrice(totalPrice: String) {
        _state.value = _state.value.copy(totalPrice = totalPrice)
    }

    fun updateNote(note: String) {
        _state.value = _state.value.copy(note = note)
    }

    fun updateName(name: String) {
        _state.value = _state.value.copy(name = name)
    }

    fun hideNameDialog() {
        _state.value = _state.value.copy(showNameDialog = false)
    }
    
    fun createOrder() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            try {
                // Validasi input dasar
                if (_state.value.phone.isBlank()) {
                    _state.value = _state.value.copy(
                        isLoading = false, error = "Nomor telepon harus diisi"
                    )
                    return@launch
                }

                // Check customer dulu
                repository.checkCustomer(_state.value.phone).onSuccess {
                    // Customer ada, langsung buat order
                    proceedCreateOrder()
                }.onFailure {
                    // Customer tidak ada, tampilkan dialog nama
                    _state.value = _state.value.copy(
                        isLoading = false, showNameDialog = true
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false, error = e.message ?: "Terjadi kesalahan"
                )
            }
        }
    }

    private suspend fun proceedCreateOrder() {
        try {
            val request = CreateOrderRequest(
                phone = _state.value.phone,
                name = _state.value.name.takeIf { it.isNotBlank() },
                laundryId = _state.value.laundryId,
                type = _state.value.type,
                weight = _state.value.weight.toDoubleOrNull() ?: 0.0,
                totalPrice = _state.value.totalPrice.toIntOrNull() ?: 0,
                note = _state.value.note
            )

            repository.createOrder(request).onSuccess {
                _state.value = _state.value.copy(
                    isLoading = false,
                    success = true,
                    error = null,
                    showNameDialog = false,
                    name = ""
                )
            }.onFailure { exception ->
                _state.value = _state.value.copy(
                    isLoading = false, error = exception.message ?: "Unknown error occurred"
                )
            }
        } catch (e: Exception) {
            _state.value = _state.value.copy(
                isLoading = false, error = e.message ?: "Unknown error occurred"
            )
        }
    }

    fun saveCustomerAndCreateOrder() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                proceedCreateOrder() // Langsung buat order tanpa pengecekan customer lagi
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false, error = e.message ?: "Terjadi kesalahan"
                )
            }
        }
    }

}