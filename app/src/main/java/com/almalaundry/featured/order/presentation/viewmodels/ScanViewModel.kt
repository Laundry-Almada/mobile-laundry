package com.almalaundry.featured.order.presentation.viewmodels

import androidx.lifecycle.ViewModel
import com.almalaundry.featured.order.data.repositories.OrderRepository
import com.almalaundry.featured.order.domain.models.Order
import com.almalaundry.featured.order.presentation.state.ScanScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ScanViewModel @Inject constructor(
    private val repository: OrderRepository
) : ViewModel() {
    private val _state = MutableStateFlow(ScanScreenState())
    val state = _state.asStateFlow()

    fun setNavigating(isNavigating: Boolean) {
        _state.update { it.copy(isNavigating = isNavigating) }
    }

    suspend fun processBarcodeResult(barcode: String): Result<Order> {
        if (_state.value.isNavigating) {
            return Result.failure(Exception("Navigation in progress"))
        }
        setNavigating(true)
        return repository.getOrderByBarcode(barcode)
    }

    fun onBarcodeDetected(value: String) {
        _state.update {
            it.copy(
                barcodeValue = value,
                isScanning = false,
                error = null,
            )
        }
    }

    fun startScanning() {
        _state.update {
            it.copy(
                isScanning = true, error = null, barcodeValue = ""
            )
        }
    }

    fun updatePermissionStatus(hasPermission: Boolean) {
        _state.update { it.copy(hasPermission = hasPermission) }
    }

    fun setError(message: String?) {
        _state.update { it.copy(error = message, isScanning = false) }
    }

    fun resetState() {
        _state.update {
            ScanScreenState()
        }
    }
}