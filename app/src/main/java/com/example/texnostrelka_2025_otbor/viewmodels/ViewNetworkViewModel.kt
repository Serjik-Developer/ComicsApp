package com.example.texnostrelka_2025_otbor.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.texnostrelka_2025_otbor.database.PreferencesManager
import com.example.texnostrelka_2025_otbor.models.NetworkModels.ComicsNetworkModel
import com.example.texnostrelka_2025_otbor.repositories.NetworkRepository
import com.example.texnostrelka_2025_otbor.repositories.NetworkRepository.BadRequestException
import com.example.texnostrelka_2025_otbor.repositories.NetworkRepository.NotFoundException
import kotlinx.coroutines.launch

class ViewNetworkViewModel(
    private val networkRepository: NetworkRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {
    private val _comics = MutableLiveData<MutableList<ComicsNetworkModel>>(mutableListOf())
    val comics: LiveData<MutableList<ComicsNetworkModel>> get() = _comics
    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> get() = _error

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
                Log.e("ViewNetworkViewModel", "Unknown error", e)
            }
        }
    }
}