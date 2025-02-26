package com.example.texnostrelka_2025_otbor.models

data class Page(
    val pageId:String,
    val comicsId: String,
    val number: Int,
    val rows: Int,
    val columns: Int
)
