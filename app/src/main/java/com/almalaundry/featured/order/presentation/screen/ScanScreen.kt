package com.almalaundry.featured.order.presentation.screen

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.almalaundry.featured.order.commons.OrderRoutes
import com.almalaundry.featured.order.commons.invoice.extractOrderId
import com.almalaundry.featured.order.presentation.components.ScanOverlay
import com.almalaundry.featured.order.presentation.viewmodels.ScanViewModel
import com.almalaundry.shared.commons.compositional.LocalNavController
import com.google.zxing.BarcodeFormat
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.BarcodeView
import com.journeyapps.barcodescanner.DefaultDecoderFactory
import kotlinx.coroutines.launch

@Composable
fun ScanScreen(
    viewModel: ScanViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val navController = LocalNavController.current
    val state by viewModel.state.collectAsState()
    val scope = rememberCoroutineScope()

    var isProcessingBarcode by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.updatePermissionStatus(isGranted)
        if (isGranted) {
            viewModel.startScanning()
        } else {
            viewModel.setError("Camera permission required")
            navController.popBackStack()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.resetState()
        if (!state.isScanning && ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            viewModel.updatePermissionStatus(true)
            viewModel.startScanning()
        } else if (!state.hasPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.resetState()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (state.hasPermission && state.isScanning) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    BarcodeView(ctx).apply {
                        // Konfigurasi format barcode (hanya QR code)
                        val formats = listOf(BarcodeFormat.QR_CODE)
                        decoderFactory = DefaultDecoderFactory(formats)
                        cameraSettings.isAutoFocusEnabled = true
                        cameraSettings.requestedCameraId = -1 // Kamera belakang

                        // Callback untuk hasil pemindaian
                        decodeContinuous(object : BarcodeCallback {
                            override fun barcodeResult(result: BarcodeResult) {
                                if (!isProcessingBarcode && !state.isNavigating) {
                                    isProcessingBarcode = true
                                    viewModel.onBarcodeDetected(result.text)

                                    val orderId = extractOrderId(result.text)
                                    if (orderId != null) {
                                        scope.launch {
                                            viewModel.processBarcodeResult(orderId).fold(
                                                onSuccess = { order ->
                                                    navController.navigate(
                                                        OrderRoutes.Detail(order.id)
                                                    ) {
                                                        launchSingleTop = true
                                                    }
                                                },
                                                onFailure = { error ->
                                                    viewModel.setError("Order tidak ditemukan: ${error.message}")
                                                }
                                            )
                                            isProcessingBarcode = false
                                            viewModel.setNavigating(false)
                                        }
                                    } else {
                                        viewModel.setError("Invalid QR code format")
                                        isProcessingBarcode = false
                                        viewModel.setNavigating(false)
                                    }
                                }
                            }

                            override fun possibleResultPoints(resultPoints: List<ResultPoint>) {
                                // Tidak digunakan untuk saat ini
                            }
                        })
                    }
                },
                update = { barcodeView ->
                    if (state.isScanning) {
                        barcodeView.resume()
                    } else {
                        barcodeView.pause()
                    }
                }
            )

            // Overlay dengan kotak transparan dan sudut-sudut
            ScanOverlay(cornerColor = MaterialTheme.colorScheme.primary)
        }

        // Tampilkan error jika ada
        if (state.error != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = state.error!!,
                    color = Color.Red,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}