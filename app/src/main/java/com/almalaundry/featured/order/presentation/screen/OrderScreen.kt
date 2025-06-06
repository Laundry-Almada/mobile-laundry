package com.almalaundry.featured.order.presentation.screen

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.almalaundry.R
import com.almalaundry.featured.order.commons.OrderRoutes
import com.almalaundry.featured.order.presentation.components.ErrorCard
import com.almalaundry.featured.order.presentation.components.FilterDialog
import com.almalaundry.featured.order.presentation.components.OrderCard
import com.almalaundry.featured.order.presentation.components.shimmer.ShimmerOrderCard
import com.almalaundry.featured.order.presentation.viewmodels.OrderViewModel
import com.almalaundry.shared.commons.compositional.LocalNavController
import com.almalaundry.shared.commons.compositional.LocalSessionManager
import com.almalaundry.shared.presentation.components.BannerHeader
import com.composables.icons.lucide.Filter
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Plus
import com.composables.icons.lucide.Search
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class, FlowPreview::class)
@Composable
fun OrderScreen(
    viewModel: OrderViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val navController = LocalNavController.current
    val sessionManager = LocalSessionManager.current
    val listState = rememberLazyListState()
    var showFilterDialog by remember { mutableStateOf(false) }
    var showSearchDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    var searchJob by remember { mutableStateOf<Job?>(null) }

    // State for pull-to-refresh
    val isRefreshing by remember { derivedStateOf { state.isLoading } }
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = { viewModel.loadOrders() }
    )

    // Load orders on first launch
    LaunchedEffect(Unit) {
        viewModel.loadOrders()
    }

    // Load more when nearing the end of the list
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
                if (shouldLoadMore && !state.isLoadingMore && !state.isLoading && state.hasMoreData) {
                    viewModel.loadOrders(isLoadMore = true)
                }
            }
    }

    Scaffold(
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.only(WindowInsetsSides.Top),
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(OrderRoutes.Create.route) }) {
                Icon(Lucide.Plus, contentDescription = "Create Order")
            }
        }
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
                    title = "Daftar Order",
                    subtitle = "Order yang diproses dan selesai",
                    imageResId = R.drawable.header_basic2,
                    actionButtons = {
                        Row {
                            // Tombol search
                            IconButton(onClick = { showSearchDialog = true }) {
                                Icon(
                                    imageVector = Lucide.Search,
                                    contentDescription = "Search Orders",
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                            // Tombol filter
                            IconButton(onClick = { showFilterDialog = true }) {
                                Icon(
                                    imageVector = Lucide.Filter,
                                    contentDescription = "Filter",
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }
                )

                // LazyColumn dengan offset untuk menutupi banner
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .offset(y = (-40).dp)
                        .background(Color.Transparent)
                ) {
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
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                ErrorCard(
                                    message = state.error ?: "Terjadi kesalahan saat memuat order",
                                    onRetry = { viewModel.loadOrders() }
                                )
                            }
                        }

                        state.orders.isEmpty() && state.totalOrders > 0 -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                ) {
                                    Text(
                                        text = "Tidak ada order di halaman ini. Coba ubah filter atau muat ulang.",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        style = MaterialTheme.typography.bodyMedium,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    TextButton(onClick = { viewModel.loadOrders() }) {
                                        Text(
                                            "Muat Ulang",
                                            style = MaterialTheme.typography.labelLarge
                                        )
                                    }
                                }
                            }
                        }

                        state.orders.isEmpty() -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Tidak ada order tersedia.",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 16.dp)
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
                                    OrderCard(
                                        order = order,
                                        onClick = { navController.navigate(OrderRoutes.Detail(order.id)) }
                                    )
                                }

                                item {
                                    if (state.isLoadingMore) {
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

            // Indikator pull-to-refresh
            PullRefreshIndicator(
                refreshing = isRefreshing,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }

    // Filter Dialog
    FilterDialog(
        show = showFilterDialog,
        currentFilter = state.filter,
        onDismiss = { showFilterDialog = false },
        onApply = { filter -> viewModel.applyFilter(filter) },
        sessionManager = sessionManager
    )

    // Search Dialog
    if (showSearchDialog) {
        AlertDialog(
            onDismissRequest = { showSearchDialog = false },
            title = { Text("Cari Order") },
            text = {
                Column {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { newQuery ->
                            searchQuery = newQuery
                            searchJob?.cancel()
                            searchJob = coroutineScope.launch {
                                delay(500) // Debounce 500ms
                                if (newQuery.length >= 3) {
                                    viewModel.searchOrders(newQuery)
                                }
                            }
                        },
                        label = { Text("Nama/Nomor WA/Username") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        trailingIcon = {
                            if (searchQuery.isNotBlank()) {
                                IconButton(onClick = {
                                    searchQuery = ""
                                    viewModel.searchOrders("")
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear Search"
                                    )
                                }
                            }
                        }
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (searchQuery.length >= 3) {
                            viewModel.searchOrders(searchQuery)
                        }
                        showSearchDialog = false
                    }
                ) {
                    Text("Cari")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        searchQuery = ""
                        viewModel.searchOrders("")
                        showSearchDialog = false
                    }
                ) {
                    Text("Clear")
                }
            }
        )
    }
}