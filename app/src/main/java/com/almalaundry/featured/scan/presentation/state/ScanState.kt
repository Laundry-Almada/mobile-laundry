package com.almalaundry.featured.scan.presentation.state

data class ScanState(
    val isLoading: Boolean = false,
    val isScanning: Boolean = false,
    val isScanned: Boolean = false,
    val isScanFailed: Boolean = false,
    val isScanSuccess: Boolean = false,
    val isScanError: Boolean = false,
    val isScanCancelled: Boolean = false,
    val isScanCompleted: Boolean = false,
    val isScanCompletedWithErrors: Boolean = false,
    val isScanCompletedWithWarnings: Boolean = false,
    val isScanCompletedWithInfo: Boolean = false,
    val isScanCompletedWithSuccess: Boolean = false,
    val isScanCompletedWithFailure: Boolean = false,
    val isScanCompletedWithCancelled: Boolean = false,
    val isScanCompletedWithUnknown: Boolean = false,
    val isScanCompletedWithUnknownError: Boolean = false,
)
