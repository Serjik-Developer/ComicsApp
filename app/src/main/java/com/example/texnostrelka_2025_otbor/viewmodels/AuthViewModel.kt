package com.example.texnostrelka_2025_otbor.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.texnostrelka_2025_otbor.database.PreferencesManager
import com.example.texnostrelka_2025_otbor.models.authentication.AuthResponse
import com.example.texnostrelka_2025_otbor.repositories.NetworkRepository
import kotlinx.coroutines.launch

class AuthViewModel(
    private val networkRepository: NetworkRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {
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
            } catch (e: NetworkRepository.BadRequestException) {
                _error.postValue("Некорректные данные. Проверьте ввод.")
            } catch (e: NetworkRepository.NetworkException) {
                _error.postValue("Проблемы с интернетом. Проверьте соединение.")
            } catch (e: NetworkRepository.NotAuthorizedException) {
                _error.postValue("Неверный логин или пароль")
            } catch (e: NetworkRepository.NotFoundException) {
                _error.postValue("Пользователь не найден")
            } catch (e: Exception) {
                _error.postValue("Произошла неизвестная ошибка")
                e.printStackTrace()
            }
        }
    }
}