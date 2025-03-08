package com.almalaundry.featured.order.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ScanOverlay(cornerColor: Color) {
    val scanAreaSize = 250.dp // Ukuran kotak transparan di tengah
    val cornerLength = 30.dp // Panjang sudut L
    val cornerThickness = 4.dp // Ketebalan sudut

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val areaSizePx = scanAreaSize.toPx() // Mengonversi Dp ke Px
            val centerX = size.width / 2
            val centerY = size.height / 2
            val left = centerX - areaSizePx / 2
            val top = centerY - areaSizePx / 2
            val right = centerX + areaSizePx / 2
            val bottom = centerY + areaSizePx / 2

            // Buat Path untuk area di luar kotak pemindaian
            val path = Path().apply {
                // Gambar persegi luar (seluruh layar)
                moveTo(0f, 0f)
                lineTo(size.width, 0f)
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                lineTo(0f, 0f)
                close()

                // Potong area dalam (kotak transparan)
                moveTo(left, top)
                lineTo(left, bottom)
                lineTo(right, bottom)
                lineTo(right, top)
                lineTo(left, top)
                close()
            }

            // Gambar overlay gelap di luar kotak
            drawPath(
                path = path,
                color = Color.Black.copy(alpha = 0.5f), // Agak gelap, tidak gelap sepenuhnya
                style = Fill
            )

            // Gambar sudut-sudut
            val cornerLengthPx = cornerLength.toPx()
            val thicknessPx = cornerThickness.toPx()
            val halfThickness = thicknessPx / 2

            // Sudut kiri atas
            drawLine(
                color = cornerColor,
                start = Offset(left + halfThickness, top),
                end = Offset(left + cornerLengthPx, top),
                strokeWidth = thicknessPx
            )
            drawLine(
                color = cornerColor,
                start = Offset(left, top + halfThickness),
                end = Offset(left, top + cornerLengthPx),
                strokeWidth = thicknessPx
            )

            // Sudut kanan atas
            drawLine(
                color = cornerColor,
                start = Offset(right - cornerLengthPx, top),
                end = Offset(right - halfThickness, top),
                strokeWidth = thicknessPx
            )
            drawLine(
                color = cornerColor,
                start = Offset(right, top + halfThickness),
                end = Offset(right, top + cornerLengthPx),
                strokeWidth = thicknessPx
            )

            // Sudut kiri bawah
            drawLine(
                color = cornerColor,
                start = Offset(left + halfThickness, bottom),
                end = Offset(left + cornerLengthPx, bottom),
                strokeWidth = thicknessPx
            )
            drawLine(
                color = cornerColor,
                start = Offset(left, bottom - cornerLengthPx),
                end = Offset(left, bottom - halfThickness),
                strokeWidth = thicknessPx
            )

            // Sudut kanan bawah
            drawLine(
                color = cornerColor,
                start = Offset(right - cornerLengthPx, bottom),
                end = Offset(right - halfThickness, bottom),
                strokeWidth = thicknessPx
            )
            drawLine(
                color = cornerColor,
                start = Offset(right, bottom - cornerLengthPx),
                end = Offset(right, bottom - halfThickness),
                strokeWidth = thicknessPx
            )
        }

        // Teks keterangan di bawah kotak
        Text(
            text = "Scan QR Code di sini",
            color = Color.White,
            fontSize = 16.sp,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (scanAreaSize / 2 + 30.dp))
        )
    }
}
