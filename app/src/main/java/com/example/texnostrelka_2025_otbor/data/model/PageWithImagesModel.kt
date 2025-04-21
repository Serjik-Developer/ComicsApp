package com.example.texnostrelka_2025_otbor.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PageWithImagesModel(
    val pageModel: PageModel,
    val images: List<ImageModel>
) : Parcelable