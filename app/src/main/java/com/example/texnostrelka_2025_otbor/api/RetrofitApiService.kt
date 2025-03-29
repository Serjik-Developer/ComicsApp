package com.example.texnostrelka_2025_otbor.api

import com.example.texnostrelka_2025_otbor.models.AuthRequest
import com.example.texnostrelka_2025_otbor.models.AuthResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface RetrofitApiService {
    @POST("/api/user/auth")
    suspend fun authenticate(@Body request: AuthRequest) : AuthResponse
}