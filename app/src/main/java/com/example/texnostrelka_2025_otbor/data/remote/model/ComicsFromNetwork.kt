package com.example.texnostrelka_2025_otbor.data.remote.model

import com.example.texnostrelka_2025_otbor.data.remote.model.PageFromNetwork

data class ComicsFromNetwork(
    val id: String,
    val text: String? = null,
    val description:String? = null,
    val pages: MutableList<PageFromNetwork>? = null
)