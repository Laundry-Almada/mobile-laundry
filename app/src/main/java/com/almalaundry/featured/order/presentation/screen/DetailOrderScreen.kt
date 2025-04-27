package com.almalaundry.featured.order.presentation.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
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
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
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
import com.almalaundry.R
import com.almalaundry.featured.order.commons.OrderRoutes
import com.almalaundry.featured.order.presentation.components.StatusChip
import com.almalaundry.featured.order.presentation.viewmodels.DetailOrderViewModel
import com.almalaundry.shared.commons.compositional.LocalNavController
import com.almalaundry.shared.presentation.components.BannerHeader
import com.almalaundry.shared.utils.openWhatsApp
import com.composables.icons.lucide.ChevronDown
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Printer
import com.composables.icons.lucide.Trash
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Brands
import compose.icons.fontawesomeicons.brands.Whatsapp

@OptIn(ExperimentalMaterialApi::class)
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
    val context = LocalContext.current

    val statusList = listOf(
        "pending", "washed", "dried", "ironed", "ready_picked", "completed", "cancelled"
    )
    var expanded by remember { mutableStateOf(false) }
    var selectedStatus by remember(state.order) { mutableStateOf(state.order?.status ?: "pending") }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // State for pull-to-refresh
    val isRefreshing by remember { derivedStateOf { state.isLoading } }
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = { viewModel.loadOrderDetail() }
    )

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Konfirmasi Hapus") },
            text = { Text("Apakah Anda yakin ingin menghapus order ini?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteOrder {
                            Toast.makeText(context, "Order berhasil dihapus", Toast.LENGTH_SHORT)
                                .show()
                            navController.popBackStack()
                        }
                        showDeleteDialog = false
                    }
                ) {
                    Text("Hapus", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
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
                .pullRefresh(pullRefreshState)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                // Banner Header
                BannerHeader(
                    title = "Detail Order",
                    // subtitle = "Informasi detail order",
                    imageResId = R.drawable.header_basic2,
                    onBackClick = { navController.popBackStack() },
                    titleAlignment = Alignment.Start,
                    actionButtons = {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                imageVector = Lucide.Trash,
                                contentDescription = "Delete",
                                tint = Color.Red
                            )
                        }
                    }
                )

                // LazyColumn dengan offset untuk menutupi sebagian banner
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .offset(y = (-40).dp) // Menutupi sebagian banner
                        .background(Color.Transparent),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    when {
                        state.isLoading -> {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }

                        state.error != null -> {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = state.error ?: "Unknown error occurred",
                                        color = Color.Red
                                    )
                                }
                            }
                        }

                        state.order != null -> {
                            item {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surface
                                    ),
                                    elevation = CardDefaults.cardElevation(
                                        defaultElevation = 2.dp
                                    )
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
                                        Text(text = "Layanan: ${state.order?.service?.name}")
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
                                            Surface(
                                                modifier = Modifier
                                                    .clickable { expanded = true }
                                                    .padding(4.dp),
                                                shape = RoundedCornerShape(16.dp)
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier.padding(end = 8.dp)
                                                ) {
                                                    StatusChip(
                                                        status = selectedStatus,
                                                        modifier = Modifier.align(Alignment.CenterVertically)
                                                    )
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
                                                modifier = Modifier.background(MaterialTheme.colorScheme.surface)
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

                                        // WhatsApp Button
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Button(
                                            onClick = {
                                                state.order?.let { order ->
                                                    val statusText = when (order.status) {
                                                        "pending" -> "Menunggu"
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
                                                        - Layanan: ${order.service.name}
                                                        - Berat: ${order.weight} kg
                                                        - Total Harga: Rp ${order.totalPrice}
                                                        - Catatan: ${order.note}
                                                        Terima kasih telah menggunakan Laundry Bersih Jaya!
                                                    """.trimIndent()
                                                    order.customer.phone.let { phone ->
                                                        openWhatsApp(phone, message, context)
                                                    }
                                                }
                                            },
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(0xFF25D366),
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

                                        // Print Barcode Button
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Button(
                                            onClick = {
                                                state.order?.let { order ->
                                                    navController.navigate(
                                                        OrderRoutes.Print(
                                                            barcode = order.barcode,
                                                            customerName = order.customer.name,
                                                            serviceName = order.service.name,
                                                            weight = order.weight,
                                                            totalPrice = order.totalPrice,
                                                            createdAt = order.createdAt
                                                        )
                                                    )
                                                }
                                            },
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(0xFF1976D2)
                                            ),
                                            shape = RoundedCornerShape(8.dp)
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
                                                Text("Cetak QR Code")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Indikator pull-to-refresh
            PullRefreshIndicator(
                refreshing = isRefreshing,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}