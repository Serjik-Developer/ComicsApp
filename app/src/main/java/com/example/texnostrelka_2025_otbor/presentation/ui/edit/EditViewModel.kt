package com.example.texnostrelka_2025_otbor.presentation.ui.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.texnostrelka_2025_otbor.data.model.PageModel
import com.example.texnostrelka_2025_otbor.domain.repository.ComicsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class EditViewModel @Inject constructor(private val repository: ComicsRepository) : ViewModel() {
    private val _pages = MutableLiveData<MutableList<PageModel>>()
    val pages:  LiveData<MutableList<PageModel>> get() = _pages


    fun fetchPages(comicsId: String) {
        viewModelScope.launch {
            val result = repository.getAllPages(comicsId)
            _pages.value = result
        }
    }
    fun addPage(rows: Int, columns: Int, number: Int, comicsId: String) {
        viewModelScope.launch {
            val pageId = UUID.randomUUID().toString()
            repository.insertPage(pageId, comicsId, rows, columns, number)
            fetchPages(comicsId)
        }
    }
    fun deletePage(pageId: String, comicsId: String) {
        viewModelScope.launch {
            repository.deletePage(pageId)
            fetchPages(comicsId)
        }
    }
}