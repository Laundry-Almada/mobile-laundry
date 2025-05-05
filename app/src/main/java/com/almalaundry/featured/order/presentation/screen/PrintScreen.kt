package com.almalaundry.featured.order.presentation.screen

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.almalaundry.R
import com.almalaundry.featured.order.commons.barcode.QRCodeUtils
import com.almalaundry.featured.order.commons.utils.BluetoothPrinterUtils
import com.almalaundry.featured.order.domain.models.Customer
import com.almalaundry.featured.order.domain.models.Laundry
import com.almalaundry.featured.order.domain.models.Order
import com.almalaundry.featured.order.domain.models.Service
import com.almalaundry.featured.order.presentation.viewmodels.PrintScreenViewModel
import com.almalaundry.shared.commons.compositional.LocalNavController
import com.almalaundry.shared.commons.compositional.LocalSessionManager
import com.almalaundry.shared.presentation.components.BannerHeader
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Printer

@Composable
fun PrintScreen(
    barcode: String,
    customerName: String,
    customerPhone: String? = null,
    customerUsername: String? = null,
    serviceName: String,
    laundryName: String,
    weight: String,
    totalPrice: String,
    createdAt: String,
    navController: NavController = LocalNavController.current
) {
    val context = LocalContext.current
    val sessionManager = LocalSessionManager.current
    val viewModel: PrintScreenViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(PrintScreenViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return PrintScreenViewModel(
                        sessionManager = sessionManager,
                        bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
                    ) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    )
    val state by viewModel.state.collectAsState()
    val order = Order(
        barcode = barcode,
        customer = Customer(
            name = customerName,
            phone = customerPhone,
            username = customerUsername
        ),
        service = Service(name = serviceName),
        laundry = Laundry(name = laundryName),
        weight = weight,
        totalPrice = totalPrice,
        createdAt = createdAt
    )
//    val formattedDate = formatDateToIndonesian(order.createdAt)

    // Launcher for requesting Bluetooth permissions
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        viewModel.handlePermissionResult(context, permissions)
    }

    // Launcher for enabling Bluetooth
    val enableBluetoothLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        viewModel.handleBluetoothEnableResult(context, result.resultCode)
    }

    // Launch Bluetooth enable request when prompted
    LaunchedEffect(state.promptEnableBluetooth) {
        if (state.promptEnableBluetooth) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            enableBluetoothLauncher.launch(enableBtIntent)
        }
    }

    // Check permissions on start
    LaunchedEffect(Unit) {
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
            viewModel.reconnectToSavedPrinter(context)
        } else {
            viewModel.checkAndRequestBluetoothPermissions(context, permissionLauncher::launch)
        }
    }

    // Device selection dialog
    if (state.showDeviceDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissDeviceDialog() },
            title = { Text("Pilih Printer Bluetooth") },
            text = {
                Column {
                    state.bondedDevices.forEach { device ->
                        TextButton(onClick = {
                            viewModel.connectToPrinter(context, device)
                            viewModel.dismissDeviceDialog()
                        }) {
                            Text(device.name ?: "Unknown Device")
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.dismissDeviceDialog() }) {
                    Text("Batal")
                }
            }
        )
    }

    Scaffold(
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.only(WindowInsetsSides.Top)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Banner Header
                BannerHeader(
                    title = "Print QR Code",
                    subtitle = "Cetak QR code untuk order",
                    imageResId = R.drawable.header_basic2,
                    onBackClick = { navController.popBackStack() },
                    titleAlignment = Alignment.Start
                )

                // LazyColumn for content
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .offset(y = (-40).dp)
                        .background(Color.Transparent),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.Start
                            ) {
                                // Preview bitmap
                                val qrCodeBitmap = QRCodeUtils.generateQRCode(order.barcode, 160)
                                if (qrCodeBitmap != null) {
                                    val printBitmap =
                                        BluetoothPrinterUtils.createPrintBitmap(order, qrCodeBitmap)
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

                                // Connection status
                                Text(
                                    text = when {
                                        state.isConnecting -> "Menyambungkan..."
                                        state.isConnected -> "Terhubung ke ${state.selectedDevice?.name}"
                                        else -> "Tidak terhubung"
                                    },
                                    color = if (state.isConnected) Color.Green else Color.Red,
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // Print Button
                                Button(
                                    onClick = {
                                        if (state.isConnected) {
                                            viewModel.printQRCode(context, order)
                                        } else {
                                            viewModel.checkAndRequestBluetoothPermissions(
                                                context,
                                                permissionLauncher::launch
                                            )
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.CenterHorizontally),
                                    enabled = !state.isConnecting && !state.isPrinting,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary
                                    )
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
                                            text = if (state.isPrinting) "Mencetak..." else "Cetak",
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}