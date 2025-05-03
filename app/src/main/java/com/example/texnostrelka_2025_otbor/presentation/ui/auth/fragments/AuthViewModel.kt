package com.example.texnostrelka_2025_otbor.presentation.ui.auth.fragments

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.texnostrelka_2025_otbor.data.local.preferences.PreferencesManager
import com.example.texnostrelka_2025_otbor.data.remote.exception.BadRequestException
import com.example.texnostrelka_2025_otbor.data.remote.exception.InvalidPasswordException
import com.example.texnostrelka_2025_otbor.data.remote.exception.NetworkException
import com.example.texnostrelka_2025_otbor.data.remote.exception.NotAuthorizedException
import com.example.texnostrelka_2025_otbor.data.remote.exception.NotFoundException
import com.example.texnostrelka_2025_otbor.data.remote.exception.TooManyRequests
import com.example.texnostrelka_2025_otbor.data.remote.model.user.notification.UserNotificationTokenModel
import com.example.texnostrelka_2025_otbor.data.remote.repository.NetworkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val networkRepository: NetworkRepository,
    private val preferencesManager: PreferencesManager
): ViewModel() {
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    private val _authSuccess = MutableLiveData<Boolean>()
    val authSuccess: LiveData<Boolean> get() = _authSuccess

    fun authenticate(login: String, password: String) {
        viewModelScope.launch {
            _error.value = null
            try {
                val response = networkRepository.authenticate(login, password)
                preferencesManager.saveAuthToken(response.token)
                preferencesManager.saveName(response.name)
                _authSuccess.postValue(true)
            } catch (e: BadRequestException) {
                _error.postValue("Некорректные данные. Проверьте ввод.")
            } catch (e: NetworkException) {
                _error.postValue("Проблемы с интернетом. Проверьте соединение.")
            } catch (e: NotAuthorizedException) {
                _error.postValue("Неверный логин или пароль")
            } catch (e: NotFoundException) {
                _error.postValue("Пользователь не найден")
            } catch (e: TooManyRequests) {
                _error.postValue(e.message)
            } catch (e: Exception) {
                _error.postValue("Произошла неизвестная ошибка")
                e.printStackTrace()
            }
        }
    }
    fun postNotificationToken(fcmToken: String) {
        viewModelScope.launch {
            _error.value = null
            try {
                val token = preferencesManager.getAuthToken()
                if (token.isNullOrEmpty()) {
                    _error.value = "Не авторизован."
                    return@launch
                }
                networkRepository.postNotificationToken(token, UserNotificationTokenModel(fcmToken))
            } catch (e: NotAuthorizedException) {
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
}