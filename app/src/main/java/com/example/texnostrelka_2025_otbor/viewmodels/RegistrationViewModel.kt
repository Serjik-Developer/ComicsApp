package com.example.texnostrelka_2025_otbor.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.texnostrelka_2025_otbor.database.PreferencesManager
import com.example.texnostrelka_2025_otbor.models.authentication.AuthResponse
import com.example.texnostrelka_2025_otbor.repositories.NetworkRepository
import kotlinx.coroutines.launch

class RegistrationViewModel(
    private val networkRepository: NetworkRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel(){
    fun registrate(login: String, password: String, name: String, onResult: (Result<AuthResponse>) -> Unit) {
        viewModelScope.launch {
            try {
                val response = networkRepository.registration(login, password, name)
                preferencesManager.saveAuthToken(response.token)
                preferencesManager.saveName(response.name)
                onResult(Result.success(response))
            }
            catch (e: Exception) {
                onResult(Result.failure(e))
            }
        }
    }
}