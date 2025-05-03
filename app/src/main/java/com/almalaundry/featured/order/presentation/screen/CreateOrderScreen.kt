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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.almalaundry.R
import com.almalaundry.featured.order.presentation.viewmodels.CreateOrderViewModel
import com.almalaundry.shared.commons.compositional.LocalNavController
import com.almalaundry.shared.presentation.components.BannerHeader
import com.composables.icons.lucide.ChevronDown
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Search
import com.composables.icons.lucide.X

@Composable
fun CreateOrderScreen(
    viewModel: CreateOrderViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val navController = LocalNavController.current
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(state.success) {
        if (state.success) {
            navController.navigateUp()
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
                    title = "Buat Order Baru",
                    subtitle = "Masukkan detail order baru",
                    imageResId = R.drawable.header_basic2,
                    onBackClick = { navController.navigateUp() },
                    titleAlignment = Alignment.Start
                )

                // LazyColumn untuk konten
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
                                modifier = Modifier.padding(16.dp)
                            ) {
                                // Input untuk pencarian nama
                                var isCustomerDropdownExpanded by remember { mutableStateOf(false) }
                                var textFieldWidth by remember { mutableFloatStateOf(0f) }

                                Box(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    OutlinedTextField(
                                        value = state.name,
                                        onValueChange = { viewModel.updateName(it) },
                                        label = { Text("Nama Customer") },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .onGloballyPositioned { coordinates ->
                                                textFieldWidth = coordinates.size.width.toFloat()
                                            },
                                        trailingIcon = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                // Reset Icon
                                                if (state.name.isNotEmpty()) {
                                                    IconButton(onClick = { viewModel.updateName("") }) {
                                                        Icon(
                                                            imageVector = Lucide.X,
                                                            contentDescription = "Reset Nama",
                                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                            modifier = Modifier.size(16.dp)
                                                        )
                                                    }
                                                }

                                                // Vertical Divider
                                                VerticalDivider(
                                                    modifier = Modifier
                                                        .padding(horizontal = 4.dp)
                                                        .height(24.dp),
                                                )

                                                // Search Icon
                                                IconButton(onClick = {
                                                    isCustomerDropdownExpanded = true
                                                    viewModel.searchCustomers()
                                                    keyboardController?.show()
                                                }) {
                                                    Icon(
                                                        imageVector = Lucide.Search,
                                                        contentDescription = "Cari Customer"
                                                    )
                                                }
                                            }
                                        },
                                        keyboardOptions = KeyboardOptions(
                                            keyboardType = KeyboardType.Text,
                                            imeAction = ImeAction.None
                                        ),
                                    )

                                    DropdownMenu(
                                        expanded = isCustomerDropdownExpanded,
                                        onDismissRequest = { isCustomerDropdownExpanded = false },
                                        modifier = Modifier
                                            .width(with(LocalDensity.current) { textFieldWidth.toDp() })
                                            .background(MaterialTheme.colorScheme.surface)
                                    ) {
                                        if (state.isLoadingCustomers) {
                                            DropdownMenuItem(
                                                text = {
                                                    CircularProgressIndicator(
                                                        modifier = Modifier
                                                            .size(24.dp)
                                                            .align(Alignment.CenterHorizontally)
                                                    )
                                                },
                                                onClick = {}
                                            )
                                        } else if (state.customerSearchError != null) {
                                            DropdownMenuItem(
                                                text = {
                                                    Text(
                                                        text = state.customerSearchError
                                                            ?: "Gagal mencari",
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        color = MaterialTheme.colorScheme.error
                                                    )
                                                },
                                                onClick = {}
                                            )
                                        } else {
                                            state.customers.forEach { customer ->
                                                DropdownMenuItem(
                                                    text = {
                                                        Column {
                                                            Text(
                                                                text = customer.name,
                                                                style = MaterialTheme.typography.bodyMedium
                                                            )
                                                            Text(
                                                                text = when {
                                                                    customer.phone?.isNotBlank() == true -> "Phone: ${customer.phone}"
                                                                    customer.username?.isNotBlank() == true -> "Username: ${customer.username}"
                                                                    else -> "No identifier"
                                                                },
                                                                style = MaterialTheme.typography.bodySmall,
                                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                                            )
                                                        }
                                                    },
                                                    onClick = {
                                                        viewModel.selectCustomer(customer)
                                                        isCustomerDropdownExpanded = false
                                                        focusManager.moveFocus(FocusDirection.Down)
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Checkbox untuk nomor WhatsApp
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = state.hasWhatsApp,
                                        onCheckedChange = { viewModel.updateHasWhatsApp(it) }
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Customer memiliki nomor WhatsApp",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Input berdasarkan hasWhatsApp
                                if (state.hasWhatsApp) {
                                    OutlinedTextField(
                                        value = state.phone,
                                        onValueChange = { viewModel.updatePhone(it) },
                                        label = { Text("Nomor Telepon") },
                                        modifier = Modifier.fillMaxWidth(),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                        placeholder = {
                                            if (state.customers.isEmpty() && state.name.isNotBlank()) {
                                                Text(
                                                    text = "Masukkan nomor untuk customer baru",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    )
                                } else {
                                    OutlinedTextField(
                                        value = state.username,
                                        onValueChange = { viewModel.updateUsername(it) },
                                        label = { Text("Username") },
                                        modifier = Modifier.fillMaxWidth(),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                                        placeholder = {
                                            if (state.customers.isEmpty() && state.name.isNotBlank()) {
                                                Text(
                                                    text = "Masukkan username baru atau kosongkan untuk otomatis",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Service Dropdown
                                if (state.isLoadingServices) {
                                    CircularProgressIndicator(
                                        modifier = Modifier
                                            .padding(vertical = 8.dp)
                                            .align(Alignment.CenterHorizontally)
                                    )
                                } else if (state.servicesError != null) {
                                    Text(
                                        text = state.servicesError ?: "Gagal memuat layanan",
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                } else {
                                    var isServiceDropdownExpanded by remember { mutableStateOf(false) }
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp)
                                    ) {
                                        OutlinedTextField(
                                            value = state.services.find { it.id == state.serviceId }?.name
                                                ?: "Pilih Layanan",
                                            onValueChange = {},
                                            modifier = Modifier.fillMaxWidth(),
                                            readOnly = true,
                                            label = { Text("Layanan") },
                                            trailingIcon = {
                                                IconButton(onClick = {
                                                    isServiceDropdownExpanded = true
                                                }) {
                                                    Icon(
                                                        imageVector = Lucide.ChevronDown,
                                                        contentDescription = "Dropdown"
                                                    )
                                                }
                                            }
                                        )
                                        DropdownMenu(
                                            expanded = isServiceDropdownExpanded,
                                            onDismissRequest = {
                                                isServiceDropdownExpanded = false
                                            },
                                            modifier = Modifier
                                                .width(with(LocalDensity.current) { textFieldWidth.toDp() })
                                                .background(MaterialTheme.colorScheme.surface)
                                        ) {
                                            state.services.forEach { service ->
                                                DropdownMenuItem(
                                                    text = {
                                                        Text(
                                                            text = service.name,
                                                            style = MaterialTheme.typography.bodyMedium
                                                        )
                                                    },
                                                    onClick = {
                                                        viewModel.updateServiceId(service.id)
                                                        isServiceDropdownExpanded = false
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Berat
                                OutlinedTextField(
                                    value = state.weight,
                                    onValueChange = { viewModel.updateWeight(it) },
                                    label = { Text("Berat (kg)") },
                                    modifier = Modifier.fillMaxWidth(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // Total Harga
                                OutlinedTextField(
                                    value = state.totalPrice,
                                    onValueChange = { viewModel.updateTotalPrice(it) },
                                    label = { Text("Total Harga") },
                                    modifier = Modifier.fillMaxWidth(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // Catatan
                                OutlinedTextField(
                                    value = state.note,
                                    onValueChange = { viewModel.updateNote(it) },
                                    label = { Text("Catatan") },
                                    modifier = Modifier.fillMaxWidth(),
                                    minLines = 3
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                // Tombol Buat Order
                                Button(
                                    onClick = { viewModel.createOrder() },
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = !state.isLoading
                                ) {
                                    if (state.isLoading) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(24.dp),
                                            color = MaterialTheme.colorScheme.onPrimary
                                        )
                                    } else {
                                        Text("Buat Order")
                                    }
                                }

                                // Pesan Error
                                if (state.error != null) {
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = state.error ?: "Terjadi kesalahan",
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodyMedium
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

@Composable
fun DropdownMenuItem(
    text: @Composable () -> Unit,
    onClick: () -> Unit
) {
    androidx.compose.material3.DropdownMenuItem(
        text = text,
        onClick = onClick
    )
}