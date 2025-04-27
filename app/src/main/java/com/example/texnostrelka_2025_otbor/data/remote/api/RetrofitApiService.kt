package com.example.texnostrelka_2025_otbor.data.remote.api

import com.example.texnostrelka_2025_otbor.data.remote.model.comic.ComicsNetworkModel
import com.example.texnostrelka_2025_otbor.data.remote.model.comic.ComicsCoverNetworkModel
import com.example.texnostrelka_2025_otbor.data.remote.model.authentication.request.AuthRequestModel
import com.example.texnostrelka_2025_otbor.data.remote.model.authentication.response.AuthResponseModel
import com.example.texnostrelka_2025_otbor.data.remote.model.authentication.request.RegisterationRequestModel
import com.example.texnostrelka_2025_otbor.data.remote.model.comic.ComicsInfoNetworkResponseModel
import com.example.texnostrelka_2025_otbor.data.remote.model.comment.request.CommentRequestModel
import com.example.texnostrelka_2025_otbor.data.remote.model.emotions.favorite.FavoriteResponseModel
import com.example.texnostrelka_2025_otbor.data.remote.model.emotions.like.LikeResponseModel
import com.example.texnostrelka_2025_otbor.data.remote.model.image.request.ImageRequestModel
import com.example.texnostrelka_2025_otbor.data.remote.model.image.request.UpdateImageRequestModel
import com.example.texnostrelka_2025_otbor.data.remote.model.page.PageFromNetworkModel
import com.example.texnostrelka_2025_otbor.data.remote.model.page.request.PageAddRequestModel
import com.example.texnostrelka_2025_otbor.data.remote.model.subscribe.SubscribeResponseModel
import com.example.texnostrelka_2025_otbor.data.remote.model.subscribe.SubscribeUsersResponseModel
import com.example.texnostrelka_2025_otbor.data.remote.model.user.InfoUserResponseModel
import com.example.texnostrelka_2025_otbor.data.remote.model.user.avatar.AvatarRequestModel
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
    suspend fun getComics(@Header("Authorization") token: String) : MutableList<ComicsCoverNetworkModel>

    @GET("/api/comics/{id}")
    suspend fun getComicPages(@Path("id") id: String, @Header("Authorization") token: String) : ComicsNetworkModel

    @POST("/api/comics/")
    suspend fun postComics(@Header("Authorization") token: String, @Body request: ComicsNetworkModel)

    @GET("/api/mycomics")
    suspend fun getMyComics(@Header("Authorization") token: String) : MutableList<ComicsCoverNetworkModel>

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

    @POST("/api/comics/{id}/like")
    suspend fun postLike(@Path("id") id: String, @Header("Authorization") token: String) : LikeResponseModel

    @POST("/api/comics/{id}/favorite")
    suspend fun postFavorite(@Path("id") id: String, @Header("Authorization") token: String) : FavoriteResponseModel

    @GET("/api/user/favorites")
    suspend fun getFavorites(@Header("Authorization") token: String) : MutableList<ComicsCoverNetworkModel>

    @POST("/api/comics/{comicsId}/comments")
    suspend fun postComment(@Path("comicsId") comicsId: String, @Header("Authorization") token: String, @Body request: CommentRequestModel)

    @DELETE("/api/comments/{commentId}")
    suspend fun deleteComment(@Path("commentId") commentId: String, @Header("Authorization") token: String)

    @GET("/api/comics/{comicsId}/info")
    suspend fun getInfoAboutComics(@Path("comicsId") comicsId: String, @Header("Authorization") token: String) : ComicsInfoNetworkResponseModel

    @GET("/api/users/{userId}")
    suspend fun getInfoAboutUser(@Path("userId") userId: String, @Header("Authorization") token: String) : InfoUserResponseModel

    @POST("/api/users/{userId}/subscribe")
    suspend fun postSubscribe(@Path("userId") userId: String, @Header("Authorization") token: String) : SubscribeResponseModel

    @GET("/api/users/{userId}/subscribers")
    suspend fun getUserSubscribers(@Path("userId") userId: String, @Header("Authorization") token: String) : MutableList<SubscribeUsersResponseModel>

    @GET("/api/users/{userId}/subscriptions")
    suspend fun getUserSubscriptions(@Path("userId") userId: String, @Header("Authorization") token: String) : MutableList<SubscribeUsersResponseModel>

    @POST("/api/user/avatar")
    suspend fun postUserAvatar(@Header("Authorization") token: String, @Body request: AvatarRequestModel)

    @DELETE("/api/user/avatar")
    suspend fun deleteUserAvatar(@Header("Authorization") token: String)
}