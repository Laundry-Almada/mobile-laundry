package com.almalaundry.featured.order.commons.barcode

fun extractOrderId(url: String): String? {
    return try {
        // Mengambil bagian terakhir dari URL (ID order)
        url.split("/").last()
    } catch (e: Exception) {
        null
    }
}