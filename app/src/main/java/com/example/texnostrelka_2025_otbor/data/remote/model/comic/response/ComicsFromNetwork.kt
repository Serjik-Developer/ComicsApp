package com.example.texnostrelka_2025_otbor.data.remote.model.comic.response

import com.example.texnostrelka_2025_otbor.data.remote.model.page.PageFromNetwork

data class ComicsFromNetwork(
    val id: String,
    val text: String? = null,
    val description:String? = null,
    val pages: MutableList<PageFromNetwork>? = null
)