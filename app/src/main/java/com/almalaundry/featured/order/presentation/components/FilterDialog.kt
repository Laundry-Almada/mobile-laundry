package com.almalaundry.featured.order.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.almalaundry.featured.order.domain.models.OrderFilter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterDialog(
    show: Boolean, currentFilter: OrderFilter, onDismiss: () -> Unit, onApply: (OrderFilter) -> Unit
) {
    if (!show) return

    var filter by remember { mutableStateOf(currentFilter) }
    var showDatePicker by remember { mutableStateOf(false) }
    var isStartDate by remember { mutableStateOf(true) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    "Filter Orders",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Status Filter
                Text("Status", style = MaterialTheme.typography.titleSmall)
                FlowRow(
                    modifier = Modifier.padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        "pending",
                        "washed",
                        "dried",
                        "ironed",
                        "ready_picked",
                        "completed",
                        "cancelled"
                    ).forEach { status ->
                        FilterChip(selected = filter.status.contains(status), onClick = {
                            filter = if (filter.status.contains(status)) {
                                filter.copy(status = filter.status - status)
                            } else {
                                filter.copy(status = filter.status + status)
                            }
                        }, label = {
                            Text(status.replaceFirstChar {
                                if (it.isLowerCase()) it.titlecase(
                                    Locale.getDefault()
                                ) else it.toString()
                            })
                        })
                    }
                }

                // Type Filter
                Text("Type", style = MaterialTheme.typography.titleSmall)
                FlowRow(
                    modifier = Modifier.padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("express", "regular", "kiloan", "satuan").forEach { type ->
                        FilterChip(selected = filter.type == type, onClick = {
                            filter = filter.copy(type = if (filter.type == type) null else type)
                        }, label = {
                            Text(type.replaceFirstChar {
                                if (it.isLowerCase()) it.titlecase(
                                    Locale.getDefault()
                                ) else it.toString()
                            })
                        })
                    }
                }

                // Date Range
                Text("Date Range", style = MaterialTheme.typography.titleSmall)
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
                        }, modifier = Modifier.weight(1f)
                    ) {
                        Text(filter.startDate ?: "Start Date")
                    }
                    OutlinedButton(
                        onClick = {
                            isStartDate = false
                            showDatePicker = true
                        }, modifier = Modifier.weight(1f)
                    ) {
                        Text(filter.endDate ?: "End Date")
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
                        Text("Cancel")
                    }
                    TextButton(onClick = {
                        onApply(filter)
                        onDismiss()
                    }) {
                        Text("Apply")
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        val dateRangePickerState = rememberDateRangePickerState()

        DatePickerDialog(onDismissRequest = { showDatePicker = false }, confirmButton = {
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
                Text("OK")
            }
        }, dismissButton = {
            TextButton(onClick = { showDatePicker = false }) {
                Text("Cancel")
            }
        }) {
            DateRangePicker(
                state = dateRangePickerState,
                title = { Text("Select date range") },
                showModeToggle = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp)
                    .padding(16.dp)
            )
        }
    }
}
