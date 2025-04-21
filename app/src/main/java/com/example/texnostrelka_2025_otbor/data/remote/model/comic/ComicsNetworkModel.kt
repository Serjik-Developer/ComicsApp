package com.example.texnostrelka_2025_otbor.data.remote.model.comic

import com.example.texnostrelka_2025_otbor.data.remote.model.page.PageFromNetworkModel

data class ComicsNetworkModel(
    val id: String? = null,
    val text: String? = null,
    val description:String? = null,
    val pages: MutableList<PageFromNetworkModel>? = null
)