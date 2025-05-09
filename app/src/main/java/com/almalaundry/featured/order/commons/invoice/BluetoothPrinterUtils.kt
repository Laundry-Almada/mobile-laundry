package com.almalaundry.featured.order.commons.invoice

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.almalaundry.featured.order.domain.models.Order
import com.almalaundry.shared.commons.session.SessionManager
import com.almalaundry.shared.domain.models.Session
import com.almalaundry.shared.utils.formatDateToIndonesian
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.UUID

object BluetoothPrinterUtils {
    // Check and request Bluetooth permissions
    fun checkAndRequestBluetoothPermissions(
        context: Context,
        permissionLauncher: (Array<String>) -> Unit,
        onPermissionsGranted: () -> Unit
    ) {
        val permissions =
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                arrayOf(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN)
            } else {
                arrayOf(Manifest.permission.BLUETOOTH)
            }
        if (permissions.all {
                ContextCompat.checkSelfPermission(
                    context,
                    it
                ) == PackageManager.PERMISSION_GRANTED
            }) {
            onPermissionsGranted()
        } else {
            permissionLauncher(permissions)
        }
    }

    // Connect to a Bluetooth printer
    suspend fun connectToPrinter(
        context: Context,
        device: BluetoothDevice,
        sessionManager: SessionManager,
        onConnectionResult: (BluetoothSocket?, Boolean) -> Unit
    ) {
        var socket: BluetoothSocket? = null
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

        if (!hasPermission) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Izin Bluetooth diperlukan", Toast.LENGTH_SHORT).show()
                onConnectionResult(null, false)
            }
            return
        }

        try {
            val uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB") // UUID SPP
            val socket = device.createRfcommSocketToServiceRecord(uuid)
            socket.connect()
            // Save printer address to session
            val currentSession = sessionManager.getSession() ?: Session()
            sessionManager.saveSession(currentSession.copy(printerAddress = device.address))
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Terhubung ke ${device.name}", Toast.LENGTH_SHORT).show()
                onConnectionResult(socket, true)
            }
        } catch (e: Exception) {
            try {
                socket?.close()
            } catch (closeException: Exception) {
                android.util.Log.e(
                    "BluetoothPrinterUtils",
                    "Error closing socket: ${closeException.message}",
                    closeException
                )
            }
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Gagal menyambung: ${e.message}", Toast.LENGTH_SHORT).show()
                onConnectionResult(null, false)
            }
        } catch (e: SecurityException) {
            try {
                socket?.close()
            } catch (closeException: Exception) {
                android.util.Log.e(
                    "BluetoothPrinterUtils",
                    "Error closing socket: ${closeException.message}",
                    closeException
                )
            }
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    context,
                    "Izin Bluetooth diperlukan: ${e.message}",
                    Toast.LENGTH_SHORT
                )
                    .show()
                onConnectionResult(null, false)
            }
        }
    }

    // Reconnect to a saved printer
    suspend fun reconnectToSavedPrinter(
        context: Context,
        sessionManager: SessionManager,
        bluetoothManager: BluetoothManager,
        onReconnectionResult: (BluetoothSocket?, BluetoothDevice?, Boolean) -> Unit
    ) {
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

        if (!hasPermission) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Izin Bluetooth diperlukan", Toast.LENGTH_SHORT).show()
                onReconnectionResult(null, null, false)
            }
            return
        }

        val savedAddress = sessionManager.getPrinterAddress() ?: run {
            onReconnectionResult(null, null, false)
            return
        }
        val savedDevice = bluetoothManager.adapter.bondedDevices.find { it.address == savedAddress }
        if (savedDevice != null) {
            val socket: BluetoothSocket? = null
            try {
                val uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
                val socket = savedDevice.createRfcommSocketToServiceRecord(uuid)
                socket.connect()
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Koneksi ke ${savedDevice.name} dipulihkan",
                        Toast.LENGTH_SHORT
                    ).show()
                    onReconnectionResult(socket, savedDevice, true)
                }
            } catch (e: Exception) {
                try {
                    socket?.close()
                } catch (closeException: Exception) {
                    android.util.Log.e(
                        "BluetoothPrinterUtils",
                        "Error closing socket: ${closeException.message}",
                        closeException
                    )
                }
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Gagal memulihkan koneksi: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    onReconnectionResult(null, null, false)
                }
            } catch (e: SecurityException) {
                try {
                    socket?.close()
                } catch (closeException: Exception) {
                    android.util.Log.e(
                        "BluetoothPrinterUtils",
                        "Error closing socket: ${closeException.message}",
                        closeException
                    )
                }
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Izin Bluetooth diperlukan: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    onReconnectionResult(null, null, false)
                }
            }
        } else {
            onReconnectionResult(null, null, false)
        }
    }

    // Convert Bitmap to byte array for printing
    private fun bitmapToBytes(bitmap: Bitmap): ByteArray {
        val width = bitmap.width
        val height = bitmap.height
        val bytes = ByteArrayOutputStream()
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        val gs = 0x1D.toByte()
        bytes.write(byteArrayOf(gs, 0x76, 0x30, 0x00)) // GS v 0 - Print raster bit image
        val widthBytes = (width + 7) / 8
        bytes.write(byteArrayOf(widthBytes.toByte(), 0x00))
        bytes.write(byteArrayOf((height % 256).toByte(), (height / 256).toByte()))

        for (y in 0 until height) {
            for (x in 0 until widthBytes) {
                var b = 0
                for (i in 0 until 8) {
                    val xPos = x * 8 + i
                    if (xPos < width) {
                        val pixel = pixels[y * width + xPos]
                        if (pixel == android.graphics.Color.BLACK) {
                            b = b or (0x80 shr i)
                        }
                    }
                }
                bytes.write(b)
            }
        }
        return bytes.toByteArray()
    }

    // Create a Bitmap for printing
    fun createPrintBitmap(order: Order, qrCodeBitmap: Bitmap): Bitmap {
        val qrCodeSize = qrCodeBitmap.width // QR code size = 160 pixels
        val textPaint = Paint().apply {
            color = android.graphics.Color.BLACK
            textSize = 17f // Small text size to fit
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)
        }

        val titlePaint = Paint().apply {
            color = android.graphics.Color.BLACK
            textSize = 20f // Larger text size for laundry name
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
        }

        // Bitmap width = 384 pixels (58 mm at 203 DPI)
        val bitmapWidth = 384
        val leftColumnWidth = qrCodeSize + 10f // Left column for QR code with margin
        val textX = leftColumnWidth + 5f // Start of right column for text
        val maxTextWidth = bitmapWidth - textX - 10f // Max width for text
        val lineHeight =
            textPaint.fontMetrics.bottom - textPaint.fontMetrics.top + 1f // Line spacing

        // Text lines for details
        val formattedDate = formatDateToIndonesian(order.createdAt)
        val textLines = listOf(
            order.customer.name,
            if (!order.customer.phone.isNullOrEmpty()) {
                "${order.customer.phone}"
            } else {
                "Username: ${order.customer.username ?: "Unknown"}"
            },
            "Layanan: ${order.service.name}",
            "Berat: ${order.weight} kg",
            "Harga: Rp${order.totalPrice}",
            "Tgl Order: $formattedDate"
        )

        // Wrap text lines that exceed maxTextWidth
        val wrappedLines = mutableListOf<String>()
        textLines.forEach { line ->
            if (textPaint.measureText(line) <= maxTextWidth) {
                wrappedLines.add(line)
            } else {
                val words = line.split(" ")
                var currentLine = ""
                words.forEach { word ->
                    val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
                    if (textPaint.measureText(testLine) <= maxTextWidth) {
                        currentLine = testLine
                    } else {
                        if (currentLine.isNotEmpty()) wrappedLines.add(currentLine)
                        currentLine = word
                    }
                }
                if (currentLine.isNotEmpty()) wrappedLines.add(currentLine)
            }
        }

        // Calculate heights
        val titleLineHeight = titlePaint.fontMetrics.bottom - titlePaint.fontMetrics.top + 1f
        val textHeight = lineHeight * wrappedLines.size
        val tearOffSpace = 40 // Extra space at the bottom for tear-off
        // Bitmap height accommodates QR code, text, title, margins, and tear-off space
        val bitmapHeight = maxOf(
            qrCodeSize + titleLineHeight.toInt() + 20, // QR code height + title + margins
            textHeight.toInt() + titleLineHeight.toInt() + 20 // Text height + title + margins
        ) + tearOffSpace // Add tear-off space

        // Create bitmap
        val bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(android.graphics.Color.WHITE)

        // Draw laundry name at the top center
        val laundryNameWidth = titlePaint.measureText(order.laundry.name)
        val laundryNameX = (bitmapWidth - laundryNameWidth) / 2
        canvas.drawText(order.laundry.name, laundryNameX, titleLineHeight, titlePaint)

        // Draw QR code at the bottom of the left column, above tear-off space
        val qrX = 5f // Left margin
        val qrY =
            bitmapHeight - qrCodeSize - 5f - tearOffSpace // Bottom-aligned above tear-off space
        canvas.drawBitmap(qrCodeBitmap, qrX, qrY, null)

        // Draw text details in the right column, starting below laundry name
        var y = titleLineHeight + lineHeight + 5f
        wrappedLines.forEach { line ->
            canvas.drawText(line, textX, y, textPaint)
            y += lineHeight
        }

        return bitmap
    }

    // Print QR code and order details
    suspend fun printQRCode(
        context: Context,
        order: Order,
        outputStream: OutputStream,
        onPrintComplete: () -> Unit
    ) {
        try {
            val qrCodeBitmap = QRCodeUtils.generateQRCode(order.barcode, 160)
            if (qrCodeBitmap == null) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Gagal generate QR code", Toast.LENGTH_SHORT).show()
                }
                return
            }

            // Create print bitmap
            val printBitmap = createPrintBitmap(order, qrCodeBitmap)
            val printBytes = bitmapToBytes(printBitmap)

            withContext(Dispatchers.IO) {
                outputStream.write(byteArrayOf(0x1B, 0x40)) // Initialize printer
                outputStream.write(printBytes) // Print bitmap
                outputStream.write(byteArrayOf(0x0A, 0x0A)) // One blank line for margin
                outputStream.write(byteArrayOf(0x1D, 0x56, 0x01)) // Cut paper
                outputStream.flush()
            }
            withContext(Dispatchers.Main) {
                onPrintComplete()
            }
        } catch (e: IOException) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Gagal mencetak: ${e.message}", Toast.LENGTH_SHORT).show()
                onPrintComplete()
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Gagal mencetak: ${e.message}", Toast.LENGTH_SHORT).show()
                onPrintComplete()
            }
        }
    }
}