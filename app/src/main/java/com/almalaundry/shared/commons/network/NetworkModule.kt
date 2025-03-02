package com.almalaundry.shared.commons.network

import com.almalaundry.featured.auth.data.repositories.AuthRepository
import com.almalaundry.shared.commons.config.BuildConfig
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
    fun provideOkHttpClient(authRepositoryProvider: Provider<AuthRepository>): OkHttpClient {
        return OkHttpClient.Builder().addInterceptor { chain ->
            val authRepository = authRepositoryProvider.get() // Ambil instance saat runtime
            val token = runBlocking { authRepository.getToken() } // Ambil token
            val request =
                chain.request().newBuilder().addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json").apply {
                        if (token != null) {
                            addHeader("Authorization", "Bearer $token")
                        }
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