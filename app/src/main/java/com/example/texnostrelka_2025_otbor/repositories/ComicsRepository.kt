package com.example.texnostrelka_2025_otbor.repositories

import com.example.texnostrelka_2025_otbor.database.ComicsDatabase
import com.example.texnostrelka_2025_otbor.models.Page

class ComicsRepository(private val database: ComicsDatabase) {
    suspend fun getAllPages(comicsId: String): MutableList<Page> {
        return database.getAllPages(comicsId)
    }
}