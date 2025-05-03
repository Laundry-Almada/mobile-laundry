package com.almalaundry.featured.order.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.almalaundry.featured.order.domain.models.Order
import com.composables.icons.lucide.CircleCheckBig
import com.composables.icons.lucide.CircleHelp
import com.composables.icons.lucide.CircleX
import com.composables.icons.lucide.Clock
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Package
import com.composables.icons.lucide.Shirt
import com.composables.icons.lucide.WashingMachine
import com.composables.icons.lucide.Wind

@Composable
fun OrderCard(
    order: Order,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val icon = when (order.status) {
        "pending" -> Lucide.Clock
        "washed" -> Lucide.WashingMachine
        "dried" -> Lucide.Wind
        "ironed" -> Lucide.Shirt
        "ready_picked" -> Lucide.Package
        "completed" -> Lucide.CircleCheckBig
        "cancelled" -> Lucide.CircleX
        else -> Lucide.CircleHelp
    }
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(2f)
            ) {
                // Nama pelanggan
                Text(
                    text = order.customer.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Nomor telepon atau username
                Text(
                    text = order.customer.phone ?: "@${order.customer.username}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Layanan
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Layanan: ${order.service.name}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

            }

            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = modifier
                        .size(52.dp)
                        .padding(end = 18.dp)
                        .align(Alignment.End)
                )
                StatusChip(
                    status = order.status,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}



