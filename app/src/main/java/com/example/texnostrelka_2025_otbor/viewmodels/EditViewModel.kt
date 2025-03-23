package com.example.texnostrelka_2025_otbor.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.texnostrelka_2025_otbor.database.ComicsDatabase
import com.example.texnostrelka_2025_otbor.models.Page
import com.example.texnostrelka_2025_otbor.repositories.ComicsRepository
import kotlinx.coroutines.launch
import java.util.UUID

class EditViewModel(private val comicsId: String, private val repository: ComicsRepository) : ViewModel() {
    private val _pages = MutableLiveData<MutableList<Page>>()
    val pages:  LiveData<MutableList<Page>> get() = _pages

    init {
        fetchPages()
    }
    fun fetchPages() {
        viewModelScope.launch {
            val result = repository.getAllPages(comicsId)
            _pages.value = result
        }
    }
    fun addPage(rows: Int, columns: Int, number: Int) {
        viewModelScope.launch {
            val pageId = UUID.randomUUID().toString()
            repository.insertPage(pageId, comicsId, rows, columns, number)
        }
    }
    fun deletePage(pageId: String) {
        viewModelScope.launch {
            repository.deletePage(pageId)
        }
    }
}