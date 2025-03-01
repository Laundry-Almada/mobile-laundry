package com.almalaundry.featured.order.presentation.components.shimmer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer

@Composable
fun ShimmerOrderCard() {
    val shimmerInstance = rememberShimmer(shimmerBounds = ShimmerBounds.View)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = BorderStroke(1.dp, Color.LightGray),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .shimmer(shimmerInstance)
        ) {
            // Shimmer untuk nama customer
            Box(
                modifier = Modifier
                    .height(24.dp)
                    .fillMaxWidth(0.7f)
                    .background(Color.LightGray, RoundedCornerShape(4.dp))
            )
            Spacer(modifier = Modifier.height(4.dp))

            // Shimmer untuk nomor telepon
            Box(
                modifier = Modifier
                    .height(20.dp)
                    .fillMaxWidth(0.5f)
                    .background(Color.LightGray, RoundedCornerShape(4.dp))
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Shimmer untuk type dan status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .height(20.dp)
                        .width(80.dp)
                        .background(Color.LightGray, RoundedCornerShape(4.dp))
                )
                Box(
                    modifier = Modifier
                        .height(20.dp)
                        .width(100.dp)
                        .background(Color.LightGray, RoundedCornerShape(16.dp))
                )
            }
        }
    }
}
