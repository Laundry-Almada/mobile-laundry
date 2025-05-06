package com.almalaundry.shared.commons.network

import com.almalaundry.shared.commons.config.BuildConfig
import com.almalaundry.shared.commons.session.SessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    @Named("Authenticated")
    fun provideAuthenticatedOkHttpClient(
        sessionManagerProvider: Provider<SessionManager>,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val sessionManager = sessionManagerProvider.get()
                val token = runBlocking { sessionManager.getToken() }

                val request = chain.request().newBuilder()
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .apply {
                        token?.let { addHeader("Authorization", "Bearer $it") }
                    }.build()

                chain.proceed(request)
            }
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    @Named("Public")
    fun providePublicOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .build()
                chain.proceed(request)
            }
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
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
}