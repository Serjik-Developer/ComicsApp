package com.example.texnostrelka_2025_otbor.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.texnostrelka_2025_otbor.database.PreferencesManager
import com.example.texnostrelka_2025_otbor.models.NetworkModels.ComicsNetworkModel
import com.example.texnostrelka_2025_otbor.repositories.NetworkRepository
import kotlinx.coroutines.launch

class ViewNetworkViewModel(
    private val networkRepository: NetworkRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {
    private val _comics = MutableLiveData<MutableList<ComicsNetworkModel>>(mutableListOf())
    val comics: LiveData<MutableList<ComicsNetworkModel>> get() = _comics
    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> get() = _error

    init {
        fetchComics()
    }

    fun fetchComics() {
        viewModelScope.launch {
            _error.value = null
            try {
                val token = preferencesManager.getAuthToken()
                if (token.isNullOrEmpty()) {
                    _error.value = "Auth token is missing"
                    return@launch
                }
                val result = networkRepository.getComics(token)
                _comics.value = result

                if (result.isEmpty()) {
                    _error.value = "No comics found"
                }
            } catch (e: Exception) {
                _error.value = "Network error: ${e.message}"
                Log.e("ViewNetworkVM", "Error fetching comics", e)
            }
        }
    }
}