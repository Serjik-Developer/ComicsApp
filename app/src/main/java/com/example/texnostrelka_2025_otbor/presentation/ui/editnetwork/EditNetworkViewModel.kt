package com.example.texnostrelka_2025_otbor.presentation.ui.editnetwork

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.texnostrelka_2025_otbor.data.local.preferences.PreferencesManager
import com.example.texnostrelka_2025_otbor.data.remote.exception.ForbiddenException
import com.example.texnostrelka_2025_otbor.data.remote.exception.NetworkException
import com.example.texnostrelka_2025_otbor.data.remote.exception.NotAuthorizedException
import com.example.texnostrelka_2025_otbor.data.remote.exception.NotFoundException
import com.example.texnostrelka_2025_otbor.data.remote.model.PageFromNetwork
import com.example.texnostrelka_2025_otbor.data.remote.model.pagenetwork.PageAddRequestModel
import com.example.texnostrelka_2025_otbor.data.remote.repository.NetworkRepository
import kotlinx.coroutines.launch

class EditNetworkViewModel(private val networkRepository: NetworkRepository, private val preferencesManager: PreferencesManager) : ViewModel() {
    private val _pages = MutableLiveData<MutableList<PageFromNetwork>>()
    val pages : LiveData<MutableList<PageFromNetwork>> get() = _pages

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    private val _success = MutableLiveData<String?>()
    val success : LiveData<String?> get() = _success

    fun addPage(comicsId: String, rows: Int, columns: Int) {
        viewModelScope.launch {
            _error.value = null
            try {
                val token = preferencesManager.getAuthToken()
                if (token.isNullOrEmpty()) {
                    _error.value = "Не авторизован."
                    return@launch
                }
                networkRepository.addPage(comicsId, token, PageAddRequestModel(rows, columns))
                _success.postValue("Успешно добавлено!")
            } catch (e: NotAuthorizedException) {
                _error.value = e.message
                preferencesManager.clearName()
                preferencesManager.clearAuthToken()
            } catch (e: ForbiddenException) {
                _error.value = e.message
            } catch (e: NotFoundException) {
                _error.value = e.message
            } catch (e: NetworkException) {
                _error.value = "Проблемы с интернетом"
            } catch (e: Exception) {
                _error.value = "Неизвестная ошибка"
                Log.e("EditNetworkViewModel", "Unknown error", e)
            }
        }
    }
    fun deletePage(pageId: String) {
        viewModelScope.launch {
            _error.value = null
            try {
                val token = preferencesManager.getAuthToken()
                if (token.isNullOrEmpty()) {
                    _error.value = "Не авторизован."
                    return@launch
                }
                networkRepository.deletePage(pageId, token)
                _success.postValue("Успешно удалено!")
            }catch (e: NotAuthorizedException) {
                _error.value = e.message
                preferencesManager.clearName()
                preferencesManager.clearAuthToken()
            } catch (e: ForbiddenException) {
                _error.value = e.message
            } catch (e: NotFoundException) {
                _error.value = e.message
            } catch (e: NetworkException) {
                _error.value = "Проблемы с интернетом"
            } catch (e: Exception) {
                _error.value = "Неизвестная ошибка"
                Log.e("EditNetworkViewModel", "Unknown error", e)
            }
        }
    }
}