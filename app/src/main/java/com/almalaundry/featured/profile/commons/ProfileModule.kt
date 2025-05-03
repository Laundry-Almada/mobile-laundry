package com.almalaundry.featured.profile.commons

import com.almalaundry.featured.profile.data.remote.ProfileApi
import com.almalaundry.shared.commons.network.NetworkApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProfileModule {
    @Provides
    @Singleton
    @Named("Authenticated")
    fun provideAuthenticatedProfileApi(@Named("Authenticated") networkApi: NetworkApi): ProfileApi {
        return networkApi.retrofit.create(ProfileApi::class.java)
    }

    @Provides
    @Singleton
    @Named("Public")
    fun providePublicProfileApi(@Named("Public") networkApi: NetworkApi): ProfileApi {
        return networkApi.retrofit.create(ProfileApi::class.java)
    }
}