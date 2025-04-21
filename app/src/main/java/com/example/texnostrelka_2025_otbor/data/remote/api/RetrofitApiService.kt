package com.example.texnostrelka_2025_otbor.data.remote.api

import com.example.texnostrelka_2025_otbor.data.remote.model.comic.response.ComicsCoverNetworkModel
import com.example.texnostrelka_2025_otbor.data.remote.model.comic.ComicsNetworkModel
import com.example.texnostrelka_2025_otbor.data.remote.model.authentication.request.AuthRequestModel
import com.example.texnostrelka_2025_otbor.data.remote.model.authentication.response.AuthResponseModel
import com.example.texnostrelka_2025_otbor.data.remote.model.authentication.request.RegisterationRequestModel
import com.example.texnostrelka_2025_otbor.data.remote.model.image.request.ImageRequestModel
import com.example.texnostrelka_2025_otbor.data.remote.model.image.request.UpdateImageRequestModel
import com.example.texnostrelka_2025_otbor.data.remote.model.page.PageFromNetworkModel
import com.example.texnostrelka_2025_otbor.data.remote.model.page.request.PageAddRequestModel
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface RetrofitApiService {
    @POST("/api/user/auth")
    suspend fun authenticate(@Body request: AuthRequestModel) : AuthResponseModel

    @POST("/api/user/register")
    suspend fun registration(@Body request: RegisterationRequestModel) : AuthResponseModel

    @GET("/api/comics")
    suspend fun getComics(@Header("Authorization") token: String) : MutableList<ComicsNetworkModel>

    @GET("/api/comics/{id}")
    suspend fun getComicPages(@Path("id") id: String, @Header("Authorization") token: String) : ComicsCoverNetworkModel

    @POST("/api/comics/")
    suspend fun postComics(@Header("Authorization") token: String, @Body request: ComicsCoverNetworkModel)

    @GET("/api/mycomics")
    suspend fun getMyComics(@Header("Authorization") token: String) : MutableList<ComicsNetworkModel>

    @DELETE("/api/comics/{id}")
    suspend fun deleteComics(@Path("id") id: String, @Header("Authorization") token: String)

    @POST("/api/comics/pages/{comicsId}")
    suspend fun postPage(@Path("comicsId") comicsId: String, @Header("Authorization") token: String, @Body request: PageAddRequestModel)

    @DELETE("/api/comics/pages/{pageId}")
    suspend fun deletePage(@Path("pageId") pageId: String, @Header("Authorization") token: String)

    @POST("/api/comics/pages/images/{pageId}")
    suspend fun postImage(@Path("pageId") pageId: String, @Header("Authorization") token: String, @Body request: ImageRequestModel)

    @DELETE("/api/comics/pages/images/{imageId}")
    suspend fun deleteImage(@Path("imageId") imageId: String, @Header("Authorization") token: String)

    @PUT("/api/comics/pages/images/{imageId}")
    suspend fun updateImage(@Path("imageId") imageId: String, @Header("Authorization") token: String, @Body image: UpdateImageRequestModel)

    @GET("/api/comics/pages/{pageId}")
    suspend fun getPage(@Path("pageId") pageId: String, @Header("Authorization") token: String) : PageFromNetworkModel
}