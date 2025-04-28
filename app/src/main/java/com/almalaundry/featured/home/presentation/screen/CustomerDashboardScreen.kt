package com.almalaundry.featured.home.presentation.screen

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.almalaundry.R
import com.almalaundry.featured.home.presentation.components.CustomerOrderCard
import com.almalaundry.featured.home.presentation.viewmodels.CustomerDashboardViewModel
import com.almalaundry.featured.order.presentation.components.shimmer.ShimmerOrderCard
import com.almalaundry.shared.commons.compositional.LocalNavController
import com.almalaundry.shared.presentation.components.BannerHeader
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged

@OptIn(FlowPreview::class)
@Composable
fun CustomerDashboardScreen(
    viewModel: CustomerDashboardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val navController = LocalNavController.current
    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow {
            val layoutInfo = listState.layoutInfo
            val totalItemsNumber = layoutInfo.totalItemsCount
            val lastVisibleItemIndex = (layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0) + 1
            lastVisibleItemIndex > 0 && lastVisibleItemIndex >= totalItemsNumber - 2
        }
            .debounce(300)
            .distinctUntilChanged()
            .collect { shouldLoadMore ->
                if (shouldLoadMore && !state.isLoading && state.hasMoreData && state.phone.length >= 10) {
                    viewModel.loadOrders(state.phone, isLoadMore = true)
                }
            }
    }

    Scaffold(
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.only(WindowInsetsSides.Top)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Banner Header
                BannerHeader(
                    title = "Dashboard Pelanggan",
                    subtitle = "Lacak pesanan Anda",
                    imageResId = R.drawable.header_basic2,
                    onBackClick = { navController.popBackStack() },
                    titleAlignment = Alignment.Start
                )

                // LazyColumn untuk konten
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .offset(y = (-35).dp) // Menutupi sebagian banner
                        .background(Color.Transparent),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    // Input nomor telepon
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
                                modifier = Modifier.padding(16.dp)
                            ) {
                                OutlinedTextField(
                                    value = state.phone,
                                    onValueChange = { viewModel.updatePhone(it) },
                                    label = { Text("Nomor Telepon") },
                                    modifier = Modifier.fillMaxWidth(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                    trailingIcon = {
                                        if (state.phone.isNotBlank()) {
                                            IconButton(onClick = {
                                                viewModel.updatePhone("")
                                                viewModel.clearOrders()
                                            }) {
                                                Icon(
                                                    imageVector = Icons.Default.Clear,
                                                    contentDescription = "Clear"
                                                )
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }

                    // Konten order
                    when {
                        state.isLoading && state.orders.isEmpty() -> {
                            items(3) {
                                ShimmerOrderCard()
                            }
                        }

                        state.error != null -> {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = state.error
                                                ?: "Terjadi kesalahan saat memuat order",
                                            color = MaterialTheme.colorScheme.error,
                                            style = MaterialTheme.typography.bodyMedium,
                                            textAlign = TextAlign.Center
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        TextButton(onClick = { viewModel.loadOrders(state.phone) }) {
                                            Text("Coba Lagi")
                                        }
                                    }
                                }
                            }
                        }

                        state.orders.isEmpty() && state.totalOrders > 0 -> {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Tidak ada order di halaman ini.",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        style = MaterialTheme.typography.bodyMedium,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }

                        state.orders.isEmpty() -> {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Tidak ada order untuk nomor ini.",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        style = MaterialTheme.typography.bodyMedium,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }

                        else -> {
                            items(state.orders) { order ->
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    CustomerOrderCard(order = order)
                                }
                            }

                            // Loading more indicator
                            item {
                                if (state.isLoading) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(24.dp)
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