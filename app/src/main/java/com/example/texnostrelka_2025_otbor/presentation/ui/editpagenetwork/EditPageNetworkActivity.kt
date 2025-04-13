package com.example.texnostrelka_2025_otbor.presentation.ui.editpagenetwork

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.texnostrelka_2025_otbor.data.local.preferences.PreferencesManager
import com.example.texnostrelka_2025_otbor.data.remote.repository.NetworkRepository
import com.example.texnostrelka_2025_otbor.databinding.ActivityEditNetworkBinding
import com.example.texnostrelka_2025_otbor.presentation.factory.EditPageNetworkViewModelFactory

class EditPageNetworkActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditNetworkBinding
    private val viewModel : EditPageNetworkViewModel by viewModels() {
        EditPageNetworkViewModelFactory(NetworkRepository(), PreferencesManager(this))
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditNetworkBinding.inflate(layoutInflater)
    }
}