package com.example.texnostrelka_2025_otbor.viewmodels

import android.graphics.Bitmap
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.texnostrelka_2025_otbor.models.ImageModel
import com.example.texnostrelka_2025_otbor.repositories.ComicsRepository
import kotlinx.coroutines.launch

class AddViewModel(private val repository: ComicsRepository) : ViewModel() {
    private val _image = MutableLiveData<ImageModel>()
    val image : LiveData<ImageModel> get() = _image

    fun savePainting(imageId: String?, bitmap: Bitmap, pageId: String, cellIndex: Int) {
        viewModelScope.launch {
            if (imageId != null) {
                // Если изображение существует, обновляем его
                repository.updatePainting(imageId, bitmap)
            } else {
                // Если изображение новое, добавляем его
                repository.insertPainting(bitmap, pageId, cellIndex)
            }
        }
    }
    fun getImageById(imageId: String)  {
        viewModelScope.launch {
            val image = repository.getImageById(imageId)
            _image.value = image
        }
    }

}