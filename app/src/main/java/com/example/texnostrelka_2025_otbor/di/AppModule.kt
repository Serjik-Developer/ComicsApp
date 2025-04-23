package com.example.texnostrelka_2025_otbor.di

import android.content.Context
import com.example.texnostrelka_2025_otbor.data.local.database.ComicsDatabase
import com.example.texnostrelka_2025_otbor.data.local.preferences.PreferencesManager
import com.example.texnostrelka_2025_otbor.data.remote.api.RetrofitApiService
import com.example.texnostrelka_2025_otbor.data.remote.repository.NetworkRepository
import com.example.texnostrelka_2025_otbor.domain.repository.ComicsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRepository(apiService: RetrofitApiService) : NetworkRepository {
        return NetworkRepository(apiService)
    }

    @Provides
    @Singleton
    fun providePreferencesManager(@ApplicationContext context: Context) : PreferencesManager {
        return PreferencesManager(context)
    }

    @Provides
    @Singleton
    fun provideComicsDatabase(@ApplicationContext context: Context) : ComicsDatabase {
        return ComicsDatabase(context)
    }

    @Provides
    @Singleton
    fun provideComicsRepository(comicsDatabase: ComicsDatabase) : ComicsRepository {
        return ComicsRepository(comicsDatabase)
    }

    @Provides
    @Singleton
    fun provideRetrofitApiService() : RetrofitApiService {
        return Retrofit.Builder()
            .baseUrl("https://comicsapp-backend.onrender.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RetrofitApiService::class.java)
    }
}