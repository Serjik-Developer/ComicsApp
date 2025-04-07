package com.example.texnostrelka_2025_otbor.presentation.ui.editpage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.texnostrelka_2025_otbor.data.model.ImageModel
import com.example.texnostrelka_2025_otbor.data.model.PageWithImagesIds
import com.example.texnostrelka_2025_otbor.domain.repository.ComicsRepository
import kotlinx.coroutines.launch

class EditPageViewModel(private val pageId: String, private val repository: ComicsRepository) : ViewModel() {
    private val _pageWithImages = MutableLiveData<PageWithImagesIds>()
    val pageWithImages: LiveData<PageWithImagesIds> get() = _pageWithImages

    private val _images = MutableLiveData<List<ImageModel>>()
    val images: LiveData<List<ImageModel>> get() = _images

    fun fetchPageWithImages() {
        viewModelScope.launch {
            val page = repository.getMyPage(pageId).find { it.pageId == pageId }
            if (page != null) {
                val imageIds = repository.getAllImagesOnPage(pageId).map { it.id!! }
                _pageWithImages.value = PageWithImagesIds(page, imageIds)
            }
        }
    }

    fun fetchImages() {
        viewModelScope.launch {
            val images = repository.getAllImagesOnPage(pageId)
            _images.value = images
        }
    }
}