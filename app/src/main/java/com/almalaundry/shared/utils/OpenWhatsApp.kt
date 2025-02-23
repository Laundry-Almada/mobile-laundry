package com.almalaundry.shared.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast


fun openWhatsApp(phone: String, message: String, context: Context) {
    val formattedPhone = if (phone.startsWith("0")) {
        "+62${phone.drop(1)}" // Ganti 0 dengan +62
    } else {
        "+62$phone"
    }
    // Encode pesan agar aman untuk URL
    val encodedMessage = Uri.encode(message)
    val url = "https://wa.me/$formattedPhone?text=$encodedMessage"
    val intent = Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse(url)
    }
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "WhatsApp tidak terinstall atau nomor salah", Toast.LENGTH_SHORT)
            .show()
    }
}