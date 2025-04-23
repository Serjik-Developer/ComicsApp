package com.example.texnostrelka_2025_otbor.presentation.ui.add

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.texnostrelka_2025_otbor.data.model.ImageModel
import com.example.texnostrelka_2025_otbor.domain.repository.ComicsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddViewModel @Inject constructor(
    private val repository: ComicsRepository
) : ViewModel() {
    private val _image = MutableLiveData<ImageModel>()
    val image : LiveData<ImageModel> get() = _image

    fun savePainting(imageId: String?, bitmap: Bitmap, pageId: String, cellIndex: Int) {
        viewModelScope.launch {
            if (imageId != null) {
                repository.updatePainting(imageId, bitmap)
            } else {
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