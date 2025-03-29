package com.example.texnostrelka_2025_otbor.repositories

import com.example.texnostrelka_2025_otbor.api.RetrofitApiService
import com.example.texnostrelka_2025_otbor.models.AuthRequest
import com.example.texnostrelka_2025_otbor.models.AuthResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NetworkRepository {
    private val apiService: RetrofitApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://comicsapp-backend.onrender.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RetrofitApiService::class.java)
    }

    suspend fun authenticate(login: String, password: String) : AuthResponse {
        return apiService.authenticate(AuthRequest(login, password))
    }
}