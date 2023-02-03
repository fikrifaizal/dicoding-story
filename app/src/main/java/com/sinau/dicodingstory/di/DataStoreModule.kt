package com.sinau.dicodingstory.di

import com.sinau.dicodingstory.data.remote.api.ApiConfig
import com.sinau.dicodingstory.data.remote.api.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Singleton
    @Provides
    fun provideApiService(): ApiService = ApiConfig.getApiService()
}