package com.example.texnostrelka_2025_otbor.repositories

import com.example.texnostrelka_2025_otbor.database.ComicsDatabase
import com.example.texnostrelka_2025_otbor.models.ComicsModel
import com.example.texnostrelka_2025_otbor.models.ImageModel
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
    suspend fun getComics() :  MutableList<ComicsModel> {
        return database.getAll()
    }
    suspend fun insertComics(id: String, text: String, description: String) {
        database.insert(id, text, description)
    }
    suspend fun deleteComics(id: String) {
        database.delete(id)
    }
    suspend fun getMyPage(pageId: String) : MutableList<Page> {
        return database.getMyPage(pageId)
    }
    suspend fun getAllImagesOnPage(pageId: String) : List<ImageModel> {
        return database.getAllImagesOnPage(pageId)
    }
    suspend fun getImageById(imageId: String)  : ImageModel? {
        return database.getImageById(imageId)
    }
}