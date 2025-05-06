package com.almalaundry.featured.home.data.models

import com.google.gson.annotations.SerializedName

data class MonthlyStatistic(
    @SerializedName("month") val month: String, // Format: "YYYY-MM"
    @SerializedName("count") val count: Int,
    @SerializedName("revenue") val revenue: Double
)
