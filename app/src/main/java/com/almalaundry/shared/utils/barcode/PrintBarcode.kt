package com.almalaundry.shared.utils.barcode

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import java.io.OutputStream
import java.util.UUID

fun printBarcode(context: Context, barcodeBitmap: Bitmap, deviceAddress: String) {
    val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    if (bluetoothAdapter == null) {
        Toast.makeText(context, "Bluetooth tidak didukung", Toast.LENGTH_SHORT).show()
        return
    }

    if (!bluetoothAdapter.isEnabled) {
        Toast.makeText(context, "Hidupkan Bluetooth terlebih dahulu", Toast.LENGTH_SHORT).show()
        return
    }

    val device: BluetoothDevice? = bluetoothAdapter.getRemoteDevice(deviceAddress)
    if (device == null) {
        Toast.makeText(context, "Perangkat printer tidak ditemukan", Toast.LENGTH_SHORT).show()
        return
    }

    try {
        val uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB") // SPP UUID
        val socket: BluetoothSocket = device.createRfcommSocketToServiceRecord(uuid)
        socket.connect()

        val outputStream: OutputStream = socket.outputStream
        val printData = bitmapToBytes(barcodeBitmap)

        outputStream.write(byteArrayOf(0x1B, 0x40)) // Inisialisasi printer
        outputStream.write(printData) // Kirim data barcode
        outputStream.write(byteArrayOf(0x0A)) // Baris baru
        outputStream.write(byteArrayOf(0x1D, 0x56, 0x00)) // Potong kertas (opsional)

        outputStream.flush()
        outputStream.close()
        socket.close()
        Toast.makeText(context, "Mencetak barcode...", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Gagal mencetak: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}