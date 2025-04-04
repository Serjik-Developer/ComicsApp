package com.example.texnostrelka_2025_otbor.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.texnostrelka_2025_otbor.database.PreferencesManager
import com.example.texnostrelka_2025_otbor.models.NetworkModels.PageFromNetwork
import com.example.texnostrelka_2025_otbor.repositories.NetworkRepository
import kotlinx.coroutines.launch

class PageNetworkViewModel(private val networkRepository: NetworkRepository, private val preferencesManager: PreferencesManager) : ViewModel() {
    private val _pages = MutableLiveData<MutableList<PageFromNetwork>>()
    val pages : LiveData<MutableList<PageFromNetwork>> get() = _pages
    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> get() = _error


    fun fetchPages(id: String) {
        viewModelScope.launch {
            val token = preferencesManager.getAuthToken()
            if (token.isNullOrEmpty()) {
                _error.value = "Auth token is missing"
                return@launch
            }
            val result = networkRepository.getComicPages(id, token)
            _pages.value = result!!
        }
    }
}