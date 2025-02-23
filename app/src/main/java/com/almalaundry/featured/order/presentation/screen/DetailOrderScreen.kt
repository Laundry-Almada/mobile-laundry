package com.almalaundry.featured.order.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.almalaundry.featured.order.presentation.components.StatusChip
import com.almalaundry.featured.order.presentation.viewmodels.DetailOrderViewModel
import com.almalaundry.shared.commons.compositional.LocalNavController
import com.almalaundry.shared.utils.openWhatsApp
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.ChevronDown
import com.composables.icons.lucide.Lucide
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Brands
import compose.icons.fontawesomeicons.brands.Whatsapp

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
    val context = LocalContext.current // Untuk intent WhatsApp

    val statusList = listOf(
        "pending", "washed", "dried", "ironed", "ready_picked", "completed", "cancelled"
    )
    var expanded by remember { mutableStateOf(false) }
    var selectedStatus by remember(state.order) {
        mutableStateOf(state.order?.status ?: "pending")
    }

    Scaffold(topBar = {
        TopAppBar(title = { Text("Detail Order") }, navigationIcon = {
            IconButton(onClick = navController::popBackStack) {
                Icon(Lucide.ArrowLeft, "Back")
            }
        })
    }) { paddingValues ->
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
                        text = state.error ?: "Unknown error occurred", color = Color.Red
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
                            Text(text = "Telepon: 0${state.order?.customer?.phone}")

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

                            // Status Section
                            Text(
                                text = "Status:",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Box {
                                Surface(modifier = Modifier
                                    .clickable { expanded = true }
                                    .padding(4.dp),
                                    shape = RoundedCornerShape(16.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(end = 8.dp)
                                    ) {
                                        StatusChip(status = selectedStatus)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Icon(
                                            imageVector = Lucide.ChevronDown,
                                            contentDescription = "Dropdown",
                                            tint = Color.Gray
                                        )
                                    }
                                }
                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false },
                                    modifier = Modifier.background(Color.White)
                                ) {
                                    statusList.forEach { status ->
                                        DropdownMenuItem(
                                            text = { StatusChip(status = status) },
                                            onClick = {
                                                selectedStatus = status
                                                viewModel.updateStatus(status)
                                                expanded = false
                                            },
                                            modifier = Modifier.padding(vertical = 4.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    state.order?.let { order ->
                                        val statusText = when (order.status) {
                                            "washed" -> "Dicuci"
                                            "dried" -> "Dikeringkan"
                                            "ironed" -> "Disetrika"
                                            "ready_picked" -> "Siap Diambil"
                                            "completed" -> "Selesai"
                                            "cancelled" -> "Dibatalkan"
                                            else -> "Menunggu"
                                        }
                                        val message = """
                                            Halo ${order.customer.name},
                                            Status pesanan laundry Anda saat ini: *$statusText*
                                            
                                            Detail Pesanan:
                                            - Barcode: ${order.barcode}
                                            - Tipe: ${order.type}
                                            - Berat: ${order.weight} kg
                                            - Total Harga: Rp ${order.totalPrice}
                                            - Catatan: ${order.note ?: "-"}
                                            
                                            Terima kasih telah menggunakan Laundry Bersih Jaya!
                                        """.trimIndent()
                                        order.customer.phone?.let { phone ->
                                            openWhatsApp(phone, message, context)
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF25D366), // Warna hijau WhatsApp
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = FontAwesomeIcons.Brands.Whatsapp,
                                        contentDescription = "WhatsApp",
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Kirim Pesan WhatsApp")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
