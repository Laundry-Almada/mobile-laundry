package com.almalaundry.featured.order.presentation.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.almalaundry.featured.order.data.repositories.OrderRepository
import com.almalaundry.featured.order.domain.models.Service
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ServiceViewModel @Inject constructor(
    private val repository: OrderRepository
) : ViewModel() {
    var services = mutableStateOf<List<Service>>(emptyList())
        private set

    var isLoading = mutableStateOf(false)
        private set

    var errorMessage = mutableStateOf<String?>(null)
        private set

    fun fetchServices(laundryId: String) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val result = repository.getServices(laundryId)
                result.onSuccess { response ->
                    if (response.success) {
                        services.value = response.data
                        errorMessage.value = null
                    } else {
                        errorMessage.value = "Failed to load services"
                    }
                }.onFailure { exception ->
                    errorMessage.value = exception.message ?: "An error occurred"
                }
            } catch (e: Exception) {
                errorMessage.value = e.message ?: "An error occurred"
            } finally {
                isLoading.value = false
            }
        }
    }
}