package com.almalaundry.featured.order.presentation.components

import androidx.compose.runtime.Composable

@Composable
fun DropdownMenuItem(
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    androidx.compose.material3.DropdownMenuItem(
        text = content,
        onClick = onClick
    )
}