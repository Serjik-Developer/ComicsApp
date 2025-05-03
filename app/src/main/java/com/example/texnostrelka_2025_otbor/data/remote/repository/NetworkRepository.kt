package com.example.texnostrelka_2025_otbor.data.remote.repository

import com.example.texnostrelka_2025_otbor.data.remote.api.RetrofitApiService
import com.example.texnostrelka_2025_otbor.data.remote.exception.ApiException
import com.example.texnostrelka_2025_otbor.data.remote.exception.BadRequestException
import com.example.texnostrelka_2025_otbor.data.remote.exception.ConflictException
import com.example.texnostrelka_2025_otbor.data.remote.exception.ForbiddenException
import com.example.texnostrelka_2025_otbor.data.remote.exception.InvalidPasswordException
import com.example.texnostrelka_2025_otbor.data.remote.exception.NetworkException
import com.example.texnostrelka_2025_otbor.data.remote.exception.NotAuthorizedException
import com.example.texnostrelka_2025_otbor.data.remote.exception.NotFoundException
import com.example.texnostrelka_2025_otbor.data.remote.exception.TooManyRequests
import com.example.texnostrelka_2025_otbor.data.remote.model.comic.ComicsNetworkModel
import com.example.texnostrelka_2025_otbor.data.remote.model.comic.ComicsCoverNetworkModel
import com.example.texnostrelka_2025_otbor.data.remote.model.page.PageFromNetworkModel
import com.example.texnostrelka_2025_otbor.data.remote.model.authentication.request.AuthRequestModel
import com.example.texnostrelka_2025_otbor.data.remote.model.authentication.response.AuthResponseModel
import com.example.texnostrelka_2025_otbor.data.remote.model.authentication.request.RegisterationRequestModel
import com.example.texnostrelka_2025_otbor.data.remote.model.comic.ComicsInfoNetworkResponseModel
import com.example.texnostrelka_2025_otbor.data.remote.model.comment.request.CommentRequestModel
import com.example.texnostrelka_2025_otbor.data.remote.model.emotions.favorite.FavoriteResponseModel
import com.example.texnostrelka_2025_otbor.data.remote.model.emotions.like.LikeResponseModel
import com.example.texnostrelka_2025_otbor.data.remote.model.image.request.ImageRequestModel
import com.example.texnostrelka_2025_otbor.data.remote.model.image.request.UpdateImageRequestModel
import com.example.texnostrelka_2025_otbor.data.remote.model.page.request.PageAddRequestModel
import com.example.texnostrelka_2025_otbor.data.remote.model.subscribe.SubscribeResponseModel
import com.example.texnostrelka_2025_otbor.data.remote.model.subscribe.SubscribeUsersResponseModel
import com.example.texnostrelka_2025_otbor.data.remote.model.user.CurrentUserInfoResponseModel
import com.example.texnostrelka_2025_otbor.data.remote.model.user.InfoUserResponseModel
import com.example.texnostrelka_2025_otbor.data.remote.model.user.avatar.AvatarRequestModel
import com.example.texnostrelka_2025_otbor.data.remote.model.user.name.UpdateNameUserRequestModel
import com.example.texnostrelka_2025_otbor.data.remote.model.user.notification.NotificationSettingsModel
import com.example.texnostrelka_2025_otbor.data.remote.model.user.notification.UserNotificationTokenModel
import com.example.texnostrelka_2025_otbor.data.remote.model.user.password.UpdateUserPasswordRequestModel
import retrofit2.HttpException
import java.io.IOException

class NetworkRepository(private val apiService: RetrofitApiService) {

    suspend fun authenticate(login: String, password: String) : AuthResponseModel {
        try {
            return apiService.authenticate(AuthRequestModel(login, password))
        } catch (e: HttpException) {
            when(e.code()) {
                400 -> throw BadRequestException("Некорректный запрос: ${e.message}")
                401 -> throw NotAuthorizedException("Не верный пароль")
                404 -> throw NotFoundException("Пользователь не найден.")
                429 -> throw TooManyRequests("Исчерпано количество попыток. Попробуйте позже")
                else -> throw ApiException("Ошибка сервера ${e.code()}")
            }
        } catch (e: IOException) {
            throw NetworkException("Ошибка сети: ${e.message}")
        }
    }

    suspend fun registration(login: String, password: String, name: String) : AuthResponseModel {
        try {
            return apiService.registration(RegisterationRequestModel(login, password, name))
        } catch (e: HttpException) {
            when(e.code()) {
                400 -> throw BadRequestException("Некорректный запрос: ${e.message}")
                409 -> throw ConflictException("Пользователь уже существует.")
                else -> throw ApiException("Ошибка сервера ${e.code()}")
            }
        } catch (e: IOException) {
            throw NetworkException("Ошибка сети: ${e.message}")
        }

    }

    suspend fun getComics(token: String): MutableList<ComicsCoverNetworkModel> {
        try {
            return apiService.getComics("Bearer $token")
        } catch (e: HttpException) {
            when(e.code()) {
                400 -> throw BadRequestException("Некорректный запрос: ${e.message}")
                401 -> throw NotAuthorizedException("Не авторизован.")
                404 -> throw NotFoundException("Комикс не найден.")
                else -> throw ApiException("Ошибка сервера ${e.code()}")
            }
        } catch (e: IOException) {
            throw NetworkException("Ошибка сети: ${e.message}")
        }

    }

    suspend fun getComicById(id: String, token: String) : ComicsNetworkModel {
        try {
            return apiService.getComicPages(id, "Bearer $token")
        } catch (e: HttpException) {
            when(e.code()) {
                400 -> throw BadRequestException("Некорректный запрос: ${e.message}")
                401 -> throw NotAuthorizedException("Не авторизован.")
                404 -> throw NotFoundException("Комикс не найден.")
                else -> throw ApiException("Ошибка сервера ${e.code()}")
            }
        } catch (e: IOException) {
            throw NetworkException("Ошибка сети: ${e.message}")
        }
    }

    suspend fun getComicPages(id: String, token: String) : MutableList<PageFromNetworkModel>? {
        try {
            return apiService.getComicPages(id, "Bearer $token").pages
        } catch (e: HttpException) {
            when(e.code()) {
                400 -> throw BadRequestException("Некорректный запрос: ${e.message}")
                401 -> throw NotAuthorizedException("Не авторизован.")
                404 -> throw NotFoundException("Комиксы не найдены.")
                else -> throw ApiException("Ошибка сервера ${e.code()}")
            }
        } catch (e: IOException) {
            throw NetworkException("Ошибка сети: ${e.message}")
        }

    }

    suspend fun postComics(token: String, comics: ComicsNetworkModel)  {
        try {
            apiService.postComics("Bearer $token", comics)
        } catch (e: HttpException) {
            when(e.code()) {
                400 -> throw BadRequestException("Некорректный запрос: ${e.message}")
                401 -> throw NotAuthorizedException("Не авторизован.")
                409 -> throw ConflictException("Этот комикс уже существует!")
                else -> throw ApiException("Ошибка сервера ${e.code()}")
            }
        } catch (e: IOException) {
            throw NetworkException("Ошибка сети: ${e.message}")
        }
    }

    suspend fun getMyComics(token: String): MutableList<ComicsCoverNetworkModel> {
        try {
            return apiService.getMyComics("Bearer $token")
        } catch (e: HttpException) {
            when(e.code()) {
                400 -> throw BadRequestException("Некорректный запрос: ${e.message}")
                401 -> throw NotAuthorizedException("Не авторизован.")
                404 -> throw NotFoundException("Комиксы не найдены.")
                else -> throw ApiException("Ошибка сервера ${e.code()}")
            }
        } catch (e: IOException) {
            throw NetworkException("Ошибка сети: ${e.message}")
        }

    }

    suspend fun deleteComics(id: String, token: String) {
        try {
            apiService.deleteComics(id, "Bearer $token")
        } catch (e: HttpException) {
            when(e.code()) {
                401 -> throw NotAuthorizedException("Не авторизован.")
                403 -> throw ForbiddenException("Недостаточно прав.")
                404 -> throw NotFoundException("Комикс не найден")
                else -> throw ApiException("Ошибка сервера ${e.code()}")
            }
        } catch (e: IOException) {
            throw NetworkException("Ошибка сети: ${e.message}")
        }
    }

    suspend fun addPage(id: String, token: String, request: PageAddRequestModel) {
        try {
            apiService.postPage(id, "Bearer $token", request)
        } catch (e: HttpException) {
            when(e.code()) {
                401 -> throw NotAuthorizedException("Не авторизован.")
                403 -> throw ForbiddenException("Недостаточно прав.")
                404 -> throw NotFoundException("Комикс не найден")
                else -> throw ApiException("Ошибка сервера ${e.code()}")
            }
        } catch (e: IOException) {
            throw NetworkException("Ошибка сети: ${e.message}")
        }
    }

    suspend fun deletePage(pageId: String, token: String) {
        try {
            apiService.deletePage(pageId, "Bearer $token")
        } catch (e: HttpException) {
            when(e.code()) {
                401 -> throw NotAuthorizedException("Не авторизован.")
                403 -> throw ForbiddenException("Недостаточно прав.")
                404 -> throw NotFoundException("Страница не найдена")
                else -> throw ApiException("Ошибка сервера ${e.code()}")
            }
        } catch (e: IOException) {
            throw NetworkException("Ошибка сети: ${e.message}")
        }
    }

    suspend fun postImage(pageId: String, token: String, request: ImageRequestModel) {
        try {
            apiService.postImage(pageId, "Bearer $token", request)
        } catch (e: HttpException) {
            when(e.code()) {
                400 -> throw BadRequestException("Некорректный запрос: ${e.message}")
                401 -> throw NotAuthorizedException("Не авторизован.")
                403 -> throw ForbiddenException("Недостаточно прав.")
                404 -> throw NotFoundException("Страница не найдена.")
                else -> throw ApiException("Ошибка сервера ${e.code()}")
            }
        } catch (e: IOException) {
            throw NetworkException("Ошибка сети: ${e.message}")
        }
    }

    suspend fun deleteImage(imageId: String, token: String) {
        try {
            apiService.deleteImage(imageId, "Bearer $token")
        } catch (e: HttpException) {
            when(e.code()) {
                400 -> throw BadRequestException("Некорректный запрос: ${e.message}")
                401 -> throw NotAuthorizedException("Не авторизован.")
                403 -> throw ForbiddenException("Недостаточно прав.")
                404 -> throw NotFoundException("Изображение не найдено.")
                else -> throw ApiException("Ошибка сервера ${e.code()}")
            }
        } catch (e: IOException) {
            throw NetworkException("Ошибка сети: ${e.message}")
        }
    }

    suspend fun updateImage(imageId: String, token: String, request: String) {
        try {
            apiService.updateImage(imageId, "Bearer $token", UpdateImageRequestModel(request))
        } catch (e: HttpException) {
            when(e.code()) {
                400 -> throw BadRequestException("Некорректный запрос: ${e.message}")
                401 -> throw NotAuthorizedException("Не авторизован.")
                403 -> throw ForbiddenException("Недостаточно прав.")
                404 -> throw NotFoundException("Изображение не найдено.")
                else -> throw ApiException("Ошибка сервера ${e.code()}")
            }
        } catch (e: IOException) {
            throw NetworkException("Ошибка сети: ${e.message}")
        }
    }

    suspend fun getPage(pageId: String, token: String) : PageFromNetworkModel {
        try {
            return apiService.getPage(pageId, "Bearer $token")
        } catch (e: HttpException) {
            when(e.code()) {
                401 -> throw NotAuthorizedException("Не авторизован.")
                else -> throw ApiException("Ошибка сервера ${e.code()}")
            }
        } catch (e: IOException) {
            throw NetworkException("Ошибка сети: ${e.message}")
        }
    }

    suspend fun postLike(id: String, token: String) : LikeResponseModel {
        try {
            return apiService.postLike(id, "Bearer $token")
        } catch (e: HttpException) {
            when(e.code()) {
                400 -> throw BadRequestException("Некорректный запрос: ${e.message}")
                401 -> throw NotAuthorizedException("Не авторизован.")
                404 -> throw NotFoundException("Комикс не найден.")
                else -> throw ApiException("Ошибка сервера ${e.code()}")
            }
        } catch (e: IOException) {
            throw NetworkException("Ошибка сети: ${e.message}")
        }
    }

    suspend fun postFavorite(id: String, token: String) : FavoriteResponseModel {
        try {
            return apiService.postFavorite(id, "Bearer $token")
        } catch (e: HttpException) {
            when(e.code()) {
                400 -> throw BadRequestException("Некорректный запрос: ${e.message}")
                401 -> throw NotAuthorizedException("Не авторизован.")
                404 -> throw NotFoundException("Комикс не найден.")
                else -> throw ApiException("Ошибка сервера ${e.code()}")
            }
        } catch (e: IOException) {
            throw NetworkException("Ошибка сети: ${e.message}")
        }
    }

    suspend fun getFavorites(token: String): MutableList<ComicsCoverNetworkModel> {
        try {
            return apiService.getFavorites("Bearer $token")
        } catch (e: HttpException) {
            when(e.code()) {
                400 -> throw BadRequestException("Некорректный запрос: ${e.message}")
                401 -> throw NotAuthorizedException("Не авторизован.")
                404 -> throw NotFoundException("Комиксы не найдены.")
                else -> throw ApiException("Ошибка сервера ${e.code()}")
            }
        } catch (e: IOException) {
            throw NetworkException("Ошибка сети: ${e.message}")
        }
    }

    suspend fun postComment(comicsId: String, token: String, request: CommentRequestModel) {
        try {
            apiService.postComment(comicsId, "Bearer $token", request)
        } catch (e: HttpException) {
            when(e.code()) {
                400 -> throw BadRequestException("Некорректный запрос: ${e.message}")
                401 -> throw NotAuthorizedException("Не авторизован.")
                404 -> throw NotFoundException("Комикс не найден.")
                else -> throw ApiException("Ошибка сервера ${e.code()}")
            }
        } catch (e: IOException) {
            throw NetworkException("Ошибка сети: ${e.message}")
        }
    }

    suspend fun deleteComment(commentId: String, token: String) {
        try {
            apiService.deleteComment(commentId, "Bearer $token")
        } catch (e: HttpException) {
            when(e.code()) {
                400 -> throw BadRequestException("Некорректный запрос: ${e.message}")
                401 -> throw NotAuthorizedException("Не авторизован.")
                404 -> throw NotFoundException("Комментарий не найден.")
                else -> throw ApiException("Ошибка сервера ${e.code()}")
            }
        } catch (e: IOException) {
            throw NetworkException("Ошибка сети: ${e.message}")
        }
    }

    suspend fun getInfoAboutComics(comicsId: String, token: String) : ComicsInfoNetworkResponseModel {
        try {
            return apiService.getInfoAboutComics(comicsId, "Bearer $token")
        } catch (e: HttpException) {
            when(e.code()) {
                400 -> throw BadRequestException("Некорректный запрос: ${e.message}")
                401 -> throw NotAuthorizedException("Не авторизован.")
                404 -> throw NotFoundException("Комикс не найден.")
                else -> throw ApiException("Ошибка сервера ${e.code()}")
            }
        } catch (e: IOException) {
            throw NetworkException("Ошибка сети: ${e.message}")
        }
    }

    suspend fun getInfoAboutUser(userId: String, token: String) : InfoUserResponseModel {
        try {
            return apiService.getInfoAboutUser(userId, "Bearer $token")
        } catch (e: HttpException) {
            when(e.code()) {
                400 -> throw BadRequestException("Некорректный запрос: ${e.message}")
                401 -> throw NotAuthorizedException("Не авторизован.")
                404 -> throw NotFoundException("Пользователь не найден.")
                else -> throw ApiException("Ошибка сервера ${e.code()}")
            }
        } catch (e: IOException) {
            throw NetworkException("Ошибка сети: ${e.message}")
        }
    }

    suspend fun postSubscribe(userId: String, token: String) : SubscribeResponseModel {
        try {
            return apiService.postSubscribe(userId, "Bearer $token")
        } catch (e: HttpException) {
            when(e.code()) {
                400 -> throw BadRequestException("Нельзя подписаться на самого себя.")
                401 -> throw NotAuthorizedException("Не авторизован.")
                404 -> throw NotFoundException("Пользователь не найден.")
                else -> throw ApiException("Ошибка сервера ${e.code()}")
            }
        } catch (e: IOException) {
            throw NetworkException("Ошибка сети: ${e.message}")
        }
    }

    suspend fun getUserSubscribers(userId: String, token: String) : MutableList<SubscribeUsersResponseModel> {
        try {
            return apiService.getUserSubscribers(userId, "Bearer $token")
        } catch (e: HttpException) {
            when(e.code()) {
                400 -> throw BadRequestException("Некорректный запрос: ${e.message}")
                401 -> throw NotAuthorizedException("Не авторизован.")
                404 -> throw NotFoundException("Пользователь не найден.")
                else -> throw ApiException("Ошибка сервера ${e.code()}")
            }
        } catch (e: IOException) {
            throw NetworkException("Ошибка сети: ${e.message}")
        }
    }

    suspend fun getUserSubscriptions(userId: String, token: String) : MutableList<SubscribeUsersResponseModel> {
        try {
            return apiService.getUserSubscriptions(userId, "Bearer $token")
        } catch (e: HttpException) {
            when(e.code()) {
                400 -> throw BadRequestException("Некорректный запрос: ${e.message}")
                401 -> throw NotAuthorizedException("Не авторизован.")
                404 -> throw NotFoundException("Пользователь не найден.")
                else -> throw ApiException("Ошибка сервера ${e.code()}")
            }
        } catch (e: IOException) {
            throw NetworkException("Ошибка сети: ${e.message}")
        }
    }

    suspend fun postUserAvatar(token: String, avatar: String) {
        try {
            apiService.postUserAvatar("Bearer $token", AvatarRequestModel(avatar))
        } catch (e: HttpException) {
            when(e.code()) {
                400 -> throw BadRequestException("Некорректный запрос: ${e.message}")
                401 -> throw NotAuthorizedException("Не авторизован.")
                else -> throw ApiException("Ошибка сервера ${e.code()}")
            }
        } catch (e: IOException) {
            throw NetworkException("Ошибка сети: ${e.message}")
        }
    }

    suspend fun deleteUserAvatar(token: String) {
        try {
            apiService.deleteUserAvatar("Bearer $token")
        } catch (e: HttpException) {
            when(e.code()) {
                400 -> throw BadRequestException("Некорректный запрос: ${e.message}")
                401 -> throw NotAuthorizedException("Не авторизован.")
                else -> throw ApiException("Ошибка сервера ${e.code()}")
            }
        } catch (e: IOException) {
            throw NetworkException("Ошибка сети: ${e.message}")
        }
    }

    suspend fun getInfoAboutCurrentUser(token: String) : CurrentUserInfoResponseModel {
        try {
            return apiService.getInfoAboutCurrentUser("Bearer $token")
        } catch (e: HttpException) {
            when(e.code()) {
                400 -> throw BadRequestException("Некорректный запрос: ${e.message}")
                401 -> throw NotAuthorizedException("Не авторизован.")
                else -> throw ApiException("Ошибка сервера ${e.code()}")
            }
        } catch (e: IOException) {
            throw NetworkException("Ошибка сети: ${e.message}")
        }
    }

    suspend fun updatePassword(token: String, request: UpdateUserPasswordRequestModel) {
        try {
            apiService.updatePassword("Bearer $token", request)
        } catch (e: HttpException) {
            when(e.code()) {
                400 -> throw BadRequestException("Некорректный запрос: ${e.message}")
                401 -> throw InvalidPasswordException("Неправильный пароль")
                404 -> throw NotFoundException("Пользователь не найден")
                else -> throw ApiException("Ошибка сервера ${e.code()}")
            }
        } catch (e: IOException) {
            throw NetworkException("Ошибка сети: ${e.message}")
        }
    }

    suspend fun updateName(token: String, request: UpdateNameUserRequestModel) {
        try {
            apiService.updateName(token, request)
        } catch (e: HttpException) {
            when(e.code()) {
                400 -> throw BadRequestException("Некорректный запрос: ${e.message}")
                401 -> throw InvalidPasswordException("Не авторизован.")
                404 -> throw NotFoundException("Пользователь не найден")
                else -> throw ApiException("Ошибка сервера ${e.code()}")
            }
        } catch (e: IOException) {
            throw NetworkException("Ошибка сети: ${e.message}")
        }
    }

    suspend fun postNotificationToken(token: String, request: UserNotificationTokenModel) {
        try {
            apiService.postNotificationToken("Bearer $token", request)
        } catch (e: HttpException) {
            when(e.code()) {
                400 -> throw BadRequestException("Некорректный запрос: ${e.message}")
                401 -> throw InvalidPasswordException("Не авторизован.")
                404 -> throw NotFoundException("Пользователь не найден")
                else -> throw ApiException("Ошибка сервера ${e.code()}")
            }
        } catch (e: IOException) {
            throw NetworkException("Ошибка сети: ${e.message}")
        }
    }

    suspend fun updateNotificationSettings(token: String, request: NotificationSettingsModel) {
        try {
            apiService.updateNotificationSettings("Bearer $token", request)
        } catch (e: HttpException) {
            when(e.code()) {
                400 -> throw BadRequestException("Некорректный запрос: ${e.message}")
                401 -> throw InvalidPasswordException("Не авторизован.")
                404 -> throw NotFoundException("Пользователь не найден")
                else -> throw ApiException("Ошибка сервера ${e.code()}")
            }
        } catch (e: IOException) {
            throw NetworkException("Ошибка сети: ${e.message}")
        }
    }
}