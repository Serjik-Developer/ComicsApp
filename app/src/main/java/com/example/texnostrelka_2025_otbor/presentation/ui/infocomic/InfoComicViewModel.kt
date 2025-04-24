package com.example.texnostrelka_2025_otbor.presentation.ui.infocomic

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.texnostrelka_2025_otbor.data.local.preferences.PreferencesManager
import com.example.texnostrelka_2025_otbor.data.remote.exception.BadRequestException
import com.example.texnostrelka_2025_otbor.data.remote.exception.NetworkException
import com.example.texnostrelka_2025_otbor.data.remote.exception.NotAuthorizedException
import com.example.texnostrelka_2025_otbor.data.remote.exception.NotFoundException
import com.example.texnostrelka_2025_otbor.data.remote.model.comic.ComicsInfoNetworkResponseModel
import com.example.texnostrelka_2025_otbor.data.remote.model.comment.request.CommentRequestModel
import com.example.texnostrelka_2025_otbor.data.remote.repository.NetworkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InfoComicViewModel @Inject constructor(private val repository: NetworkRepository, private val preferencesManager: PreferencesManager) : ViewModel(){
    private val _comics = MutableLiveData<ComicsInfoNetworkResponseModel>()
    val comics : LiveData<ComicsInfoNetworkResponseModel> get() = _comics
    private val _error = MutableLiveData<String?>()
    val error : LiveData<String?> get() = _error
    private val _success = MutableLiveData<String?>()
    val success : LiveData<String?> get() = _success

    fun fetchInfo(comicsId: String) {
        viewModelScope.launch {
            _error.value = null
            try {
                val token = preferencesManager.getAuthToken()
                if (token.isNullOrEmpty()) {
                    _error.value = "Не авторизован."
                    return@launch
                }
                val result = repository.getInfoAboutComics(comicsId, token)
                _comics.value = result
            } catch (e: NotAuthorizedException) {
                _error.value = "Не авторизован."
                preferencesManager.clearName()
                preferencesManager.clearAuthToken()
            } catch (e: BadRequestException) {
                _error.value = "Некорректный запрос"
            } catch (e: NetworkException) {
                _error.value = "Проблемы с интернетом"
            } catch (e: NotFoundException) {
                _error.value = "Комикс не найден"
            } catch (e: Exception) {
                _error.value = "Неизвестная ошибка"
                Log.e("MyComicsViewModel", "Unknown error", e)
            }
        }
    }

    fun postComment(text: String, comicsId: String) {
        viewModelScope.launch {
            _error.value = null
            try {
                val token = preferencesManager.getAuthToken()
                if (token.isNullOrEmpty()) {
                    _error.value = "Не авторизован."
                    return@launch
                }
                val request = CommentRequestModel(text)
                repository.postComment(comicsId, token, request)
            } catch (e: NotAuthorizedException) {
                _error.value = "Не авторизован."
                preferencesManager.clearName()
                preferencesManager.clearAuthToken()
            } catch (e: BadRequestException) {
                _error.value = "Некорректный запрос"
            } catch (e: NetworkException) {
                _error.value = "Проблемы с интернетом"
            } catch (e: NotFoundException) {
                _error.value = "Комикс не найден"
            } catch (e: Exception) {
                _error.value = "Неизвестная ошибка"
                Log.e("MyComicsViewModel", "Unknown error", e)
            }
        }
    }

    fun deleteComment(commentId: String) {
        _error.value = null
        _success.value = null
        viewModelScope.launch {
            try {
                val token = preferencesManager.getAuthToken()
                if (token.isNullOrEmpty()) {
                    _error.value = "Не авторизован."
                    return@launch
                }
                repository.deleteComment(commentId, token)
                _success.value = "Успешно удалено!"
            } catch (e: NotAuthorizedException) {
                _error.value = "Не авторизован."
                preferencesManager.clearName()
                preferencesManager.clearAuthToken()
            } catch (e: BadRequestException) {
                _error.value = "Некорректный запрос"
            } catch (e: NetworkException) {
                _error.value = "Проблемы с интернетом"
            } catch (e: NotFoundException) {
                _error.value = "Комикс не найден"
            } catch (e: Exception) {
                _error.value = "Неизвестная ошибка"
                Log.e("MyComicsViewModel", "Unknown error", e)
            }
        }
    }
}