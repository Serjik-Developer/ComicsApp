package com.example.texnostrelka_2025_otbor.data.remote.model.comic.response

import com.example.texnostrelka_2025_otbor.data.remote.model.page.PageFromNetworkModel

data class ComicsCoverNetworkModel(
    val id: String? = null,
    val text: String? = null,
    val description:String? = null,
    val pages: MutableList<PageFromNetworkModel>? = null
)