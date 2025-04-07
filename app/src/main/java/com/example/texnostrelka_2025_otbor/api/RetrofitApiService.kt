package com.example.texnostrelka_2025_otbor.api

import com.example.texnostrelka_2025_otbor.models.NetworkModels.ComicsFromNetwork
import com.example.texnostrelka_2025_otbor.models.ComicsModel
import com.example.texnostrelka_2025_otbor.models.NetworkModels.ComicsNetworkModel
import com.example.texnostrelka_2025_otbor.models.authentication.AuthRequest
import com.example.texnostrelka_2025_otbor.models.authentication.AuthResponse
import com.example.texnostrelka_2025_otbor.models.authentication.RegisterationRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface RetrofitApiService {
    @POST("/api/user/auth")
    suspend fun authenticate(@Body request: AuthRequest) : AuthResponse

    @POST("/api/user/register")
    suspend fun registration(@Body request: RegisterationRequest) : AuthResponse

    @GET("/api/comics")
    suspend fun getComics(@Header("Authorization") token: String) : MutableList<ComicsNetworkModel>

    @GET("/api/comics/{id}")
    suspend fun getComicPages(@Path("id") id: String, @Header("Authorization") token: String) : ComicsFromNetwork

    @POST("/api/comics/")
    suspend fun postComics(@Header("Authorization") token: String, @Body request: ComicsFromNetwork)

    @GET("/api/mycomics")
    suspend fun getMyComics(@Header("Authorization") token: String) : MutableList<ComicsNetworkModel>

    @DELETE("/api/comics/{id}")
    suspend fun deleteComics(@Path("id") id: String, @Header("Authorization") token: String)
}