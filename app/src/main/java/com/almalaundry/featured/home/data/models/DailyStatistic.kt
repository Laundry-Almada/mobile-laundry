package com.almalaundry.featured.home.data.models

import com.google.gson.annotations.SerializedName

data class DailyStatistic(
    @SerializedName("date") val date: String, // Format: "YYYY-MM-DD"
    @SerializedName("count") val count: Int,
    @SerializedName("revenue") val revenue: Double
)
