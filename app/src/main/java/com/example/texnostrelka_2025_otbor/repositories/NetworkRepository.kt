package com.example.texnostrelka_2025_otbor.repositories

import android.util.Log
import com.example.texnostrelka_2025_otbor.api.RetrofitApiService
import com.example.texnostrelka_2025_otbor.models.NetworkModels.ComicsFromNetwork
import com.example.texnostrelka_2025_otbor.models.ComicsModel
import com.example.texnostrelka_2025_otbor.models.NetworkModels.ComicsNetworkModel
import com.example.texnostrelka_2025_otbor.models.NetworkModels.PageFromNetwork
import com.example.texnostrelka_2025_otbor.models.authentication.AuthRequest
import com.example.texnostrelka_2025_otbor.models.authentication.AuthResponse
import com.example.texnostrelka_2025_otbor.models.authentication.RegisterationRequest
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

    class NotAuthorizedException(message: String) : Exception(message)
    class BadRequestException(message: String) : Exception(message)
    class ApiException(message: String) : Exception(message)
    class NetworkException(message: String) : Exception(message)
    class NotFoundException(message: String) : Exception(message)
    class ConflictException(message: String) : Exception(message)
}