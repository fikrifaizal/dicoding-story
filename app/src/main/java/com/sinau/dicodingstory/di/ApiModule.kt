package com.sinau.dicodingstory.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.sinau.dicodingstory.data.local.AuthDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore("preferences")

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Singleton
    @Provides
    fun provideDataStore(@ApplicationContext applicationContext: Context): DataStore<Preferences> = applicationContext.dataStore

    @Singleton
    @Provides
    fun provideAuthPreference(dataStore: DataStore<Preferences>): AuthDataStore = AuthDataStore(dataStore)
}