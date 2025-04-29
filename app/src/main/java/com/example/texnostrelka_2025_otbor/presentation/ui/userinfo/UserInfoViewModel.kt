package com.example.texnostrelka_2025_otbor.presentation.ui.userinfo

import android.util.Log
import androidx.compose.ui.platform.textInputServiceFactory
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.VIEW_MODEL_STORE_OWNER_KEY
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.texnostrelka_2025_otbor.data.local.preferences.PreferencesManager
import com.example.texnostrelka_2025_otbor.data.remote.exception.BadRequestException
import com.example.texnostrelka_2025_otbor.data.remote.exception.NetworkException
import com.example.texnostrelka_2025_otbor.data.remote.exception.NotAuthorizedException
import com.example.texnostrelka_2025_otbor.data.remote.exception.NotFoundException
import com.example.texnostrelka_2025_otbor.data.remote.model.subscribe.SubscribeUsersResponseModel
import com.example.texnostrelka_2025_otbor.data.remote.model.user.InfoUserResponseModel
import com.example.texnostrelka_2025_otbor.data.remote.repository.NetworkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserInfoViewModel @Inject constructor(private val repository: NetworkRepository,private val preferencesManager: PreferencesManager) : ViewModel() {
    private val _userData = MutableLiveData<InfoUserResponseModel>()
    val userData: LiveData<InfoUserResponseModel> get() = _userData
    private val _error = MutableLiveData<String?>()
    val error : LiveData<String?> get() = _error
    private val _isSubscribed = MutableLiveData<Boolean>()
    val isSubscribed : LiveData<Boolean> get() = _isSubscribed
    private val _subscribeUsers = MutableLiveData<MutableList<SubscribeUsersResponseModel>>()
    val subscribeUsers : LiveData<MutableList<SubscribeUsersResponseModel>> get() = _subscribeUsers

    fun fecthUserdata(userId: String) {
        _error.value = null
        viewModelScope.launch {
            try {
                val token = preferencesManager.getAuthToken()
                if(token.isNullOrEmpty()){
                    _error.value = "Не авторизован."
                    return@launch
                }
                _userData.value = repository.getInfoAboutUser(userId, token)
            } catch (e: NotAuthorizedException) {
                _error.value = "Не авторизован."
                preferencesManager.clearName()
                preferencesManager.clearAuthToken()
            } catch (e: BadRequestException) {
                _error.value = "Некорректный запрос"
            } catch (e: NetworkException) {
                _error.value = "Проблемы с интернетом"
            } catch (e: NotFoundException) {
                _error.value = "Пользователь не найден"
            } catch (e: Exception) {
                _error.value = "Неизвестная ошибка"
                Log.e("InfoComicViewModel", "Unknown error", e)
            }
        }
    }

    fun postSubscribe(userId: String) {
        _error.value = null
        viewModelScope.launch {
            try {
                val token = preferencesManager.getAuthToken()
                if(token.isNullOrEmpty()) {
                    _error.value = "Не авторизован."
                    return@launch
                }
                _isSubscribed.value = repository.postSubscribe(userId, token).subscribed
            } catch (e: NotAuthorizedException) {
                _error.value = "Не авторизован."
                preferencesManager.clearName()
                preferencesManager.clearAuthToken()
            } catch (e: BadRequestException) {
                _error.value = "Нельзя подписаться на самого себя"
            } catch (e: NetworkException) {
                _error.value = "Проблемы с интернетом"
            } catch (e: NotFoundException) {
                _error.value = "Пользователь не найден"
            } catch (e: Exception) {
                _error.value = "Неизвестная ошибка"
                Log.e("InfoComicViewModel", "Unknown error", e)
            }
        }
    }

    fun fetchSubscribersUsers(userId: String) {
        _error.value = null
        viewModelScope.launch {
            try {
                val token = preferencesManager.getAuthToken()
                if (token.isNullOrEmpty()) {
                    _error.value = "Не авторизован."
                    return@launch
                }
                val response = repository.getUserSubscribers(userId, token)
                _subscribeUsers.postValue(response)
            } catch (e: NotAuthorizedException) {
                _error.value = "Не авторизован."
                preferencesManager.clearName()
                preferencesManager.clearAuthToken()
            } catch (e: BadRequestException) {
                _error.value = "Некорректный запрос"
            } catch (e: NetworkException) {
                _error.value = "Проблемы с интернетом"
            } catch (e: NotFoundException) {
                _error.value = "Пользователь не найден"
            } catch (e: Exception) {
                _error.value = "Неизвестная ошибка"
                Log.e("InfoComicViewModel", "Unknown error", e)
            }
        }
    }

    fun fetchSubscriptionsUsers(userId: String) {
        _error.value = null
        viewModelScope.launch {
            try {
                val token = preferencesManager.getAuthToken()
                if (token.isNullOrEmpty()) {
                    _error.value = "Не авторизован."
                    return@launch
                }
                val response = repository.getUserSubscriptions(userId, token)
                _subscribeUsers.postValue(response)
            } catch (e: NotAuthorizedException) {
                _error.value = "Не авторизован."
                preferencesManager.clearName()
                preferencesManager.clearAuthToken()
            } catch (e: BadRequestException) {
                _error.value = "Некорректный запрос"
            } catch (e: NetworkException) {
                _error.value = "Проблемы с интернетом"
            } catch (e: NotFoundException) {
                _error.value = "Пользователь не найден"
            } catch (e: Exception) {
                _error.value = "Неизвестная ошибка"
                Log.e("InfoComicViewModel", "Unknown error", e)
            }
        }
    }
}