package com.example.texnostrelka_2025_otbor.presentation.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.texnostrelka_2025_otbor.data.local.preferences.PreferencesManager
import com.example.texnostrelka_2025_otbor.data.remote.repository.NetworkRepository
import com.example.texnostrelka_2025_otbor.presentation.ui.pagenetwork.PageNetworkViewModel

class PageNetworkViewModelFactory(private val networkRepository: NetworkRepository, private val preferencesManager: PreferencesManager) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(PageNetworkViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PageNetworkViewModel(networkRepository, preferencesManager) as T
        }
        throw IllegalArgumentException("Unknown View Model Class")
    }
}