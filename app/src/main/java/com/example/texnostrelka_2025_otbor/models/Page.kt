package com.example.texnostrelka_2025_otbor.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Page(
    val pageId: String,
    val comicsId: String,
    val number: Int,
    val rows: Int,
    val columns: Int
) : Parcelable