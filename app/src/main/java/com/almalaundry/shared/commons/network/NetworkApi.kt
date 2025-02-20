package com.almalaundry.shared.commons.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit

interface NetworkApi {
    val retrofit: Retrofit
    val httpClient: OkHttpClient
}
