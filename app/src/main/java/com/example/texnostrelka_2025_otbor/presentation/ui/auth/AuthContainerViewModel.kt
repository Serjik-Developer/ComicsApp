package com.example.texnostrelka_2025_otbor.presentation.ui.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.texnostrelka_2025_otbor.data.local.preferences.PreferencesManager
import com.example.texnostrelka_2025_otbor.data.remote.exception.InvalidPasswordException
import com.example.texnostrelka_2025_otbor.data.remote.exception.NetworkException
import com.example.texnostrelka_2025_otbor.data.remote.exception.NotAuthorizedException
import com.example.texnostrelka_2025_otbor.data.remote.exception.NotFoundException
import com.example.texnostrelka_2025_otbor.data.remote.model.user.notification.UserNotificationTokenModel
import com.example.texnostrelka_2025_otbor.data.remote.repository.NetworkRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class AuthContainerViewModel @Inject constructor(private val preferencesManager: PreferencesManager, private val repository: NetworkRepository) : ViewModel() {
    private val _error = MutableLiveData<String?>()
    val error : LiveData<String?> get() = _error
    fun postNotificationToken(fcmToken: String) {
        viewModelScope.launch {
            _error.value = null
            try {
                val token = preferencesManager.getAuthToken()
                if (token.isNullOrEmpty()) {
                    _error.value = "Не авторизован."
                    return@launch
                }
                repository.postNotificationToken(token, UserNotificationTokenModel(fcmToken))
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