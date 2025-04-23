package com.example.texnostrelka_2025_otbor.data.remote.repository

import com.example.texnostrelka_2025_otbor.data.remote.api.RetrofitApiService
import com.example.texnostrelka_2025_otbor.data.remote.exception.ApiException
import com.example.texnostrelka_2025_otbor.data.remote.exception.BadRequestException
import com.example.texnostrelka_2025_otbor.data.remote.exception.ConflictException
import com.example.texnostrelka_2025_otbor.data.remote.exception.ForbiddenException
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
import com.example.texnostrelka_2025_otbor.data.remote.model.image.request.ImageRequestModel
import com.example.texnostrelka_2025_otbor.data.remote.model.image.request.UpdateImageRequestModel
import com.example.texnostrelka_2025_otbor.data.remote.model.page.request.PageAddRequestModel
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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
        return apiService.getComicPages(id, "Bearer $token")
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
}