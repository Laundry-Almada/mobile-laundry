package com.almalaundry.shared.utils

import java.text.SimpleDateFormat
import java.util.Locale

fun formatDateToIndonesian(dateString: String): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val outputFormat = SimpleDateFormat("dd-MM-yyyy, HH:mm:ss", Locale("id", "ID"))
    val date = inputFormat.parse(dateString)
    return outputFormat.format(date!!)
}