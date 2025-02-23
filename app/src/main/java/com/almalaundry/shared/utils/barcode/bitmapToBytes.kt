package com.almalaundry.shared.utils.barcode

import android.graphics.Bitmap

// Konversi bitmap ke byte array untuk printer thermal/untuk ESC/POS
fun bitmapToBytes(bitmap: Bitmap): ByteArray {
    val width = bitmap.width
    val height = bitmap.height
    val bytes = ByteArray(width * height / 8 + height + 8)
    var index = 0

    bytes[index++] = 0x1D.toByte()
    bytes[index++] = 0x76.toByte()
    bytes[index++] = 0x30.toByte()
    bytes[index++] = 0x00.toByte()
    bytes[index++] = (width / 8).toByte()
    bytes[index++] = 0x00.toByte()
    bytes[index++] = (height % 256).toByte()
    bytes[index++] = (height / 256).toByte()

    for (y in 0 until height) {
        for (x in 0 until width step 8) {
            var value = 0
            for (bit in 0..7) {
                if (x + bit < width) {
                    value = value shl 1
                    value = value or (if (bitmap.getPixel(
                            x + bit, y
                        ) == android.graphics.Color.BLACK
                    ) 1 else 0)
                }
            }
            bytes[index++] = value.toByte()
        }
    }
    return bytes
}