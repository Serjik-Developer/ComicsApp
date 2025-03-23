package com.example.texnostrelka_2025_otbor.repositories

import com.example.texnostrelka_2025_otbor.database.ComicsDatabase
import com.example.texnostrelka_2025_otbor.models.Page

class ComicsRepository(private val database: ComicsDatabase) {
    suspend fun getAllPages(comicsId: String): MutableList<Page> {
        return database.getAllPages(comicsId)
    }
    suspend fun insertPage(pageId: String, comicsId:String, rows: Int, columns: Int, number: Int) {
        database.insertPage(pageId, comicsId, rows, columns, number)
    }
    suspend fun deletePage(pageId: String) {
        database.deletePage(pageId)
    }
}