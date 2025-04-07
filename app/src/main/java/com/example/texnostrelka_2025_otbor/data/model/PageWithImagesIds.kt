package com.example.texnostrelka_2025_otbor.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PageWithImagesIds(
    val page: Page,
    val imageIds: List<String> // Передаем только идентификаторы изображений
) : Parcelable