package com.almalaundry.featured.order.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.almalaundry.featured.order.domain.models.Order

@Composable
fun OrderCard(order: Order, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = order.customerName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = order.phoneNumber,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Type: ${order.type}",
                    style = MaterialTheme.typography.bodyMedium
                )
                // Menggunakan Surface sebagai badge untuk status
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = when (order.status) {
                        "Dicuci" -> Color(0xFF2196F3).copy(alpha = 0.2f)
                        "Dikeringkan" -> Color(0xFFFFEB3B).copy(alpha = 0.2f)
                        "Disetrika" -> Color(0xFF4CAF50).copy(alpha = 0.2f)
                        "Siap diambil" -> Color(0xFF00BCD4).copy(alpha = 0.2f)
                        else -> Color.Gray.copy(alpha = 0.2f)
                    },
                    modifier = Modifier.padding(4.dp)
                ) {
                    Text(
                        text = order.status,
                        style = MaterialTheme.typography.bodyMedium,
                        color = when (order.status) {
                            "Dicuci" -> Color(0xFF1976D2)
                            "Dikeringkan" -> Color(0xFFFBC02D)
                            "Disetrika" -> Color(0xFF388E3C)
                            "Siap diambil" -> Color(0xFF0097A7)
                            else -> Color.DarkGray
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}