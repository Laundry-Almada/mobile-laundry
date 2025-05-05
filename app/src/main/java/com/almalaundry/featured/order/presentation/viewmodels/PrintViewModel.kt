package com.almalaundry.featured.order.presentation.viewmodels

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.almalaundry.featured.order.commons.utils.BluetoothPrinterUtils
import com.almalaundry.featured.order.domain.models.Order
import com.almalaundry.featured.order.presentation.state.PrintScreenState
import com.almalaundry.shared.commons.session.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PrintScreenViewModel(
    private val sessionManager: SessionManager,
    private val bluetoothManager: BluetoothManager
) : ViewModel() {

    private val _state = MutableStateFlow(PrintScreenState())
    val state: StateFlow<PrintScreenState> = _state
    private var bluetoothSocket: BluetoothSocket? = null

    private fun isBluetoothEnabled(): Boolean {
        return bluetoothManager.adapter?.isEnabled == true
    }

    fun checkAndRequestBluetoothPermissions(
        context: Context,
        permissionLauncher: (Array<String>) -> Unit
    ) {
        if (!isBluetoothEnabled()) {
            _state.update { it.copy(promptEnableBluetooth = true) }
            return
        }
        BluetoothPrinterUtils.checkAndRequestBluetoothPermissions(context, permissionLauncher) {
            // Load bonded devices when permissions are granted
            loadBondedDevices(context)
            _state.update { it.copy(showDeviceDialog = true) }
        }
    }

    fun connectToPrinter(context: Context, device: BluetoothDevice) {
        if (!isBluetoothEnabled()) {
            _state.update { it.copy(promptEnableBluetooth = true) }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isConnecting = true) }
            BluetoothPrinterUtils.connectToPrinter(
                context,
                device,
                sessionManager
            ) { socket, isConnected ->
                bluetoothSocket = socket
                _state.update {
                    it.copy(
                        isConnecting = false,
                        isConnected = isConnected,
                        selectedDevice = if (isConnected) device else null
                    )
                }
            }
        }
    }

    fun reconnectToSavedPrinter(context: Context) {
        if (!isBluetoothEnabled()) {
            _state.update { it.copy(promptEnableBluetooth = true) }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isConnecting = true) }
            BluetoothPrinterUtils.reconnectToSavedPrinter(
                context,
                sessionManager,
                bluetoothManager
            ) { socket, device, isConnected ->
                bluetoothSocket = socket
                _state.update {
                    it.copy(
                        isConnecting = false,
                        isConnected = isConnected,
                        selectedDevice = device
                    )
                }
            }
        }
    }

    fun printQRCode(context: Context, order: Order) {
        if (!isBluetoothEnabled()) {
            _state.update { it.copy(promptEnableBluetooth = true) }
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            _state.update { it.copy(isPrinting = true) }
            bluetoothSocket?.outputStream?.let { outputStream ->
                BluetoothPrinterUtils.printQRCode(context, order, outputStream) {
                    viewModelScope.launch {
                        _state.update { it.copy(isPrinting = false) }
                        Toast.makeText(context, "Mencetak...", Toast.LENGTH_SHORT).show()
                    }
                }
            } ?: run {
                viewModelScope.launch {
                    _state.update { it.copy(isPrinting = false) }
                    Toast.makeText(context, "Printer tidak terhubung", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun handlePermissionResult(context: Context, permissions: Map<String, Boolean>) {
        if (permissions.all { it.value }) {
            // Load bonded devices when permissions are granted
            loadBondedDevices(context)
            _state.update { it.copy(showDeviceDialog = true) }
        } else {
            // Handle permission denied (notified in BluetoothPrinterUtils)
        }
    }

    fun handleBluetoothEnableResult(context: Context, resultCode: Int) {
        if (resultCode == android.app.Activity.RESULT_OK) {
            // Bluetooth enabled, proceed with operations
            loadBondedDevices(context)
            _state.update { it.copy(promptEnableBluetooth = false, showDeviceDialog = true) }
        } else {
            // User declined to enable Bluetooth
            Toast.makeText(context, "Hidupkan Bluetooth terlebih dahulu", Toast.LENGTH_SHORT).show()
            _state.update { it.copy(promptEnableBluetooth = false) }
        }
    }

    fun dismissDeviceDialog() {
        _state.update { it.copy(showDeviceDialog = false) }
    }

    private fun loadBondedDevices(context: Context) {
        // Check BLUETOOTH_CONNECT permission
        val hasPermission =
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true // Permission not required for older Android versions
            }

        if (hasPermission && isBluetoothEnabled()) {
            val bondedDevices = bluetoothManager.adapter?.bondedDevices?.toList() ?: emptyList()
            _state.update { it.copy(bondedDevices = bondedDevices) }
        } else {
            _state.update { it.copy(bondedDevices = emptyList()) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        // Close the socket when ViewModel is cleared
        viewModelScope.launch(Dispatchers.IO) {
            try {
                bluetoothSocket?.close()
            } catch (e: Exception) {
                // Ignore closing errors
            }
        }
    }
}