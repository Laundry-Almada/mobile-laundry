package com.almalaundry.featured.order.presentation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.almalaundry.featured.order.presentation.viewmodels.CreateOrderViewModel
import com.almalaundry.shared.commons.compositional.LocalNavController
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.Lucide

@Composable
fun CreateOrderScreen(
    viewModel: CreateOrderViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val navController = LocalNavController.current

    LaunchedEffect(state.success) {
        if (state.success) {
            navController.navigateUp()
        }
    }

    if (state.showNameDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.hideNameDialog() },
            title = { Text("Input Nama Customer") },
            text = {
                Column {
                    Text(
                        text = "Customer belum terdaftar di database, masukkan nama",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = state.name,
                        onValueChange = { viewModel.updateName(it) },
                        label = { Text("Nama") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (state.name.isNotBlank()) {
                            viewModel.hideNameDialog()
                            viewModel.saveCustomerAndCreateOrder()
                        }
                    },
                    enabled = state.name.isNotBlank()
                ) {
                    Text("Simpan")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.hideNameDialog() }) {
                    Text("Batal")
                }
            }
        )
    }

    Scaffold(topBar = {
        TopAppBar(title = { Text("Buat Order Baru") }, navigationIcon = {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(Lucide.ArrowLeft, "Back")
            }
        })
    }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = state.phone,
                onValueChange = { viewModel.updatePhone(it) },
                label = { Text("Nomor Telepon") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = state.laundryId,
                onValueChange = { viewModel.updateLaundryId(it) },
                label = { Text("ID Laundry") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = state.weight,
                onValueChange = { viewModel.updateWeight(it) },
                label = { Text("Berat (kg)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = state.totalPrice,
                onValueChange = { viewModel.updateTotalPrice(it) },
                label = { Text("Total Harga") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = state.note,
                onValueChange = { viewModel.updateNote(it) },
                label = { Text("Catatan") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.createOrder() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Buat Order")
                }
            }

            if (state.error != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = state.error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}