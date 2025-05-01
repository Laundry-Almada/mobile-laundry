package com.almalaundry.featured.home.presentation.state

import com.almalaundry.featured.home.data.models.DailyStatistic
import com.almalaundry.featured.home.data.models.MonthlyStatistic

data class DashboardLaundryState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val monthlyData: List<MonthlyStatistic> = emptyList(),
    val dailyData: List<DailyStatistic> = emptyList(),
)

//package com.almalaundry.featured.home.presentation.state
//
//data class DashboardLaundryState()
