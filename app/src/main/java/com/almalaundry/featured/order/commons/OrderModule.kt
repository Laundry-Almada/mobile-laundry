package com.almalaundry.featured.order.commons

import com.almalaundry.featured.order.data.source.OrderApi
import com.almalaundry.shared.commons.network.NetworkApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object OrderModule {
    @Provides
    @Singleton
    fun provideOrderApi(networkApi: NetworkApi): OrderApi {
        return networkApi.retrofit.create(OrderApi::class.java)
    }
}
//    @Provides
//    @Singleton
//    fun provideOrderRepository(
//        orderApi: OrderApi
//    ): OrderRepository {
//        return OrderRepositoryImpl(orderApi)
//    }


//retrofit.create(OrderApi::class.java)