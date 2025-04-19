package com.example.texnostrelka_2025_otbor.presentation.ui.editpagenetwork

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.texnostrelka_2025_otbor.data.local.preferences.PreferencesManager
import com.example.texnostrelka_2025_otbor.data.remote.exception.ApiException
import com.example.texnostrelka_2025_otbor.data.remote.exception.BadRequestException
import com.example.texnostrelka_2025_otbor.data.remote.exception.ForbiddenException
import com.example.texnostrelka_2025_otbor.data.remote.exception.NetworkException
import com.example.texnostrelka_2025_otbor.data.remote.exception.NotAuthorizedException
import com.example.texnostrelka_2025_otbor.data.remote.exception.NotFoundException
import com.example.texnostrelka_2025_otbor.data.remote.model.page.PageFromNetwork
import com.example.texnostrelka_2025_otbor.data.remote.repository.NetworkRepository
import kotlinx.coroutines.launch

class EditPageNetworkViewModel(private val repository: NetworkRepository, private val preferencesManager: PreferencesManager) : ViewModel() {
    private val _page = MutableLiveData<PageFromNetwork>()
    val page: LiveData<PageFromNetwork> get() = _page
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error
    private val _success = MutableLiveData<String?>()
    val success: LiveData<String?> get() = _success
    private val _refreshTrigger = MutableLiveData<Boolean>()
    val refreshTrigger: LiveData<Boolean> = _refreshTrigger

    fun fetchPage(pageId: String) {
        viewModelScope.launch {
            _error.value = null
            try {
                val token = preferencesManager.getAuthToken()
                if (token.isNullOrEmpty()) {
                    _error.value = "Не авторизован."
                    return@launch
                }
                val result = repository.getPage(pageId, token)
                _page.value = result
            } catch (e: NotAuthorizedException) {
                preferencesManager.clearAuthToken()
                preferencesManager.clearName()
                _error.value = e.message
            } catch (e: NetworkException) {
                _error.value = "Проблемы с интернетом"
            } catch (e: Exception) {
                Log.e("EditPageNetworkViewModel", "Unknown error", e)
            }
        }
    }

    fun deleteImage(imageId: String) {
        viewModelScope.launch {
            try {
                val token = preferencesManager.getAuthToken()
                if (token.isNullOrEmpty()) {
                    _error.value = "Не авторизован."
                    return@launch
                }
                repository.deleteImage(imageId, token)
                val currentList = _page.value?.images?.toMutableList() ?: mutableListOf()
                currentList.removeAll { it.id == imageId }
                _success.value = "Успешно удалено!"
                _refreshTrigger.postValue(true)
            } catch (e: NotAuthorizedException) {
                preferencesManager.clearAuthToken()
                preferencesManager.clearName()
                _error.value = e.message
            } catch (e: ForbiddenException) {
                _error.value = e.message
            } catch (e: BadRequestException) {
                _error.value = "Ошибка запроса: ${e.message}"
            } catch (e: NotFoundException) {
                _error.value = e.message
            } catch (e: NetworkException) {
                _error.value = "Проблемы с интернетом"
            } catch (e: Exception) {
                Log.e("EditPageNetworkViewModel", "Unknown error", e)
            }
        }
    }
    fun resetRefreshTrigger() {
        _refreshTrigger.postValue(false)
    }
}