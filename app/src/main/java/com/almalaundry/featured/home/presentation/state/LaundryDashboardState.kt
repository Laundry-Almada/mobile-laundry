package com.almalaundry.featured.home.presentation.state

import com.almalaundry.featured.home.data.models.DailyStatistic
import com.almalaundry.featured.home.data.models.MonthlyStatistic

data class LaundryDashboardState(
    val dailyStats: List<DailyStatistic> = emptyList(),
    val monthlyStats: List<MonthlyStatistic> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val userName: String = "User",
    val laundryName: String = "Loading..."
)