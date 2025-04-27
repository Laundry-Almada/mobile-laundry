package com.almalaundry.featured.order.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.almalaundry.featured.order.data.dtos.CreateOrderRequest
import com.almalaundry.featured.order.data.repositories.OrderRepository
import com.almalaundry.featured.order.presentation.state.CreateOrderScreenState
import com.almalaundry.shared.commons.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateOrderViewModel @Inject constructor(
    private val repository: OrderRepository,
    private val sessionManager: SessionManager
) : ViewModel() {
    private val _state = MutableStateFlow(CreateOrderScreenState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val session = sessionManager.getSession()
            if (session != null) {
                _state.value = _state.value.copy(laundryId = session.laundryId ?: "")
                fetchServices(session.laundryId ?: "")
            } else {
                _state.value = _state.value.copy(
                    servicesError = "Sesi tidak ditemukan, silakan login kembali"
                )
            }
        }
    }

    private fun fetchServices(laundryId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoadingServices = true, servicesError = null)
            repository.getServices(laundryId).onSuccess { response ->
                _state.value = _state.value.copy(
                    isLoadingServices = false,
                    services = response.data,
                    serviceId = response.data.firstOrNull()?.id ?: ""
                )
            }.onFailure { exception ->
                _state.value = _state.value.copy(
                    isLoadingServices = false,
                    servicesError = exception.message ?: "Gagal memuat layanan"
                )
            }
        }
    }

    fun updatePhone(phone: String) {
        _state.value = _state.value.copy(phone = phone)
    }

    fun updateServiceId(serviceId: String) {
        _state.value = _state.value.copy(serviceId = serviceId)
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
            _state.value = _state.value.copy(isLoading = true, error = null)

            try {
                if (_state.value.phone.isBlank()) {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = "Nomor telepon harus diisi"
                    )
                    return@launch
                }
                if (_state.value.laundryId.isBlank()) {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = "Laundry ID tidak tersedia"
                    )
                    return@launch
                }
                if (_state.value.serviceId.isBlank()) {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = "Layanan harus dipilih"
                    )
                    return@launch
                }
                if (_state.value.weight.toDoubleOrNull() == null || _state.value.weight.toDouble() <= 0) {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = "Berat harus berupa angka positif"
                    )
                    return@launch
                }
                if (_state.value.totalPrice.toIntOrNull() == null || _state.value.totalPrice.toInt() <= 0) {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = "Total harga harus berupa angka positif"
                    )
                    return@launch
                }

                repository.checkCustomer(_state.value.phone).onSuccess {
                    proceedCreateOrder()
                }.onFailure {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        showNameDialog = true
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Terjadi kesalahan"
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
                serviceId = _state.value.serviceId,
                weight = _state.value.weight.toDoubleOrNull() ?: 0.0,
                totalPrice = _state.value.totalPrice.toIntOrNull() ?: 0,
                note = _state.value.note.takeIf { it.isNotBlank() } ?: ""
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
                    isLoading = false,
                    error = exception.message ?: "Gagal membuat order"
                )
            }
        } catch (e: Exception) {
            _state.value = _state.value.copy(
                isLoading = false,
                error = e.message ?: "Gagal membuat order"
            )
        }
    }

    fun saveCustomerAndCreateOrder() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                if (_state.value.name.isBlank()) {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = "Nama pelanggan harus diisi"
                    )
                    return@launch
                }
                proceedCreateOrder()
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Gagal membuat order"
                )
            }
        }
    }
}