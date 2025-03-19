package com.example.texnostrelka_2025_otbor.models

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ImageModel(
    val id: String? = null,
    val pageId: String? = null,
    val image: Bitmap? = null,
    val cellIndex: Int = -1 // Индекс ячейки
) : Parcelable