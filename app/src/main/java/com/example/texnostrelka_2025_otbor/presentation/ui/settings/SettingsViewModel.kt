package com.example.texnostrelka_2025_otbor.presentation.ui.settings

import android.media.session.MediaSession.Token
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.texnostrelka_2025_otbor.data.local.preferences.PreferencesManager
import com.example.texnostrelka_2025_otbor.data.remote.exception.ForbiddenException
import com.example.texnostrelka_2025_otbor.data.remote.exception.InvalidPasswordException
import com.example.texnostrelka_2025_otbor.data.remote.exception.NetworkException
import com.example.texnostrelka_2025_otbor.data.remote.exception.NotAuthorizedException
import com.example.texnostrelka_2025_otbor.data.remote.exception.NotFoundException
import com.example.texnostrelka_2025_otbor.data.remote.model.user.CurrentUserInfoResponseModel
import com.example.texnostrelka_2025_otbor.data.remote.model.user.name.UpdateNameUserRequestModel
import com.example.texnostrelka_2025_otbor.data.remote.model.user.notification.UserNotificationTokenModel
import com.example.texnostrelka_2025_otbor.data.remote.model.user.password.UpdateUserPasswordRequestModel
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
    private val _postAvatarSuccess = MutableLiveData<Boolean>()
    val postAvatarSuccess : LiveData<Boolean> get() = _postAvatarSuccess
    private val _deleteAvatarSuccess = MutableLiveData<Boolean>()
    val deleteAvatarSuccess : LiveData<Boolean> get() = _deleteAvatarSuccess
    private val _changeSuccess = MutableLiveData<Boolean>()
    val changeSuccess : LiveData<Boolean> get() = _changeSuccess
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
                _postAvatarSuccess.postValue(true)
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
                _deleteAvatarSuccess.postValue(true)
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

    fun changePassword(currentPassword: String, newPassword: String) {
        viewModelScope.launch {
            try {
                val token = preferencesManager.getAuthToken()
                if (token.isNullOrEmpty()) {
                    _error.value = "Не авторизован."
                    return@launch
                }
                repository.updatePassword(token, UpdateUserPasswordRequestModel(currentPassword, newPassword))
                _changeSuccess.value = true
            }catch (e: NotAuthorizedException) {
                preferencesManager.clearName()
                preferencesManager.clearAuthToken()
                _error.value = e.message
            } catch (e: InvalidPasswordException) {
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

    fun updateName(newName: String) {
        viewModelScope.launch {
            try {
                val token = preferencesManager.getAuthToken()
                if(token.isNullOrEmpty()) {
                    _error.value = "Не авторизован."
                    return@launch
                }
                repository.updateName(token, UpdateNameUserRequestModel(newName))
                _changeSuccess.value = true
            }catch (e: NotAuthorizedException) {
                preferencesManager.clearName()
                preferencesManager.clearAuthToken()
                _error.value = e.message
            } catch (e: InvalidPasswordException) {
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
    fun logOut() {
        preferencesManager.clearName()
        preferencesManager.clearAuthToken()
        _error.value = "Не авторизован."
    }

    fun resetDeleteAvatarSuccess() {
        _deleteAvatarSuccess.postValue(false)
    }

    fun resetPostAvatarSuccess() {
        _postAvatarSuccess.postValue(false)
    }

    fun resetChangeSuccess() {
        _changeSuccess.postValue(false)
    }
}