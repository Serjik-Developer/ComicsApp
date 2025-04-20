package com.example.texnostrelka_2025_otbor.presentation.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.texnostrelka_2025_otbor.data.local.preferences.PreferencesManager
import com.example.texnostrelka_2025_otbor.data.remote.repository.NetworkRepository
import com.example.texnostrelka_2025_otbor.presentation.ui.comicnetwork.ComicNetworkViewModel

class ComicNetworkViewModelFactory(private val networkRepository: NetworkRepository, private val preferencesManager: PreferencesManager) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(ComicNetworkViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ComicNetworkViewModel(networkRepository, preferencesManager) as T
        }
        throw IllegalArgumentException("Unknown View Model Class")
    }
}