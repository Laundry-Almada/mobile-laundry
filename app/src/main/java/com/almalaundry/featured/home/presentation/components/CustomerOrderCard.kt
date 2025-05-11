package com.almalaundry.featured.home.presentation.components

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.almalaundry.featured.order.domain.models.Order
import com.almalaundry.featured.order.presentation.components.StatusChip
import com.almalaundry.shared.utils.formatTimestamp
import com.almalaundry.shared.utils.openWhatsApp
import com.composables.icons.lucide.ChevronDown
import com.composables.icons.lucide.ChevronUp
import com.composables.icons.lucide.CircleCheckBig
import com.composables.icons.lucide.CircleHelp
import com.composables.icons.lucide.CircleX
import com.composables.icons.lucide.Clock
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Package
import com.composables.icons.lucide.Shirt
import com.composables.icons.lucide.WashingMachine
import com.composables.icons.lucide.Wind
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Brands
import compose.icons.fontawesomeicons.brands.Whatsapp

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CustomerOrderCard(
    order: Order,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Ikon berdasarkan status
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
            .padding(horizontal = 8.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column {
            // Header Accordion
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(bottom = 8.dp, top = 8.dp, start = 16.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(3f)
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
                    // Tanggal order
                    Text(
                        text = order.orderDate?.let { date ->
                            formatTimestamp(date, "yyyy-MM-dd HH:mm:ss")
                        } ?: "-",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                Column(
                    modifier = Modifier.weight(2f),
                    horizontalAlignment = Alignment.End
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(32.dp)
                            .padding(end = 8.dp)
                    )
                    StatusChip(
                        status = order.status,
                        modifier = Modifier.align(Alignment.End)
                    )
                }
                Icon(
                    imageVector = if (expanded) Lucide.ChevronUp else Lucide.ChevronDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .size(24.dp)
                        .padding(start = 8.dp)
                )
            }

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f))
                        .padding(top = 4.dp, bottom = 16.dp, start = 16.dp, end = 16.dp),
                ) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                    // Laundry name dan chat
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (order.laundry.phone?.isNotBlank() == true) {
                                    val customerIdentity = when {
                                        order.customer.phone?.isNotBlank() == true -> "No. telepon ${order.customer.phone}"
                                        order.customer.username?.isNotBlank() == true -> "Username @${order.customer.username}"
                                        else -> "tidak tersedia"
                                    }
                                    openWhatsApp(
                                        phone = order.laundry.phone,
                                        message = "Halo, saya ingin bertanya tentang pesanan laundry dengan nama ${order.customer.name}, $customerIdentity, barcode ${order.barcode}",
                                        context = context
                                    )
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Nomor telepon tidak tersedia",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Laundry: ${order.laundry.name}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = FontAwesomeIcons.Brands.Whatsapp,
                            contentDescription = "Chat via WhatsApp",
                            tint = Color(0xFF25D366),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "chat",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    // Nomor telepon
                    order.customer.phone?.let { phone ->
                        Text(
                            text = "No. Telepon: $phone",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    } ?: Text(
                        text = "No. Telepon: Tidak tersedia",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    // username
                    order.customer.username?.takeIf { it.isNotBlank() }?.let { username ->
                        Text(
                            text = "Username: @$username",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    // Layanan
                    Text(
                        text = "Layanan: ${order.service.name}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    // Berat
                    Text(
                        text = "Berat: ${order.weight} kg",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    // Harga total
                    Text(
                        text = "Harga Total: Rp${order.totalPrice}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    // Catatan
                    if (!order.note.isNullOrBlank()) {
                        Text(
                            text = "Catatan: ${order.note}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    // Created at
                    Text(
                        text = "Dibuat: ${formatTimestamp(order.createdAt)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    // Updated at
                    Text(
                        text = "Diperbarui: ${formatTimestamp(order.updatedAt)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}