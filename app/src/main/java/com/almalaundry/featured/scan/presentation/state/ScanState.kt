package com.almalaundry.featured.scan.presentation.state

data class ScanState(
    val barcodeValue: String = "",
    val isScanning: Boolean = false,
    val hasPermission: Boolean = false,
    val error: String? = null
)