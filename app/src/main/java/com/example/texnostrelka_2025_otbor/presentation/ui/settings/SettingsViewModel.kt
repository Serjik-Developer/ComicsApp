package com.example.texnostrelka_2025_otbor.presentation.ui.settings

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.texnostrelka_2025_otbor.data.local.preferences.PreferencesManager
import com.example.texnostrelka_2025_otbor.data.remote.exception.ForbiddenException
import com.example.texnostrelka_2025_otbor.data.remote.exception.NetworkException
import com.example.texnostrelka_2025_otbor.data.remote.exception.NotAuthorizedException
import com.example.texnostrelka_2025_otbor.data.remote.exception.NotFoundException
import com.example.texnostrelka_2025_otbor.data.remote.model.user.CurrentUserInfoResponseModel
import com.example.texnostrelka_2025_otbor.data.remote.repository.NetworkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(private val repository: NetworkRepository, private val preferencesManager: PreferencesManager) : ViewModel() {
    private val _userData = MutableLiveData<CurrentUserInfoResponseModel>()
    val userData : LiveData<CurrentUserInfoResponseModel> get() = _userData
    private val _error = MutableLiveData<String?>()
    val error : LiveData<String?> get() = _error

    fun fetchUserData() {
        viewModelScope.launch {
            try {
                val token = preferencesManager.getAuthToken()
                if(token.isNullOrEmpty()) {
                    _error.value = "Не авторизован."
                    return@launch
                }
                val response = repository.getInfoAboutCurrentUser(token)
                _userData.value = response
            } catch (e: NotAuthorizedException) {
                preferencesManager.clearName()
                preferencesManager.clearAuthToken()
                _error.value = e.message
            } catch (e: ForbiddenException) {
                _error.value = e.message
            } catch (e: NotFoundException) {
                _error.value = e.message
            } catch (e: NetworkException) {
                _error.value = "Проблемы с интернетом"
            } catch (e: Exception) {
                _error.value = "Неизвестная ошибка"
                Log.e("SettingsViewModel", "Unknown error", e)
            }
        }
    }

    fun postNewAvatar(avatar: String) {
        viewModelScope.launch {
            try {
                val token = preferencesManager.getAuthToken()
                if(token.isNullOrEmpty()) {
                    _error.value = "Не авторизован."
                    return@launch
                }
                repository.postUserAvatar(token, avatar)
            } catch (e: NotAuthorizedException) {
                preferencesManager.clearName()
                preferencesManager.clearAuthToken()
                _error.value = e.message
            } catch (e: ForbiddenException) {
                _error.value = e.message
            } catch (e: NotFoundException) {
                _error.value = e.message
            } catch (e: NetworkException) {
                _error.value = "Проблемы с интернетом"
            } catch (e: Exception) {
                _error.value = "Неизвестная ошибка"
                Log.e("SettingsViewModel", "Unknown error", e)
            }
        }
    }

    fun deleteUserAvatar() {
        viewModelScope.launch {
            try {
                val token = preferencesManager.getAuthToken()
                if(token.isNullOrEmpty()) {
                    _error.value = "Не авторизован."
                    return@launch
                }
                repository.deleteUserAvatar(token)
            } catch (e: NotAuthorizedException) {
                preferencesManager.clearName()
                preferencesManager.clearAuthToken()
                _error.value = e.message
            } catch (e: ForbiddenException) {
                _error.value = e.message
            } catch (e: NotFoundException) {
                _error.value = e.message
            } catch (e: NetworkException) {
                _error.value = "Проблемы с интернетом"
            } catch (e: Exception) {
                _error.value = "Неизвестная ошибка"
                Log.e("SettingsViewModel", "Unknown error", e)
            }
        }
    }
}