package com.example.texnostrelka_2025_otbor.data.remote.model.page

import com.example.texnostrelka_2025_otbor.data.remote.model.image.response.ImageNetworkModel

data class PageFromNetwork(
    val pageId:String,
    val number:Int,
    val rows:Int,
    val columns:Int,
    val images: MutableList<ImageNetworkModel>? = null
)