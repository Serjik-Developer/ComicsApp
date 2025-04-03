package com.example.texnostrelka_2025_otbor.models.NetworkModels

import com.example.texnostrelka_2025_otbor.models.ImageModel

data class PageFromNetwork(
    val pageId:String,
    val number:Int,
    val rows:Int,
    val columns:Int,
    val images: MutableList<ImageModel>? = null
)