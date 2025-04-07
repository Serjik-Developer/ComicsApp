package com.example.texnostrelka_2025_otbor.data.remote.repository

import com.example.texnostrelka_2025_otbor.data.remote.api.RetrofitApiService
import com.example.texnostrelka_2025_otbor.data.remote.exception.ApiException
import com.example.texnostrelka_2025_otbor.data.remote.exception.BadRequestException
import com.example.texnostrelka_2025_otbor.data.remote.exception.ConflictException
import com.example.texnostrelka_2025_otbor.data.remote.exception.ForbiddenException
import com.example.texnostrelka_2025_otbor.data.remote.exception.NetworkException
import com.example.texnostrelka_2025_otbor.data.remote.exception.NotAuthorizedException
import com.example.texnostrelka_2025_otbor.data.remote.exception.NotFoundException
import com.example.texnostrelka_2025_otbor.data.remote.model.ComicsFromNetwork
import com.example.texnostrelka_2025_otbor.data.remote.model.ComicsNetworkModel
import com.example.texnostrelka_2025_otbor.data.remote.model.PageFromNetwork
import com.example.texnostrelka_2025_otbor.data.remote.model.authentication.AuthRequest
import com.example.texnostrelka_2025_otbor.data.remote.model.authentication.AuthResponse
import com.example.texnostrelka_2025_otbor.data.remote.model.authentication.RegisterationRequest
import com.example.texnostrelka_2025_otbor.data.remote.model.pagenetwork.PageAddRequestModel
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class NetworkRepository {

    private val apiService: RetrofitApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://comicsapp-backend.onrender.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RetrofitApiService::class.java)
    }

    suspend fun authenticate(login: String, password: String) : AuthResponse {
        try {
            return apiService.authenticate(AuthRequest(login, password))
        } catch (e: HttpException) {
            when(e.code()) {
                400 -> throw BadRequestException("Некорректный запрос: ${e.message}")
                401 -> throw NotAuthorizedException("Не верный пароль")
                404 -> throw NotFoundException("Пользователь не найден.")
                else -> throw ApiException("Ошибка сервера ${e.code()}")
            }
        } catch (e: IOException) {
            throw NetworkException("Ошибка сети: ${e.message}")
        }
    }

    suspend fun registration(login: String, password: String, name: String) : AuthResponse {
        try {
            return apiService.registration(RegisterationRequest(login, password, name))
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

    suspend fun getComics(token: String): MutableList<ComicsNetworkModel> {
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

    suspend fun getComicById(id: String, token: String) : ComicsFromNetwork {
        return apiService.getComicPages(id, "Bearer $token")
    }

    suspend fun getComicPages(id: String, token: String) : MutableList<PageFromNetwork>? {
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

    suspend fun postComics(token: String, comics: ComicsFromNetwork)  {
        try {
            apiService.postComics("Bearer $token", comics)
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

    suspend fun getMyComics(token: String): MutableList<ComicsNetworkModel> {
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
}