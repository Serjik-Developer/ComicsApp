package com.example.texnostrelka_2025_otbor.presentation.ui.editnetwork

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.texnostrelka_2025_otbor.data.local.preferences.PreferencesManager
import com.example.texnostrelka_2025_otbor.data.remote.model.ImageNetworkModel
import com.example.texnostrelka_2025_otbor.data.remote.repository.NetworkRepository

class EditNetworkViewModel(private val networkRepository: NetworkRepository, private val preferencesManager: PreferencesManager) : ViewModel() {

}