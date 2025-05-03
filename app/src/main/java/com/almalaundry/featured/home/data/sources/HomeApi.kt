package com.almalaundry.featured.home.data.sources

import com.almalaundry.featured.home.data.models.ApiResponse
import com.almalaundry.featured.home.data.models.DailyStatistic
import com.almalaundry.featured.home.data.models.MonthlyStatistic
import retrofit2.http.GET

interface HomeApi {
    @GET("admin/orders/statistics?period=daily")
    suspend fun getDailyStatistics(): ApiResponse<List<DailyStatistic>>

    @GET("admin/orders/statistics?period=monthly&months=6")
    suspend fun getMonthlyStatistics(): ApiResponse<List<MonthlyStatistic>>
}

