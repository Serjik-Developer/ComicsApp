package com.example.texnostrelka_2025_otbor.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.texnostrelka_2025_otbor.database.PreferencesManager
import com.example.texnostrelka_2025_otbor.repositories.NetworkRepository
import com.example.texnostrelka_2025_otbor.viewmodels.ViewNetworkViewModel

class ViewNetworkViewModelFactory(private val networkRepository: NetworkRepository, private val preferencesManager: PreferencesManager) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(ViewNetworkViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return ViewNetworkViewModel(networkRepository, preferencesManager) as T
        }
        throw IllegalArgumentException("Unknown View Model Class")
    }
}