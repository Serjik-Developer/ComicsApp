package com.example.texnostrelka_2025_otbor.presentation.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.texnostrelka_2025_otbor.domain.repository.ComicsRepository
import com.example.texnostrelka_2025_otbor.presentation.ui.add.AddViewModel

class AddViewModelFactory(private val repository: ComicsRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(AddViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}