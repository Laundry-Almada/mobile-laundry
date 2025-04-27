package com.almalaundry.featured.order.commons

import com.almalaundry.featured.order.data.source.OrderApi
import com.almalaundry.shared.commons.network.NetworkApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object OrderModule {
    @Provides
    @Singleton
    @Named("Authenticated")
    fun provideAuthenticatedOrderApi(@Named("Authenticated") networkApi: NetworkApi): OrderApi {
        return networkApi.retrofit.create(OrderApi::class.java)
    }

    @Provides
    @Singleton
    @Named("Public")
    fun providePublicOrderApi(@Named("Public") networkApi: NetworkApi): OrderApi {
        return networkApi.retrofit.create(OrderApi::class.java)
    }
}