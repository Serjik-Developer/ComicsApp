package com.example.texnostrelka_2025_otbor.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.texnostrelka_2025_otbor.database.PreferencesManager
import com.example.texnostrelka_2025_otbor.models.AuthResponse
import com.example.texnostrelka_2025_otbor.repositories.NetworkRepository
import kotlinx.coroutines.launch
import java.net.PasswordAuthentication

class AuthViewModel(
    private val networkRepository: NetworkRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    fun authenticate(login: String, password: String, onResult: (Result<AuthResponse>) -> Unit) {
        viewModelScope.launch {
            try {
                val response = networkRepository.authenticate(login, password)
                preferencesManager.saveAuthToken(response.token)
                onResult(Result.success(response))
            } catch (e: Exception) {
                onResult(Result.failure(e))
            }
        }
    }

}