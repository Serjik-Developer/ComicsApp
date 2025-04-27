package com.example.texnostrelka_2025_otbor.presentation.ui.userinfo

import androidx.lifecycle.ViewModel
import com.example.texnostrelka_2025_otbor.data.local.preferences.PreferencesManager
import com.example.texnostrelka_2025_otbor.data.remote.repository.NetworkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserInfoViewModel @Inject constructor(repository: NetworkRepository, preferencesManager: PreferencesManager) : ViewModel() {
}