package com.example.texnostrelka_2025_otbor.models.NetworkModels

import com.example.texnostrelka_2025_otbor.models.NetworkModels.PageFromNetwork

data class ComicsFromNetwork(
    val id: String,
    val text: String? = null,
    val description:String? = null,
    val creator:String? = null,
    val pages: MutableList<PageFromNetwork>? = null
)