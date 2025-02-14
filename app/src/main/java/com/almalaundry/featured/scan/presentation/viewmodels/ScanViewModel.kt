package com.almalaundry.featured.scan.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.almalaundry.featured.scan.presentation.state.ScanState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScanViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow(ScanState())
    val state = _state.asStateFlow()

    fun onBarcodeDetected(value: String) {
        viewModelScope.launch {
            _state.update { it.copy(barcodeValue = value, isScanning = false) }
        }
    }

    fun startScanning() {
        _state.update { it.copy(isScanning = true) }
    }

    fun stopScanning() {
        _state.update { it.copy(isScanning = false) }
    }

    fun updatePermissionStatus(hasPermission: Boolean) {
        _state.update { it.copy(hasPermission = hasPermission) }
    }

    fun setError(message: String?) {
        _state.update { it.copy(error = message) }
    }
}