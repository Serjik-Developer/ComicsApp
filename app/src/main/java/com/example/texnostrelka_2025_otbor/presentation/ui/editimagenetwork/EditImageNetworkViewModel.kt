package com.example.texnostrelka_2025_otbor.presentation.ui.editimagenetwork

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.texnostrelka_2025_otbor.data.local.preferences.PreferencesManager
import com.example.texnostrelka_2025_otbor.data.remote.exception.BadRequestException
import com.example.texnostrelka_2025_otbor.data.remote.exception.ForbiddenException
import com.example.texnostrelka_2025_otbor.data.remote.exception.NetworkException
import com.example.texnostrelka_2025_otbor.data.remote.exception.NotAuthorizedException
import com.example.texnostrelka_2025_otbor.data.remote.exception.NotFoundException
import com.example.texnostrelka_2025_otbor.data.remote.model.image.request.ImageRequestModel
import com.example.texnostrelka_2025_otbor.data.remote.repository.NetworkRepository
import kotlinx.coroutines.launch

class EditImageNetworkViewModel(private val repository: NetworkRepository, private val preferencesManager: PreferencesManager) : ViewModel() {

    private val _error = MutableLiveData<String?>()
    val error : LiveData<String?> get() = _error
    private val _success = MutableLiveData<String?>()
    val success : LiveData<String?> get() = _success

    fun addImage(pageId: String, request: ImageRequestModel) {
        viewModelScope.launch {
            try {
                val token = preferencesManager.getAuthToken()
                if (token.isNullOrEmpty()) {
                    _error.postValue("Не авторизован.")
                    return@launch
                }
                repository.postImage(pageId, "Bearer $token", request)
                _success.postValue("Изображение успешно загружено!")
            } catch (e: BadRequestException) {
                _error.value = e.message
            } catch (e: NotAuthorizedException) {
                preferencesManager.clearAuthToken()
                preferencesManager.clearName()
                _error.value = e.message
            } catch (e: ForbiddenException) {
                _error.value = e.message
            } catch (e: NotFoundException) {
                _error.value = e.message
            } catch (e: NetworkException) {
                _error.value = "Проблемы с интернетом"
            } catch (e: Exception) {
                _error.value = "Незивестная ошибка"
                Log.w("EditImageNetworkViewModel", "Unknown error", e)
            }
        }
    }

    fun updateImage(imageId: String, request: String) {
        viewModelScope.launch {
            try {
                val token = preferencesManager.getAuthToken()
                if (token.isNullOrEmpty()) {
                    _error.postValue("Не авторизован.")
                    return@launch
                }
                repository.updateImage(imageId, "Bearer $token", request)
                _success.postValue("Изображение успешно обновлено!")
            } catch (e: NotAuthorizedException) {
                preferencesManager.clearAuthToken()
                preferencesManager.clearName()
                _error.value = e.message
            } catch (e: ForbiddenException) {
                _error.value = e.message
            } catch (e: NotFoundException) {
                _error.value = e.message
            } catch (e: NetworkException) {
                _error.value = "Проблемы с интернетом"
            } catch (e: Exception) {
                _error.value = "Незивестная ошибка"
                Log.w("EditImageNetworkViewModel", "Unknown error", e)
            }
        }
    }
}