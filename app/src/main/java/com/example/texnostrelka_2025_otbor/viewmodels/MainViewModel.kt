package com.example.texnostrelka_2025_otbor.viewmodelslist

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.texnostrelka_2025_otbor.database.PreferencesManager
import com.example.texnostrelka_2025_otbor.models.ComicsModel
import com.example.texnostrelka_2025_otbor.models.NetworkModels.ComicsFromNetwork
import com.example.texnostrelka_2025_otbor.repositories.ComicsRepository
import com.example.texnostrelka_2025_otbor.repositories.NetworkRepository
import kotlinx.coroutines.launch
import java.util.UUID

class MainViewModel(private val repository: ComicsRepository, private val networkRepository: NetworkRepository, private val preferencesManager: PreferencesManager) : ViewModel() {
    private val _comics = MutableLiveData<MutableList<ComicsModel>>()
    val comics: LiveData<MutableList<ComicsModel>> get() = _comics

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage : LiveData<String?> get() = _errorMessage
    init {
        fetchComics()
    }
    fun fetchComics() {
        viewModelScope.launch {
            val result = repository.getComics()
            _comics.value = result
        }
    }
    fun addComics(text: String, description: String) {
        viewModelScope.launch {
            val id = UUID.randomUUID().toString()
            repository.insertComics(id, text, description)
            fetchComics()
        }
    }
    fun deleteComics(id: String) {
        viewModelScope.launch {
            repository.deleteComics(id)
            fetchComics()
        }
    }
    fun postComics(id: String) {
        viewModelScope.launch {
            _errorMessage.value = null
            try {
                val token = preferencesManager.getAuthToken()
                if (token.isNullOrEmpty()) {
                    _errorMessage.value = "Не авторизован."
                    return@launch
                }
                networkRepository.postComics(token, repository.getComicsById(id))
            }
            catch (e: Exception)
            {
                _errorMessage.value = "Неизвестная ошибка"
            }
            catch (e: NetworkRepository.NotAuthorizedException) {
                _errorMessage.value = "Не авторизован."
                preferencesManager.clearName()
                preferencesManager.clearAuthToken()
            } catch (e: NetworkRepository.BadRequestException) {
                _errorMessage.value = "Ошибка запроса: ${e.message}"
            } catch (e: NetworkRepository.NetworkException) {
                _errorMessage.value = "Проблемы с интернетом"
            }
        }
    }
}