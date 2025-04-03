package com.example.texnostrelka_2025_otbor.repositories

import android.util.Log
import com.example.texnostrelka_2025_otbor.api.RetrofitApiService
import com.example.texnostrelka_2025_otbor.models.ComicsFromNetwork
import com.example.texnostrelka_2025_otbor.models.ComicsModel
import com.example.texnostrelka_2025_otbor.models.ComicsNetworkModel
import com.example.texnostrelka_2025_otbor.models.authentication.AuthRequest
import com.example.texnostrelka_2025_otbor.models.authentication.AuthResponse
import com.example.texnostrelka_2025_otbor.models.authentication.RegisterationRequest
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
    suspend fun registration(login: String, password: String, name: String) : AuthResponse {
        return apiService.registration(RegisterationRequest(login, password, name))
    }
    suspend fun getComics(token: String): MutableList<ComicsNetworkModel> {
        return try {
            val data = apiService.getComics("Bearer $token")
            Log.d("NetworkRepository", "Received ${data.size} comics")
            Log.d("NetworkRepository", "First comic: ${data.firstOrNull()}")
            data
        } catch (e: Exception) {
            Log.e("NetworkRepository", "Error getting comics", e)
            mutableListOf()
        }
    }
    suspend fun getComicPages(id: String, token: String) : ComicsFromNetwork {
        return apiService.getComicPages(id, token)
    }
}