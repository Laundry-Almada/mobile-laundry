package com.almalaundry.featured.order.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.almalaundry.featured.order.data.dtos.CreateOrderRequest
import com.almalaundry.featured.order.data.repositories.OrderRepository
import com.almalaundry.featured.order.domain.models.Customer
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
    val mutualState = MutableStateFlow(CreateOrderScreenState())
    val state = mutualState.asStateFlow()

    init {
        viewModelScope.launch {
            val session = sessionManager.getSession()
            if (session != null) {
                mutualState.value = mutualState.value.copy(laundryId = session.laundryId ?: "")
                fetchServices(session.laundryId ?: "")
            } else {
                mutualState.value = mutualState.value.copy(
                    servicesError = "Sesi tidak ditemukan, silakan login kembali"
                )
            }
        }
    }

    fun fetchServices(laundryId: String) {
        viewModelScope.launch {
            mutualState.value =
                mutualState.value.copy(isLoadingServices = true, servicesError = null)
            repository.getServices(laundryId).onSuccess { response ->
                mutualState.value = mutualState.value.copy(
                    isLoadingServices = false,
                    services = response.data,
                    serviceId = response.data.firstOrNull()?.id ?: ""
                )
            }.onFailure { exception ->
                mutualState.value = mutualState.value.copy(
                    isLoadingServices = false,
                    servicesError = exception.message ?: "Gagal memuat layanan"
                )
            }
        }
    }

    fun updateName(name: String) {
        mutualState.value = mutualState.value.copy(name = name)
        // Tidak memanggil searchCustomers di sini
    }

    fun searchCustomers() {
        val query = mutualState.value.name
        if (query.isBlank() || query.length < 2) {
            mutualState.value =
                mutualState.value.copy(customers = emptyList(), isLoadingCustomers = false)
            return
        }
        viewModelScope.launch {
            mutualState.value =
                mutualState.value.copy(isLoadingCustomers = true, customerSearchError = null)
            repository.searchCustomers(query).onSuccess { response ->
                mutualState.value = mutualState.value.copy(
                    isLoadingCustomers = false,
                    customers = response.data,
                    customerSearchError = if (response.data.isEmpty()) "Tidak ada customer ditemukan" else null
                )
            }.onFailure { exception ->
                mutualState.value = mutualState.value.copy(
                    isLoadingCustomers = false,
                    customers = emptyList(),
                    customerSearchError = exception.message ?: "Gagal mencari customer"
                )
            }
        }
    }

    fun selectCustomer(customer: Customer) {
        mutualState.value = mutualState.value.copy(
            name = customer.name,
            phone = if (mutualState.value.hasWhatsApp) customer.phone ?: "" else "",
            username = if (!mutualState.value.hasWhatsApp) customer.username ?: "" else "",
            customers = emptyList() // Kosongkan dropdown setelah memilih
        )
    }

    fun updatePhone(phone: String) {
        mutualState.value = mutualState.value.copy(phone = phone)
    }

    fun updateUsername(username: String) {
        mutualState.value = mutualState.value.copy(username = username)
    }

    fun updateHasWhatsApp(hasWhatsApp: Boolean) {
        mutualState.value = mutualState.value.copy(
            hasWhatsApp = hasWhatsApp,
            phone = if (!hasWhatsApp) "" else mutualState.value.phone,
            username = if (hasWhatsApp) "" else mutualState.value.username
        )
    }

    fun updateServiceId(serviceId: String) {
        mutualState.value = mutualState.value.copy(serviceId = serviceId)
    }

    fun updateWeight(weight: String) {
        mutualState.value = mutualState.value.copy(weight = weight)
    }

    fun updateTotalPrice(totalPrice: String) {
        mutualState.value = mutualState.value.copy(totalPrice = totalPrice)
    }

    fun updateNote(note: String) {
        mutualState.value = mutualState.value.copy(note = note)
    }

    fun createOrder() {
        viewModelScope.launch {
            mutualState.value = mutualState.value.copy(isLoading = true, error = null)

            try {
                // Validasi input
                if (mutualState.value.name.isBlank()) {
                    mutualState.value = mutualState.value.copy(
                        isLoading = false,
                        error = "Nama pelanggan harus diisi"
                    )
                    return@launch
                }
                if (mutualState.value.hasWhatsApp && mutualState.value.phone.isBlank()) {
                    mutualState.value = mutualState.value.copy(
                        isLoading = false,
                        error = "Nomor telepon harus diisi jika menggunakan WhatsApp"
                    )
                    return@launch
                }
                if (mutualState.value.laundryId.isBlank()) {
                    mutualState.value = mutualState.value.copy(
                        isLoading = false,
                        error = "Laundry ID tidak tersedia"
                    )
                    return@launch
                }
                if (mutualState.value.serviceId.isBlank()) {
                    mutualState.value = mutualState.value.copy(
                        isLoading = false,
                        error = "Layanan harus dipilih"
                    )
                    return@launch
                }
                if (mutualState.value.weight.toDoubleOrNull() == null || mutualState.value.weight.toDouble() <= 0) {
                    mutualState.value = mutualState.value.copy(
                        isLoading = false,
                        error = "Berat harus berupa angka positif"
                    )
                    return@launch
                }
                if (mutualState.value.totalPrice.toIntOrNull() == null || mutualState.value.totalPrice.toInt() <= 0) {
                    mutualState.value = mutualState.value.copy(
                        isLoading = false,
                        error = "Total harga harus berupa angka positif"
                    )
                    return@launch
                }

                // Langsung buat order, biarkan backend menangani username
                proceedCreateOrder()
            } catch (e: Exception) {
                mutualState.value = mutualState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Terjadi kesalahan"
                )
            }
        }
    }

    private suspend fun proceedCreateOrder() {
        try {
            val request = CreateOrderRequest(
                name = mutualState.value.name,
                phone = mutualState.value.phone.takeIf { it.isNotBlank() },
                username = mutualState.value.username.takeIf { it.isNotBlank() },
                laundryId = mutualState.value.laundryId,
                serviceId = mutualState.value.serviceId,
                weight = mutualState.value.weight.toDoubleOrNull() ?: 0.0,
                totalPrice = mutualState.value.totalPrice.toIntOrNull() ?: 0,
                note = mutualState.value.note.takeIf { it.isNotBlank() } ?: ""
            )

            repository.createOrder(request).onSuccess {
                mutualState.value = mutualState.value.copy(
                    isLoading = false,
                    success = true,
                    error = null,
                    name = "",
                    phone = "",
                    username = "",
                    customers = emptyList()
                )
            }.onFailure { exception ->
                val errorMessage = if (exception.message?.contains("username") == true) {
                    "Username sudah digunakan, silakan masukkan username lain atau kosongkan untuk otomatis"
                } else {
                    exception.message ?: "Gagal membuat order"
                }
                mutualState.value = mutualState.value.copy(
                    isLoading = false,
                    error = errorMessage
                )
            }
        } catch (e: Exception) {
            mutualState.value = mutualState.value.copy(
                isLoading = false,
                error = e.message ?: "Gagal membuat order"
            )
        }
    }
}