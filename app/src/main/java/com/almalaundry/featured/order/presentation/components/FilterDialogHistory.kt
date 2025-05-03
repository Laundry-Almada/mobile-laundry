package com.almalaundry.featured.order.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.almalaundry.featured.order.domain.models.OrderFilter
import com.almalaundry.featured.order.presentation.viewmodels.ServiceViewModel
import com.almalaundry.shared.commons.session.SessionManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val statusTranslations = mapOf(
    "completed" to "Selesai",
    "cancelled" to "Dibatalkan"
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterDialogHistory(
    show: Boolean,
    currentFilter: OrderFilter,
    onDismiss: () -> Unit,
    onApply: (OrderFilter) -> Unit,
    serviceViewModel: ServiceViewModel = hiltViewModel(),
    sessionManager: SessionManager
) {
    if (!show) return

    var filter by remember { mutableStateOf(currentFilter) }
    var showDatePicker by remember { mutableStateOf(false) }
    var isStartDate by remember { mutableStateOf(true) }
    val textFieldWidth by remember { mutableFloatStateOf(0f) }

    // Ambil laundryId dari SessionManager
    var laundryId by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(Unit) {
        laundryId = sessionManager.getLaundryId()
        laundryId?.let { serviceViewModel.fetchServices(it) }
    }

    val serviceState by serviceViewModel.state.collectAsState()
    val services = serviceState.services
    val isLoadingServices = serviceState.isLoading
    val serviceError = serviceState.errorMessage

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Filter Pesanan",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Status Filter
                Text(
                    text = "Status",
                    style = MaterialTheme.typography.bodyMedium
                )
                FlowRow(
                    modifier = Modifier.padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    statusTranslations.forEach { (englishStatus, indonesianStatus) ->
                        FilterChip(
                            selected = filter.status.contains(englishStatus),
                            onClick = {
                                filter = if (filter.status.contains(englishStatus)) {
                                    filter.copy(status = filter.status - englishStatus)
                                } else {
                                    filter.copy(status = filter.status + englishStatus)
                                }
                            },
                            label = {
                                Text(
                                    text = indonesianStatus,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        )
                    }
                }

                // Service Filter
                if (laundryId == null) {
                    Text(
                        text = "Tidak dapat memuat layanan: Laundry tidak ditemukan",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                } else if (isLoadingServices) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                } else if (serviceError != null) {
                    Text(
                        text = serviceError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                } else {
                    var expanded by remember { mutableStateOf(false) }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        OutlinedTextField(
                            value = services.find { it.id == filter.serviceId }?.name
                                ?: "Pilih Layanan",
                            onValueChange = {},
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true,
                            label = {
                                Text(
                                    "Layanan",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            },
                            trailingIcon = {
                                IconButton(onClick = { expanded = true }) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = "Pilih Layanan"
                                    )
                                }
                            }
                        )
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier
                                .width(with(LocalDensity.current) { textFieldWidth.toDp() })
                                .background(MaterialTheme.colorScheme.surface)
                        ) {
                            services.forEach { service ->
                                DropdownMenuItem(
                                    onClick = {
                                        filter = filter.copy(serviceId = service.id)
                                        expanded = false
                                    }
                                ) {
                                    Text(
                                        text = service.name,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                            // Opsi untuk reset service
                            DropdownMenuItem(
                                onClick = {
                                    filter = filter.copy(serviceId = null)
                                    expanded = false
                                }
                            ) {
                                Text(
                                    text = "Semua Layanan",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }

                // Date Range
                Text(
                    text = "Rentang Tanggal",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            isStartDate = true
                            showDatePicker = true
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = filter.startDate ?: "Tanggal Awal",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    OutlinedButton(
                        onClick = {
                            isStartDate = false
                            showDatePicker = true
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = filter.endDate ?: "Tanggal Akhir",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                // Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(text = "Batal", style = MaterialTheme.typography.labelLarge)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = {
                        onApply(filter)
                        onDismiss()
                    }) {
                        Text(text = "Terapkan", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        val dateRangePickerState = rememberDateRangePickerState()

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    dateRangePickerState.selectedStartDateMillis?.let { startMillis ->
                        val startDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(
                            Date(startMillis)
                        )
                        filter = filter.copy(startDate = startDate)
                    }

                    dateRangePickerState.selectedEndDateMillis?.let { endMillis ->
                        val endDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(
                            Date(endMillis)
                        )
                        filter = filter.copy(endDate = endDate)
                    }

                    showDatePicker = false
                }) {
                    Text(text = "OK", style = MaterialTheme.typography.labelLarge)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(text = "Batal", style = MaterialTheme.typography.labelLarge)
                }
            }
        ) {
            DateRangePicker(
                state = dateRangePickerState,
                title = {
                    Text(
                        text = "Pilih rentang tanggal",
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                showModeToggle = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp)
                    .padding(16.dp)
            )
        }
    }
}