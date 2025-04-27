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
fun StatusChip(
    status: String, modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor, text) = when (status) {
        "washed" -> Triple(Color(0xFFBBDEFB), Color(0xFF0D47A1), "Dicuci") // Soft blue
        "dried" -> Triple(Color(0xFFFFF9C4), Color(0xFFF57F17), "Dikeringkan") // Soft yellow
        "ironed" -> Triple(Color(0xFFC8E6C9), Color(0xFF1B5E20), "Disetrika") // Soft green
        "ready_picked" -> Triple(Color(0xFFB2EBF2), Color(0xFF006064), "Siap Diambil") // Soft teal
        "completed" -> Triple(Color(0xFFD7CCC8), Color(0xFF5D4037), "Selesai") // Warm gray-brown
        "cancelled" -> Triple(Color(0xFFFFCDD2), Color(0xFFB71C1C), "Dibatalkan") // Soft red
        else -> Triple(Color(0xFFE0E0E0), Color(0xFF424242), "Menunggu") // Neutral gray
    }

    Surface(

        modifier = modifier,
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

