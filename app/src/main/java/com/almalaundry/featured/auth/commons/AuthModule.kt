package com.almalaundry.featured.auth.commons

import com.almalaundry.featured.auth.data.source.AuthApi
import com.almalaundry.shared.commons.network.NetworkApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {
    @Provides
    @Singleton
    fun provideAuthApi(networkApi: NetworkApi): AuthApi {
        return networkApi.retrofit.create(AuthApi::class.java)
    }
}