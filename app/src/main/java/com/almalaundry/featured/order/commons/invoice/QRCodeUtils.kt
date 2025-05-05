package com.almalaundry.featured.order.commons.invoice

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel

object QRCodeUtils {
    fun generateQRCode(data: String, size: Int): Bitmap? {
        return try {
            // Tingkatkan ukuran QR Code untuk lebih jelas (tanpa memperbesar pada kertas)
            val qrCodeSize = size
            val margin = 1

            val hints = mapOf(
                EncodeHintType.MARGIN to margin,
                EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.H
            )

            val bitMatrix: BitMatrix = MultiFormatWriter().encode(
                data, BarcodeFormat.QR_CODE, qrCodeSize, qrCodeSize, hints
            )

            val bitmap = Bitmap.createBitmap(qrCodeSize, qrCodeSize, Bitmap.Config.ARGB_8888)

            for (x in 0 until qrCodeSize) {
                for (y in 0 until qrCodeSize) {
                    bitmap.setPixel(
                        x,
                        y,
                        if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE
                    )
                }
            }
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}