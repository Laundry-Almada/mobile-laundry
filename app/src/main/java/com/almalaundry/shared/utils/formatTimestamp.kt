package com.almalaundry.shared.utils

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// Fungsi untuk memformat timestamp
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun formatTimestamp(
    timestamp: String,
    inputFormatPattern: String = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
): String {
    return try {
        val inputFormat = DateTimeFormatter.ofPattern(inputFormatPattern)
        val outputFormat = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm")
        val parsedDate = LocalDateTime.parse(timestamp, inputFormat)
        parsedDate.format(outputFormat)
    } catch (e: Exception) {
        timestamp
    }
}