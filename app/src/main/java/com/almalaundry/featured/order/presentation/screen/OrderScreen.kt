package com.almalaundry.featured.order.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.almalaundry.featured.order.commons.OrderRoutes
import com.almalaundry.featured.order.presentation.components.FilterDialog
import com.almalaundry.featured.order.presentation.components.OrderCard
import com.almalaundry.featured.order.presentation.components.shimmer.ShimmerOrderCard
import com.almalaundry.featured.order.presentation.viewmodels.OrderViewModel
import com.almalaundry.shared.commons.compositional.LocalNavController
import com.composables.icons.lucide.Filter
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Plus
import kotlinx.coroutines.flow.distinctUntilChanged

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun OrderScreen(
    viewModel: OrderViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val navController = LocalNavController.current
    val listState = rememberLazyListState()
    var showFilterDialog by remember { mutableStateOf(false) }

    // State untuk pull-to-refresh
    val isRefreshing by remember { derivedStateOf { state.isLoading } }
    val pullRefreshState =
        rememberPullRefreshState(refreshing = isRefreshing, onRefresh = { viewModel.loadOrders() })

    LaunchedEffect(Unit) { viewModel.loadOrders() }

    // Load more saat hampir sampai akhir list
    LaunchedEffect(listState) {
        snapshotFlow {
            val layoutInfo = listState.layoutInfo
            val totalItemsNumber = layoutInfo.totalItemsCount
            val lastVisibleItemIndex = (layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0) + 1

            lastVisibleItemIndex > 0 && lastVisibleItemIndex >= totalItemsNumber - 2
        }.distinctUntilChanged().collect {
            if (it && !state.isLoadingMore && !state.isLoading) {
                viewModel.loadOrders(isLoadMore = true)
            }
        }
    }

    Scaffold(contentWindowInsets = ScaffoldDefaults.contentWindowInsets.only(WindowInsetsSides.Top),
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(OrderRoutes.Create.route) }) {
                Icon(Lucide.Plus, "Create Order")
            }
        }) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .pullRefresh(pullRefreshState) // pull-to-refresh
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF8F9FA))
            ) {
                // App Bar
                Row(
                    modifier = Modifier.padding(
                        start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp
                    ),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Daftar Order",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Berikut adalah daftar order yang tersedia",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
//                    Row {
                    IconButton(onClick = { showFilterDialog = true }) {
                        Icon(Lucide.Filter, "Filter")
                    }
//                        IconButton(onClick = { viewModel.loadOrders() }) {
//                            Icon(
//                                imageVector = Lucide.RefreshCcw,
//                                contentDescription = "Refresh orders"
//                            )
//                        }
//                    }
                }

                when {
                    state.isLoading && !state.isLoadingMore -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            items(3) {
                                ShimmerOrderCard()
                            }
                        }
                    }

                    state.error != null -> {
                        Box(
                            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = state.error ?: "Unknown error occurred", color = Color.Red
                            )
                        }
                    }

                    else -> {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            items(state.orders) { order ->
                                OrderCard(order = order, onClick = {
                                    navController.navigate(OrderRoutes.Detail(order.id))
                                })
                            }

                            // Show loading more indicator
                            item {
                                if (state.isLoadingMore) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        ShimmerOrderCard()
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

    FilterDialog(show = showFilterDialog,
        currentFilter = state.filter,
        onDismiss = { showFilterDialog = false },
        onApply = { filter ->
            viewModel.applyFilter(filter)
        })
}