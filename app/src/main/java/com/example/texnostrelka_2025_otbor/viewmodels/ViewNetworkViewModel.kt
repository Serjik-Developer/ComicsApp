package com.example.texnostrelka_2025_otbor.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.texnostrelka_2025_otbor.database.PreferencesManager
import com.example.texnostrelka_2025_otbor.models.ComicsModel
import com.example.texnostrelka_2025_otbor.repositories.NetworkRepository
import kotlinx.coroutines.launch

class ViewNetworkViewModel(private val networkRepository: NetworkRepository, private val preferencesManager: PreferencesManager) : ViewModel() {
    private val _comics = MutableLiveData<MutableList<ComicsModel>>()
    val comics: LiveData<MutableList<ComicsModel>> get() = _comics

    init {
        fetchComics()
    }
    fun fetchComics() {
        viewModelScope.launch {
            try {
                val result = networkRepository.getComics(preferencesManager.getAuthToken()!!)
                _comics.value = result
            }
            catch (e: Exception) {

            }
        }
    }
}