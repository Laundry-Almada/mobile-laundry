package com.almalaundry.featured.order.presentation.state

data class ScanScreenState(
    val barcodeValue: String = "",
    val isScanning: Boolean = false,
    val hasPermission: Boolean = false,
    val error: String? = null,
    val isNavigating: Boolean = false
)

