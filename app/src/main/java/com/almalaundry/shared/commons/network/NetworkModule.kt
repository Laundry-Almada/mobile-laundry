package com.almalaundry.shared.commons.network

import com.almalaundry.shared.commons.config.BuildConfig
import com.almalaundry.shared.commons.session.SessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideOkHttpClient(sessionManagerProvider: Provider<SessionManager>): OkHttpClient {
        return OkHttpClient.Builder().addInterceptor { chain ->
            val sessionManager = sessionManagerProvider.get()
            val token = runBlocking { sessionManager.getToken() }
            val request =
                chain.request().newBuilder().addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json").apply {
                        token?.let { addHeader("Authorization", "Bearer $it") }
                    }.build()
            chain.proceed(request)
        }.build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder().baseUrl(BuildConfig.BASE_URL).client(client)
            .addConverterFactory(GsonConverterFactory.create()).build()
    }

    @Provides
    @Singleton
    fun provideNetworkApi(retrofit: Retrofit): NetworkApi {
        return object : NetworkApi {
            override val retrofit: Retrofit = retrofit
            override val httpClient: OkHttpClient = retrofit.callFactory() as OkHttpClient
        }
    }
}