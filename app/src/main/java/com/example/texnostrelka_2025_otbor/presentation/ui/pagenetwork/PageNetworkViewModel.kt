package com.example.texnostrelka_2025_otbor.presentation.ui.pagenetwork

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
import com.example.texnostrelka_2025_otbor.data.remote.model.PageFromNetwork
import com.example.texnostrelka_2025_otbor.data.remote.repository.NetworkRepository
import kotlinx.coroutines.launch

class PageNetworkViewModel(private val networkRepository: NetworkRepository, private val preferencesManager: PreferencesManager) : ViewModel() {
    private val _pages = MutableLiveData<MutableList<PageFromNetwork>>()
    val pages : LiveData<MutableList<PageFromNetwork>> get() = _pages
    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> get() = _error


    fun fetchPages(id: String) {
        viewModelScope.launch {
            _error.value = null
            try {
                val token = preferencesManager.getAuthToken()
                if (token.isNullOrEmpty()) {
                    _error.value = "Не авторизован."
                    return@launch
                }
                val result = networkRepository.getComicPages(id, token)
                _pages.value = result!!
            } catch (e: BadRequestException) {
                _error.value = "Ошибка запроса: ${e.message}"
            } catch (e: NotAuthorizedException) {
                _error.value = "Не авторизован."
                preferencesManager.clearName()
                preferencesManager.clearAuthToken()
            } catch (e: NotFoundException) {
                _error.value = "Комикс не найден."
            } catch (e: NetworkException) {
                _error.value = "Проблемы с интернетом"
            } catch (e: Exception) {
                _error.value = "Неизвестная ошибка"
                Log.e("ViewNetworkViewModel", "Unknown error", e)
            }
        }
    }
}