package com.example.texnostrelka_2025_otbor.data.local.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.example.texnostrelka_2025_otbor.data.model.ComicsModel
import com.example.texnostrelka_2025_otbor.data.model.ImageModel
import com.example.texnostrelka_2025_otbor.data.remote.model.comic.response.ComicsFromNetwork
import com.example.texnostrelka_2025_otbor.data.remote.model.image.response.ImageNetworkModel
import com.example.texnostrelka_2025_otbor.data.remote.model.page.PageFromNetwork
import com.example.texnostrelka_2025_otbor.data.model.Page
import com.example.texnostrelka_2025_otbor.presentation.utils.toBase64
import java.io.ByteArrayOutputStream
import java.util.UUID

class ComicsDatabase(context: Context) {

    private val databaseHelper = DatabaseHelper(context)
    private lateinit var image: ImageModel

    fun insert(id: String, text: String, description: String) {
        val db = databaseHelper.writableDatabase
        val values = ContentValues().apply {
            put("id", id)
            put("text", text)
            put("description", description)
        }
        val result = db.insert("comics", null, values)
        if (result == -1L) {
            Log.w("DB", "Ошибка при добавлении данных в базу данных")
        } else {
            Log.w("DB", "Данные успешно добавлены в базу данных")
        }
        db.close()
    }
    @SuppressLint("Range")
    fun getAll(): MutableList<ComicsModel> {
        val list = mutableListOf<ComicsModel>()

        val db = databaseHelper.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM comics", null)

        while (cursor.moveToNext()) {
            val id = cursor.getString(cursor.getColumnIndex("id"))
            val text = cursor.getString(cursor.getColumnIndex("text"))
            val description = cursor.getString(cursor.getColumnIndex("description"))
            val cursor1 = db.rawQuery("SELECT pageId, number FROM pages WHERE comicsId = ?", arrayOf(id))
            if (cursor1.count == 0) {
                list.add(ComicsModel(id, text, description, null))
            }
            else {
            while (cursor1.moveToNext()){
                val pageId = cursor1.getString(cursor1.getColumnIndex("pageId"))
                val number = cursor1.getInt(cursor1.getColumnIndex("number"))
                if (number == 0) {
                    val cursor2 = db.rawQuery("SELECT image FROM image WHERE pageId = ?", arrayOf(pageId))
                    if (cursor2.count == 0) list.add(ComicsModel(id, text, description, null))
                    while (cursor2.moveToNext()){
                        val imageByteArray = cursor2.getBlob(cursor2.getColumnIndex("image"))
                        val bitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)
                        list.add(ComicsModel(id, text, description, bitmap))
                    }

                }
            }}


        }
        cursor.close()
        db.close()

        return list
    }

    fun update(id: String, image: String) {
        val db = databaseHelper.writableDatabase

        val values = ContentValues().apply {

            put("image", image)
        }

        // update the data in the table
        db.update("comics", values, "id = ?", arrayOf(id.toString()))

        // close the database connection
        db.close()
    }

    fun delete(id: String) {

        val db = databaseHelper.writableDatabase

        // delete the data from the table
        db.delete("comics", "id = ?", arrayOf(id))

        // close the database connection
        db.close()
    }
    fun insertPage(pageId: String, comicsId:String, rows: Int, columns: Int, number: Int) {
        val db = databaseHelper.writableDatabase
        val values = ContentValues().apply {
            put("pageId", pageId)
            put("comicsId", comicsId)
            put("number", number)
            put("rows", rows)
            put("columns", columns)
        }

        // insert the data into the table
        db.insert("pages", null, values)

        // close the database connection
        db.close()
    }
    fun deletePage(pageId:String) {
        val db = databaseHelper.writableDatabase

        // delete the data from the table
        db.delete("pages", "pageId = ?", arrayOf(pageId))

        // close the database connection
        db.close()
    }
    @SuppressLint("Range")
    fun getAllPages(comicsId: String) : MutableList<Page> {
        val list = mutableListOf<Page>()

        val db = databaseHelper.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM pages WHERE comicsId = ?", arrayOf(comicsId))

        while (cursor.moveToNext()) {
            val pageId = cursor.getString(cursor.getColumnIndex("pageId"))
            val comicsId = cursor.getString(cursor.getColumnIndex("comicsId"))
            val number = cursor.getInt(cursor.getColumnIndex("number"))
            val rows = cursor.getInt(cursor.getColumnIndex("rows"))
            val columns = cursor.getInt(cursor.getColumnIndex("columns"))
            list.add(Page(pageId, comicsId, number, rows, columns))
        }

        // close the cursor and database connection
        cursor.close()
        db.close()

        return list
    }
    @SuppressLint("Range")
    fun getMyPage(pageId: String) : MutableList<Page> {
        val list = mutableListOf<Page>()

        val db = databaseHelper.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM pages WHERE pageId = ?", arrayOf(pageId))

        while (cursor.moveToNext()) {
            val pageId = cursor.getString(cursor.getColumnIndex("pageId"))
            val comicsId = cursor.getString(cursor.getColumnIndex("comicsId"))
            val number = cursor.getInt(cursor.getColumnIndex("number"))
            val rows = cursor.getInt(cursor.getColumnIndex("rows"))
            val columns = cursor.getInt(cursor.getColumnIndex("columns"))
            list.add(Page(pageId, comicsId, number, rows, columns))
        }

        // close the cursor and database connection
        cursor.close()
        db.close()

        return list
    }
    @SuppressLint("Range")
    fun getAllImagesOnPage(pageId: String): List<ImageModel> {
        val list = mutableListOf<ImageModel>()
        val db = databaseHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM image WHERE pageId = ? ORDER BY cellIndex", arrayOf(pageId))

        while (cursor.moveToNext()) {
            val id = cursor.getString(cursor.getColumnIndex("id"))
            val pageId = cursor.getString(cursor.getColumnIndex("pageId"))
            val cellIndex = cursor.getInt(cursor.getColumnIndex("cellIndex"))
            val imageByteArray = cursor.getBlob(cursor.getColumnIndex("image"))
            val bitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)
            list.add(ImageModel(id, pageId, bitmap, cellIndex))
        }
        cursor.close()
        db.close()
        return list
    }
    fun updatePainting(imageId: String, bitmap: Bitmap) {
        val db = databaseHelper.writableDatabase
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val values = ContentValues().apply {
            put("image", stream.toByteArray())
        }
        db.update("image", values, "id = ?", arrayOf(imageId))
        db.close()
    }
    fun insertPainting(bitmap: Bitmap, pageId: String, cellIndex: Int) {
        val db = databaseHelper.writableDatabase
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val values = ContentValues().apply {
            put("id", UUID.randomUUID().toString())
            put("pageId", pageId)
            put("cellIndex", cellIndex) // Сохраняем индекс ячейки
            put("image", stream.toByteArray())
        }
        db.insert("image", null, values)
        db.close()
    }
    @SuppressLint("Range")
    fun getImageById(imageId: String): ImageModel {
        val db = databaseHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM image WHERE id = ?", arrayOf(imageId))

        while (cursor.moveToNext()) {
            val id = cursor.getString(cursor.getColumnIndex("id"))
            val pageId = cursor.getString(cursor.getColumnIndex("pageId"))
            val imageByteArray = cursor.getBlob(cursor.getColumnIndex("image"))
            val bitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)
            cursor.close()
            image = ImageModel(id, pageId, bitmap)

        }
        return image
    }
    @SuppressLint("Range")
    fun getComicsById(id: String) : ComicsFromNetwork {
        var text = ""
        var description = ""
        val db = databaseHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM comics WHERE id = ?", arrayOf(id))
        val pages = mutableListOf<PageFromNetwork>()

        while (cursor.moveToNext()) {
            text = cursor.getString(cursor.getColumnIndex("text"))
            description = cursor.getString(cursor.getColumnIndex("description"))

            val cursorPages = db.rawQuery("SELECT * FROM pages WHERE comicsId = ? ORDER BY number", arrayOf(id))
            while (cursorPages.moveToNext()) {
                val pageId = cursorPages.getString(cursorPages.getColumnIndex("pageId"))
                val number = cursorPages.getInt(cursorPages.getColumnIndex("number"))
                val rows = cursorPages.getInt(cursorPages.getColumnIndex("rows"))
                val columns = cursorPages.getInt(cursorPages.getColumnIndex("columns"))

                // Для каждой страницы создаем новый список изображений
                val images = mutableListOf<ImageNetworkModel>()
                val cursorImages = db.rawQuery(
                    "SELECT * FROM image WHERE pageId = ? ORDER BY cellIndex",
                    arrayOf(pageId)
                )

                while (cursorImages.moveToNext()) {
                    val imageId = cursorImages.getString(cursorImages.getColumnIndex("id"))
                    val cellIndex = cursorImages.getInt(cursorImages.getColumnIndex("cellIndex"))
                    val imageByteArray = cursorImages.getBlob(cursorImages.getColumnIndex("image"))
                    val bitmapImage = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)
                    val image = bitmapImage.toBase64()
                    images.add(ImageNetworkModel(imageId, cellIndex, image))
                }
                cursorImages.close()

                pages.add(PageFromNetwork(pageId, number, rows, columns, images))
            }
            cursorPages.close()
        }
        cursor.close()
        db.close()

        return ComicsFromNetwork(id, text, description, pages)
    }
    //Удивительный факт, я идиот!
}