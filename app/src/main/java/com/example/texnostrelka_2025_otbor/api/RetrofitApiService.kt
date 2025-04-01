package com.example.texnostrelka_2025_otbor.api

import com.example.texnostrelka_2025_otbor.models.authentication.AuthRequest
import com.example.texnostrelka_2025_otbor.models.authentication.AuthResponse
import com.example.texnostrelka_2025_otbor.models.authentication.RegisterationRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface RetrofitApiService {
    @POST("/api/user/auth")
    suspend fun authenticate(@Body request: AuthRequest) : AuthResponse

    @POST("/api/user/register")
    suspend fun registration(@Body request: RegisterationRequest) : AuthResponse
}