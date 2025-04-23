package com.example.texnostrelka_2025_otbor.presentation.ui.main.fragments.viewmodels

import android.database.sqlite.SQLiteConstraintException
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
import com.example.texnostrelka_2025_otbor.data.remote.model.comic.ComicsCoverNetworkModel
import com.example.texnostrelka_2025_otbor.data.remote.repository.NetworkRepository
import com.example.texnostrelka_2025_otbor.domain.repository.ComicsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewNetworkViewModel @Inject constructor(
    private val networkRepository: NetworkRepository,
    private val preferencesManager: PreferencesManager,
    private val repository: ComicsRepository
) : ViewModel() {
    private val _comics = MutableLiveData<MutableList<ComicsCoverNetworkModel>>(mutableListOf())
    val comics: LiveData<MutableList<ComicsCoverNetworkModel>> get() = _comics
    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> get() = _error
    private val _downloadSuccess = MutableLiveData<Boolean>()
    val downloadSuccess : LiveData<Boolean> get() = _downloadSuccess

    init {
        fetchComics()
    }

    fun fetchComics() {
        viewModelScope.launch {
            _error.value = null
            try {
                val token = preferencesManager.getAuthToken()
                if (token.isNullOrEmpty()) {
                    _error.value = "Не авторизован."
                    return@launch
                }
                val result = networkRepository.getComics(token)
                _comics.value = result

                if (result.isEmpty()) {
                    _error.value = "Комиксы не найдены!"
                }
            } catch (e: NotAuthorizedException) {
                _error.value = "Не авторизован."
                preferencesManager.clearName()
                preferencesManager.clearAuthToken()
            } catch (e: BadRequestException) {
                _error.value = "Ошибка запроса: ${e.message}"
            } catch (e: NetworkException) {
                _error.value = "Проблемы с интернетом"
            } catch (e: NotFoundException) {
                _error.value = "Комиксы не найдены"
            } catch (e: Exception) {
                _error.value = "Неизвестная ошибка"
                Log.e("ViewNetworkViewModel", "Unknown error", e)
            }
        }
    }

    fun downloadComic(id: String) {
        viewModelScope.launch {
            _downloadSuccess.postValue(false)
            _error.value = null
            try {
                val token = preferencesManager.getAuthToken()
                if (token.isNullOrEmpty()) {
                    _error.value = "Не авторизован."
                    return@launch
                }
                val comic = networkRepository.getComicById(id, token)
                if (comic == null) {
                    _error.value = "Комикс не найден"
                }
                repository.downloadComicFromNetwork(comic)
                _downloadSuccess.postValue(true)
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
            } catch (e: SQLiteConstraintException) {
                _error.value = "Комикс уже скачан!"
            } catch (e: Exception) {
                _error.value = "Неизвестная ошибка"
                Log.e("ViewNetworkViewModel", "Unknown error", e)
            }
        }
    }
    fun resetDownloadSuccess() {
        _downloadSuccess.postValue(false)
    }
}