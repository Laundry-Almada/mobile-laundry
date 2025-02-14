package com.almalaundry.featured.scan.presentation.screen

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.almalaundry.featured.scan.presentation.viewmodels.ScanViewModel
import com.almalaundry.shared.commons.compositional.LocalNavController

@Composable
fun ScanScreen(
    viewModel: ScanViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    LocalContext.current
    val navController = LocalNavController.current

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        navController.popBackStack()
        viewModel.updatePermissionStatus(isGranted)
        if (isGranted) {
            viewModel.startScanning()
        }
    }

    LaunchedEffect(Unit) {
        launcher.launch(Manifest.permission.CAMERA)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (state.hasPermission && state.isScanning) {
            BarcodeScanner(
                onBarcodeDetected = { barcode ->
                    viewModel.onBarcodeDetected(barcode)
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        // Tampilkan hasil scanscan
        if (state.barcodeValue.isNotEmpty()) {
            Text(
                text = "Barcode: ${state.barcodeValue}",
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            )
        }

        state.error?.let { error ->
            Text(
                text = error,
                color = Color.Red,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(
                        16.dp
                    )
            )
        }
    }
}
