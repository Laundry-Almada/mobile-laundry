package com.almalaundry.shared.utils

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

// Fungsi untuk memformat timestamp
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun formatTimestamp(timestamp: String): String {
    return try {
        val inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val outputFormat = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm")
        val parsedDate = Instant.parse(timestamp).atZone(ZoneId.systemDefault()).toLocalDateTime()
        parsedDate.format(outputFormat)
    } catch (e: Exception) {
        timestamp
    }
}
