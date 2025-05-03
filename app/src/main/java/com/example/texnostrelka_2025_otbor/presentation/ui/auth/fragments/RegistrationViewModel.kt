package com.example.texnostrelka_2025_otbor.presentation.ui.auth.fragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.texnostrelka_2025_otbor.data.local.preferences.PreferencesManager
import com.example.texnostrelka_2025_otbor.data.remote.exception.BadRequestException
import com.example.texnostrelka_2025_otbor.data.remote.exception.ConflictException
import com.example.texnostrelka_2025_otbor.data.remote.exception.NetworkException
import com.example.texnostrelka_2025_otbor.data.remote.repository.NetworkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val networkRepository: NetworkRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel(){
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    private val _authSuccess = MutableLiveData<Boolean>()
    val authSuccess: LiveData<Boolean> get() = _authSuccess

    fun registrate(login: String, password: String, name: String) {
        viewModelScope.launch {
            _error.value = null
            try {
                val response = networkRepository.registration(login, password, name)
                preferencesManager.saveAuthToken(response.token)
                preferencesManager.saveName(response.name)
                _authSuccess.postValue(true)
            } catch (e: BadRequestException) {
                _error.postValue("Некорректные данные. Проверьте ввод.")
            } catch (e: NetworkException) {
                _error.postValue("Проблемы с интернетом. Проверьте соединение.")
            } catch (e: ConflictException) {
                _error.postValue("Пользователь уже существует.")
            } catch (e: Exception) {
                _error.postValue("Произошла неизвестная ошибка")
                e.printStackTrace()
            }
        }
    }
}