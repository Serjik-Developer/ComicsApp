package com.example.texnostrelka_2025_otbor.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.texnostrelka_2025_otbor.database.PreferencesManager
import com.example.texnostrelka_2025_otbor.models.NetworkModels.ComicsNetworkModel
import com.example.texnostrelka_2025_otbor.repositories.NetworkRepository
import com.example.texnostrelka_2025_otbor.repositories.NetworkRepository.NotAuthorizedException
import kotlinx.coroutines.launch

class MyComicsViewModel(
    private val networkRepository: NetworkRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {
    private val _comics = MutableLiveData<MutableList<ComicsNetworkModel>>()
    val comics : LiveData<MutableList<ComicsNetworkModel>> get() = _comics

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> get() = _error

    private val _deleteSuccess = MutableLiveData<Boolean>()
    val deleteSuccess: LiveData<Boolean> get() = _deleteSuccess

    init {
        fetchComics()
    }
    fun deleteComics(id: String) {
        viewModelScope.launch {
            _error.value = null
            try {
                val token = preferencesManager.getAuthToken()
                if(token.isNullOrEmpty()) {
                    _error.value = "Не авторизован."
                    return@launch
                }
                networkRepository.deleteComics(id, token)
                val currentList = _comics.value?.toMutableList() ?: mutableListOf()
                currentList.removeAll { it.id == id }
                _comics.value = currentList
                _deleteSuccess.postValue(true)
            } catch (e: NotAuthorizedException) {
                _error.value = e.message
            } catch (e: NetworkRepository.ForbiddenException) {
                _error.value = e.message
            } catch (e: NetworkRepository.NotFoundException) {
                _error.value = e.message
            } catch (e: NetworkRepository.NetworkException) {
                _error.value = "Проблемы с интернетом"
            } catch (e: Exception) {
                _error.value = "Неизвестная ошибка"
                Log.e("MyComicsViewModel", "Unknown error", e)
            }
        }
    }
    fun fetchComics() {
        viewModelScope.launch {
            try {
                val token = preferencesManager.getAuthToken()
                if(token.isNullOrEmpty()) {
                    _error.value = "Не авторизован."
                    return@launch
                }
                val result = networkRepository.getMyComics(token)
                _comics.value = result
            } catch (e: NetworkRepository.NotAuthorizedException) {
                _error.value = "Не авторизован."
                preferencesManager.clearName()
                preferencesManager.clearAuthToken()
            } catch (e: NetworkRepository.BadRequestException) {
                _error.value = "Ошибка запроса: ${e.message}"
            } catch (e: NetworkRepository.NetworkException) {
                _error.value = "Проблемы с интернетом"
            } catch (e: NetworkRepository.NotFoundException) {
                _error.value = "Комиксы не найдены"
            } catch (e: Exception) {
                _error.value = "Неизвестная ошибка"
                Log.e("MyComicsViewModel", "Unknown error", e)
            }
        }
    }
}