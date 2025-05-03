package com.almalaundry.shared.utils

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

fun formatDateToIndonesian(dateString: String): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault())
    inputFormat.timeZone = TimeZone.getTimeZone("UTC")
    val outputFormat = SimpleDateFormat("dd-MM-yyyy, HH:mm", Locale("id", "ID"))
    val date = inputFormat.parse(dateString)
    return outputFormat.format(date!!)
}
