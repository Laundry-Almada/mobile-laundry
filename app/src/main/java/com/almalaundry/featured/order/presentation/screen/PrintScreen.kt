package com.almalaundry.featured.order.presentation.screen

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
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.almalaundry.featured.order.commons.barcode.QRCodeUtils
import com.almalaundry.featured.order.domain.models.Customer
import com.almalaundry.featured.order.domain.models.Order
import com.almalaundry.featured.order.domain.models.Service
import com.almalaundry.shared.commons.compositional.LocalNavController
import com.almalaundry.shared.commons.compositional.LocalSessionManager
import com.almalaundry.shared.commons.session.SessionManager
import com.almalaundry.shared.domain.models.Session
import com.almalaundry.shared.utils.formatDateToIndonesian
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Printer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.util.UUID

@Composable
fun PrintScreen(
    barcode: String,
    customerName: String,
    serviceName: String,
    weight: String,
    totalPrice: String,
    createdAt: String,
    navController: NavController = LocalNavController.current,
    sessionManager: SessionManager = LocalSessionManager.current
) {
    val order = Order(
        barcode = barcode,
        customer = Customer(name = customerName),
        service = Service(name = serviceName),
        weight = weight,
        totalPrice = totalPrice,
        createdAt = createdAt
    )
    val context = LocalContext.current
    val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    val bluetoothAdapter = bluetoothManager.adapter
    val scope = rememberCoroutineScope()
    var bluetoothSocket by remember { mutableStateOf<BluetoothSocket?>(null) }
    var selectedDevice by remember { mutableStateOf<BluetoothDevice?>(null) }
    var isConnecting by remember { mutableStateOf(false) }
    var isPrinting by remember { mutableStateOf(false) }
    var showDeviceDialog by remember { mutableStateOf(false) }
    val formattedDate = formatDateToIndonesian(order.createdAt)

    // Launcher untuk meminta izin Bluetooth
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.all { it.value }) {
            showDeviceDialog = true
        } else {
            Toast.makeText(context, "Izin Bluetooth diperlukan", Toast.LENGTH_SHORT).show()
        }
    }

    // Cek dan minta izin Bluetooth
    fun checkAndRequestBluetoothPermissions() {
        val permissions =
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                arrayOf(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN)
            } else {
                arrayOf(Manifest.permission.BLUETOOTH)
            }
        if (permissions.all {
                ContextCompat.checkSelfPermission(
                    context, it
                ) == PackageManager.PERMISSION_GRANTED
            }) {
            showDeviceDialog = true
        } else {
            permissionLauncher.launch(permissions)
        }
    }

    // Fungsi untuk menyambungkan ke printer
    fun connectToPrinter(device: BluetoothDevice) {
        scope.launch(Dispatchers.IO) {
            isConnecting = true
            try {
                val uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB") // UUID SPP
                val socket = device.createRfcommSocketToServiceRecord(uuid)
                socket.connect()
                bluetoothSocket = socket
                selectedDevice = device
                // Simpan alamat printer ke session
                val currentSession = sessionManager.getSession() ?: Session()
                sessionManager.saveSession(currentSession.copy(printerAddress = device.address))
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Terhubung ke ${device.name}", Toast.LENGTH_SHORT)
                        .show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Gagal menyambung: ${e.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            } finally {
                isConnecting = false
            }
        }
    }

    // Fungsi untuk mencoba menyambungkan kembali ke printer yang tersimpan
    fun reconnectToSavedPrinter() {
        scope.launch(Dispatchers.IO) {
            val savedAddress = sessionManager.getPrinterAddress() ?: return@launch
            val savedDevice = bluetoothAdapter.bondedDevices.find { it.address == savedAddress }
            if (savedDevice != null && bluetoothSocket?.isConnected != true) {
                isConnecting = true
                try {
                    val uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
                    val socket = savedDevice.createRfcommSocketToServiceRecord(uuid)
                    socket.connect()
                    bluetoothSocket = socket
                    selectedDevice = savedDevice
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context, "Koneksi ke ${savedDevice.name} dipulihkan", Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: Exception) {
                    bluetoothSocket = null
                    selectedDevice = null
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context, "Gagal memulihkan koneksi: ${e.message}", Toast.LENGTH_SHORT
                        ).show()
                        checkAndRequestBluetoothPermissions()
                    }
                } finally {
                    isConnecting = false
                }
            }
        }
    }

    // Cek koneksi saat pertama kali masuk ke PrintScreen
    LaunchedEffect(Unit) {
        if (bluetoothSocket?.isConnected != true) {
            reconnectToSavedPrinter()
        }
    }

    // Fungsi untuk mengonversi Bitmap ke byte array untuk dicetak
    fun bitmapToBytes(bitmap: Bitmap): ByteArray {
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

    fun createPrintBitmap(order: Order, qrCodeBitmap: Bitmap): Bitmap {
        val qrCodeSize = qrCodeBitmap.width // Ukuran QR code = 160 piksel
        val textPaint = Paint().apply {
            color = android.graphics.Color.BLACK
            textSize = 17f // Ukuran teks kecil agar muat
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)
        }

        // Lebar bitmap = 384 piksel (58 mm pada 203 DPI)
        val bitmapWidth = 384
        val qrX = 1f // Margin kiri untuk QR code
        val textX = qrCodeSize + 5f // Jarak dari QR code
        val maxTextWidth = bitmapWidth - textX - 10f // Lebar maksimum untuk teks
        val lineHeight =
            textPaint.fontMetrics.bottom - textPaint.fontMetrics.top + 1f // Jarak antar baris

        // Daftar teks
        val textLines = listOf(
            order.customer.name,
            "Layanan: ${order.service.name}",
            "Berat: ${order.weight} kg",
            "Harga: Rp${order.totalPrice}",
            "Tgl Order: $formattedDate"
        )

        // Hitung teks yang terpotong dan lanjutkan di bawah
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

        // Hitung tinggi bitmap
        val textHeight = lineHeight * wrappedLines.size
        val bitmapHeight = maxOf(qrCodeSize, textHeight.toInt()) + 20 // Margin bawah kecil

        // Buat bitmap
        val bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(android.graphics.Color.WHITE)

        // Gambar QR code di kiri atas
        canvas.drawBitmap(qrCodeBitmap, qrX, 0f, null)

        // Gambar teks di kanan dan lanjutkan di bawah jika perlu
        var y = lineHeight // Mulai dari atas, sejajar dengan QR code
        wrappedLines.forEach { line ->
            canvas.drawText(line, textX, y, textPaint)
            y += lineHeight
        }

        return bitmap
    }

    // Fungsi untuk mencetak QR code dan detail order
    suspend fun printQRCode(
        outputStream: OutputStream, context: Context, order: Order
    ) {
        try {
            val qrCodeBitmap = QRCodeUtils.generateQRCode(order.barcode, 160)
            if (qrCodeBitmap == null) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Gagal generate QR code", Toast.LENGTH_SHORT).show()
                }
                return
            }

            // Buat bitmap cetakan
            val printBitmap = createPrintBitmap(order, qrCodeBitmap)
            val printBytes = bitmapToBytes(printBitmap)

            withContext(Dispatchers.IO) {
                outputStream.write(byteArrayOf(0x1B, 0x40)) // Inisialisasi printer
                outputStream.write(printBytes) // Cetak bitmap
                outputStream.write(byteArrayOf(0x0A, 0x0A)) // Satu baris kosong untuk margin
                outputStream.write(byteArrayOf(0x1D, 0x56, 0x01)) // Potong kertas
                outputStream.flush()
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Gagal mencetak: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Dialog untuk memilih perangkat Bluetooth
    if (showDeviceDialog) {
        AlertDialog(onDismissRequest = { showDeviceDialog = false },
            title = { Text("Pilih Printer Bluetooth") },
            text = {
                Column {
                    bluetoothAdapter?.bondedDevices?.forEach { device ->
                        TextButton(onClick = {
                            connectToPrinter(device)
                            showDeviceDialog = false
                        }) {
                            Text(device.name ?: "Unknown Device")
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDeviceDialog = false }) {
                    Text("Batal")
                }
            })
    }

    Scaffold(topBar = {
        TopAppBar(title = { Text("Print QR Code") }, navigationIcon = {
            IconButton(onClick = navController::popBackStack) {
                Icon(Lucide.ArrowLeft, "Back")
            }
        })
    }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // Preview bitmap
            val qrCodeBitmap =
                QRCodeUtils.generateQRCode(order.barcode, 160) // Sesuaikan dengan ukuran cetak
            if (qrCodeBitmap != null) {
                val printBitmap = createPrintBitmap(order, qrCodeBitmap)
                Image(
                    bitmap = printBitmap.asImageBitmap(),
                    contentDescription = "Preview Cetakan",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(printBitmap.width.toFloat() / printBitmap.height)
                )
            } else {
                Text(
                    text = "Gagal generate QR code",
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Status koneksi
            Text(
                text = when {
                    isConnecting -> "Menyambungkan..."
                    bluetoothSocket?.isConnected == true -> "Terhubung ke ${selectedDevice?.name}"
                    else -> "Tidak terhubung"
                },
                color = if (bluetoothSocket?.isConnected == true) Color.Green else Color.Red,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Tombol Print
            Button(
                onClick = {
                    if (bluetoothAdapter == null) {
                        Toast.makeText(context, "Bluetooth tidak didukung", Toast.LENGTH_SHORT)
                            .show()
                        return@Button
                    }
                    if (!bluetoothAdapter.isEnabled) {
                        Toast.makeText(
                            context, "Hidupkan Bluetooth terlebih dahulu", Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }
                    if (bluetoothSocket?.isConnected == true) {
                        scope.launch(Dispatchers.IO) {
                            isPrinting = true
                            bluetoothSocket?.outputStream?.let { outputStream ->
                                printQRCode(outputStream, context, order)
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(context, "Mencetak...", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }
                            isPrinting = false
                        }
                    } else {
                        checkAndRequestBluetoothPermissions()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally),
                enabled = !isConnecting && !isPrinting,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Lucide.Printer,
                        contentDescription = "Print",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isPrinting) "Mencetak..." else "Cetak", color = Color.White
                    )
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            // Jangan tutup socket di sini agar tetap terjaga
            // bluetoothSocket?.close()
        }
    }
}