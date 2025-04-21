package com.example.texnostrelka_2025_otbor.domain.repository

import android.graphics.Bitmap
import com.example.texnostrelka_2025_otbor.data.local.database.ComicsDatabase
import com.example.texnostrelka_2025_otbor.data.model.ComicsModel
import com.example.texnostrelka_2025_otbor.data.model.ImageModel
import com.example.texnostrelka_2025_otbor.data.model.PageModel
import com.example.texnostrelka_2025_otbor.data.remote.model.comic.response.ComicsCoverNetworkModel

class ComicsRepository(private val database: ComicsDatabase) {
    suspend fun getAllPages(comicsId: String): MutableList<PageModel> {
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
    suspend fun getMyPage(pageId: String) : MutableList<PageModel> {
        return database.getMyPage(pageId)
    }
    suspend fun getAllImagesOnPage(pageId: String) : List<ImageModel> {
        return database.getAllImagesOnPage(pageId)
    }
    suspend fun getImageById(imageId: String)  : ImageModel {
        return database.getImageById(imageId)
    }
    suspend fun updatePainting(imageId: String, bitmap: Bitmap) {
        database.updatePainting(imageId, bitmap)
    }
    suspend fun insertPainting(bitmap: Bitmap, pageId: String, cellIndex: Int) {
        database.insertPainting(bitmap, pageId, cellIndex)
    }
    suspend fun getComicsById(id: String) : ComicsCoverNetworkModel {
        return database.getComicsById(id)
    }
}