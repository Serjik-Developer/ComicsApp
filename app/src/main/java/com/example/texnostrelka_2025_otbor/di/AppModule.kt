package com.example.texnostrelka_2025_otbor.di

import android.content.Context
import com.example.texnostrelka_2025_otbor.data.local.preferences.PreferencesManager
import com.example.texnostrelka_2025_otbor.data.remote.repository.NetworkRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRepository() : NetworkRepository {
        return NetworkRepository()
    }

    @Provides
    @Singleton
    fun providePreferencesManager(@ApplicationContext context: Context) : PreferencesManager {
        return PreferencesManager(context)
    }
}