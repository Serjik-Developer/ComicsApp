package com.example.texnostrelka_2025_otbor.presentation.ui.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.texnostrelka_2025_otbor.data.model.Page
import com.example.texnostrelka_2025_otbor.domain.repository.ComicsRepository
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
            fetchPages()
        }
    }
    fun deletePage(pageId: String) {
        viewModelScope.launch {
            repository.deletePage(pageId)
            fetchPages()
        }
    }
}