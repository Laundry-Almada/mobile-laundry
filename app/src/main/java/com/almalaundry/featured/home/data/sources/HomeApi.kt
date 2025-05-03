package com.almalaundry.featured.home.data.sources

import com.almalaundry.featured.home.data.models.ApiResponse
import com.almalaundry.featured.home.data.models.DailyStatistic
import com.almalaundry.featured.home.data.models.MonthlyStatistic
import retrofit2.http.GET
import retrofit2.http.Query

interface HomeApi {
    @GET("orders/statistics?period=daily")
    suspend fun getDailyStatistics(): ApiResponse<List<DailyStatistic>>

    @GET("orders/statistics?period=monthly&months=6")
    suspend fun getMonthlyStatistics(): ApiResponse<List<MonthlyStatistic>>
}

