package com.almalaundry.featured.order.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun StatusChip(status: String) {
    val (backgroundColor, textColor, text) = when (status) {
        "washed" -> Triple(Color(0xFFE3F2FD), Color(0xFF1976D2), "Dicuci")
        "dried" -> Triple(Color(0xFFFFFDE7), Color(0xFFFBC02D), "Dikeringkan")
        "ironed" -> Triple(Color(0xFFE8F5E9), Color(0xFF388E3C), "Disetrika")
        "ready_picked" -> Triple(Color(0xFFE0F7FA), Color(0xFF0097A7), "Siap Diambil")
        "completed" -> Triple(Color(0xFFF5F5F5), Color.DarkGray, "Selesai")
        "cancelled" -> Triple(Color(0xFFFFEBEE), Color(0xFFD32F2F), "Dibatalkan")
        else -> Triple(Color(0xFFF5F5F5), Color.DarkGray, "Menunggu") // untuk "pending"
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = textColor,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

