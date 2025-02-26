package com.example.texnostrelka_2025_otbor.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PageWithImages(
    val page: Page,
    val images: List<ImageModel>
) : Parcelable