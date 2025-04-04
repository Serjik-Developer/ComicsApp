package com.example.texnostrelka_2025_otbor.viewmodels


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.texnostrelka_2025_otbor.models.Page
import com.example.texnostrelka_2025_otbor.repositories.ComicsRepository
import kotlinx.coroutines.launch

class ViewViewModel(private val comicsId : String, private val repository: ComicsRepository) : ViewModel() {
    private val _pages = MutableLiveData<MutableList<Page>>()
    val pages : LiveData<MutableList<Page>> get() = _pages

    fun fetchData() {
        viewModelScope.launch{
            val result = repository.getAllPages(comicsId)
            _pages.value = result
        }
    }
}