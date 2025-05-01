package com.almalaundry.featured.home.commons

import com.almalaundry.featured.home.data.sources.HomeApi
import com.almalaundry.shared.commons.network.NetworkApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HomeModule {
    @Provides
    @Singleton
    @Named("Authenticated")
    fun provideAuthenticatedHomeApi(@Named("Authenticated") networkApi: NetworkApi): HomeApi {
        return networkApi.retrofit.create(HomeApi::class.java)
    }
}