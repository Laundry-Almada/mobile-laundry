package com.almalaundry.featured.order.presentation.state

import android.bluetooth.BluetoothDevice
import android.graphics.Bitmap

data class PrintScreenState(
    val isConnecting: Boolean = false,
    val isPrinting: Boolean = false,
    val isConnected: Boolean = false,
    val selectedDevice: BluetoothDevice? = null,
    val showDeviceDialog: Boolean = false,
    val bondedDevices: List<BluetoothDevice> = emptyList(),
    val promptEnableBluetooth: Boolean = false,
    val connectionError: String? = null,
    val screenshotBitmap: Bitmap? = null
)