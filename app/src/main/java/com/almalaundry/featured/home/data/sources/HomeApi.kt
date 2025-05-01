package com.almalaundry.featured.home.data.sources

import com.almalaundry.featured.home.data.models.ApiResponse
import com.almalaundry.featured.home.data.models.DailyStatistic
import com.almalaundry.featured.home.data.models.MonthlyStatistic
import retrofit2.http.GET
import retrofit2.http.Query

interface HomeApi {
    @GET("/api/orders/statistics?period=daily")
    suspend fun getDailyStatistics(
        @Query("days") days: Int
    ): ApiResponse<List<DailyStatistic>>

    @GET("/api/orders/statistics?period=monthly")
    suspend fun getMonthlyStatistics(
        @Query("months") months: Int
    ): ApiResponse<List<MonthlyStatistic>>
}

//package com.almalaundry.featured.home.data.sources
//
//interface HomeApi {
//}