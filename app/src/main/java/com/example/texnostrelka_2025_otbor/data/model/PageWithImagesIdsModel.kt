package com.example.texnostrelka_2025_otbor.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PageWithImagesIdsModel(
    val pageModel: PageModel,
    val imageIds: List<String> // Передаем только идентификаторы изображений
) : Parcelable