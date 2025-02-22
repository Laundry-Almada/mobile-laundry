package com.almalaundry.featured.order.presentation.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.almalaundry.featured.order.presentation.components.StatusChip
import com.almalaundry.featured.order.presentation.viewmodels.DetailOrderViewModel
import com.almalaundry.shared.commons.compositional.LocalNavController
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.Lucide

@Composable
fun DetailOrderScreen(
    orderId: String?,
    viewModel: DetailOrderViewModel = hiltViewModel(),
) {
    if (orderId == null) {
        return
    }
    val state by viewModel.state.collectAsState()
    val navController = LocalNavController.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Order") },
                navigationIcon = {
                    IconButton(
                        onClick = navController::popBackStack
                    ) {
                        Icon(Lucide.ArrowLeft, "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            state.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.error ?: "Unknown error occurred",
                        color = Color.Red
                    )
                }
            }

            state.order != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Customer Info
                            Text(
                                text = "Informasi Pelanggan",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Nama: ${state.order?.customer?.name}")
                            Text(text = "Telepon: ${state.order?.customer?.phone}")

                            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                            // Order Info
                            Text(
                                text = "Informasi Order",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Barcode: ${state.order?.barcode}")
                            Text(text = "Tipe: ${state.order?.type}")
                            Text(text = "Berat: ${state.order?.weight} kg")
                            Text(text = "Total: Rp ${state.order?.totalPrice}")

                            if (!state.order?.note.isNullOrEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = "Catatan: ${state.order?.note}")
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            StatusChip(status = state.order?.status ?: "")
                        }
                    }
                }
            }
        }
    }
}
