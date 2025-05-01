package com.almalaundry.featured.home.data.models

data class MonthlyStatistic(
    val month: String, // Format: "YYYY-MM"
    val count: Int,
    val revenue: Double,
    val washIronCount: Int, // Cuci Setrika
    val washFoldCount: Int, // Cuci Lipat
    val dryCleanCount: Int // Cuci Kering
)
