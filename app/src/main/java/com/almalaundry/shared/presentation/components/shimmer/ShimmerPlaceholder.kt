package com.almalaundry.shared.presentation.components.shimmer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer

@Composable
fun ShimmerPlaceholder() {
    val shimmer = rememberShimmer(shimmerBounds = ShimmerBounds.View)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .shimmer(shimmer)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.LightGray.copy(alpha = 0.6f),
                        Color.LightGray.copy(alpha = 0.2f),
                        Color.LightGray.copy(alpha = 0.6f)
                    )
                ),
                shape = RoundedCornerShape(20.dp)
            )
    ) {
        // Simulasi struktur konten kartu
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Placeholder untuk teks judul
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(14.dp)
                    .background(Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
            )
            Spacer(modifier = Modifier.height(8.dp))
            // Placeholder untuk chart
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(Color.Gray.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
            )
        }
    }
}