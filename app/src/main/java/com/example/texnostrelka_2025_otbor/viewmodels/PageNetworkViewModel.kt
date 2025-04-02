package com.example.texnostrelka_2025_otbor.viewmodels

import androidx.lifecycle.ViewModel
import com.example.texnostrelka_2025_otbor.database.PreferencesManager
import com.example.texnostrelka_2025_otbor.repositories.NetworkRepository

class PageNetworkViewModel(private val networkRepository: NetworkRepository, private val preferencesManager: PreferencesManager) : ViewModel() {

}