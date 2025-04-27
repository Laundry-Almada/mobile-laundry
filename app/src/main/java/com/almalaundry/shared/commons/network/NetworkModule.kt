package com.almalaundry.shared.commons.network

import com.almalaundry.featured.profile.data.remote.ApiService
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
import javax.inject.Named
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    @Named("Authenticated")
    fun provideAuthenticatedOkHttpClient(sessionManagerProvider: Provider<SessionManager>): OkHttpClient {
        return OkHttpClient.Builder().addInterceptor { chain ->
            val sessionManager = sessionManagerProvider.get()
            val token = runBlocking { sessionManager.getToken() }

            val request = chain.request().newBuilder()
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .apply {
                    token?.let { addHeader("Authorization", "Bearer $it") }
                }.build()

            chain.proceed(request)
        }.build()
    }

    @Provides
    @Singleton
    @Named("Public")
    fun providePublicOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .build()
                chain.proceed(request)
            }.build()
    }

    @Provides
    @Singleton
    @Named("Authenticated")
    fun provideAuthenticatedRetrofit(@Named("Authenticated") client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @Named("Public")
    fun providePublicRetrofit(@Named("Public") client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @Named("Authenticated")
    fun provideAuthenticatedNetworkApi(@Named("Authenticated") retrofit: Retrofit): NetworkApi {
        return object : NetworkApi {
            override val retrofit: Retrofit = retrofit
            override val httpClient: OkHttpClient = retrofit.callFactory() as OkHttpClient
        }
    }

    @Provides
    @Singleton
    @Named("Public")
    fun providePublicNetworkApi(@Named("Public") retrofit: Retrofit): NetworkApi {
        return object : NetworkApi {
            override val retrofit: Retrofit = retrofit
            override val httpClient: OkHttpClient = retrofit.callFactory() as OkHttpClient
        }
    }

    @Provides
    @Singleton
    fun provideApiService(@Named("Authenticated") retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}